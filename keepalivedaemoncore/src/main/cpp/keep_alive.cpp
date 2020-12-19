/**
 * Linux文件锁flock原理
 *
 * 在多个进程同时操作同一份文件的过程中很容易导致文件中的数据混乱，需要锁操作来保证数据的完整性，
 * 这里介绍的针对文件的锁称之为“文件锁”——flock。
 *
 * flock，建议性锁，不具备强制性。
 *
 * 一个进程使用flock将文件锁住，另一个进程可以直接操作正在被锁的文件、修改文件中的数据。
 * 原因在于flock只是用于检测文件是否被加锁，针对文件已经被加锁另一个进程写入数据的情况，
 * 内核不会阻止这个进程的写入操作，也就是建议性锁的内核处理策略。
 *
 * flock主要三种操作类型：
 * LOCK_SH，共享锁，多个进程可以使用同一把锁，常被用作读共享锁；
 * LOCK_EX，排他锁，同时只允许一个进程使用，常被用作写锁；
 * LOCK_UN，释放锁。
 *
 * 进程使用flock尝试锁文件时，如果文件已经被其他进程锁住，进程会被阻塞直到锁被释放掉，或者在调用flock的时候，
 * 采用LOCK_NB参数，在尝试锁住该文件的时候，发现已经被其他服务锁住，会返回错误，errno错误码为EWOULDBLOCK。
 * 即提供两种工作模式：阻塞与非阻塞类型。
 *
 * 服务会阻塞等待直到锁被释放：
 * flock(lockfd, LOCK_EX)
 *
 * 服务会返回错误发现文件已经被锁住时：
 * iret = flock(lockfd, LOCK_EX|LOCK_NB)
 * 同时ret = -1, errno = EWOULDBLOCK
 *
 * flock锁的释放非常具有特色，即可调用LOCK_UN参数来释放文件锁，
 * 也可以通过关闭fd的方式来释放文件锁（flock的第一个参数是fd），意味着flock会随着进程的关闭而被自动释放掉。
 *
 * flock其中的一个使用场景为：检测进程是否已经存在。
 */

#include <jni.h>
#include <sys/wait.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/file.h>
#include <linux/android/binder.h>
#include <sys/mman.h>
#include "common.h"

extern "C" {
int32_t open_file(const char *pfile) {
//    LOGD("try to open file >> %s <<", pfile);
    int32_t lockfd = open(pfile, O_RDONLY);
    if (lockfd == -1) {
        lockfd = open(pfile, O_CREAT,
                      S_IRUSR | S_IXUSR | S_IRWXG | S_IWGRP | S_IRWXO | S_IXOTH);
        if (lockfd == -1) {
            LOGE("Ooooops!!!!!! failed to create file >> %s <<", pfile);
        } else {
//            LOGD("success to create file >> %s <<", pfile);
        }
    } else {
//        LOGD("success to open file >> %s <<", pfile);
    }
    return lockfd;
}

int32_t lock_file(const char *pfile) {
    int32_t lockfd = open_file(pfile);
    if (lockfd == -1) {
        LOGE("Ooooops!!!!!! failed to open file >> %s <<", pfile);
        return lockfd + 1;
    }

    LOGD("try to lock [-ex] file >> %s <<", pfile);
    int32_t iret = flock(lockfd, LOCK_EX);
    bool result = iret != -1;
    if (result) {
        LOGD("success to lock file >> %s <<", pfile);
    } else {
        LOGE("Ooooops!!!!!! failed to lock file >> %s <<", pfile);
    }
    return iret + 1;
}

bool wait_file_lock(const char *pfile) {
    int32_t lockfd = open_file(pfile);
    if (lockfd == -1) {
        LOGE("Ooooops!!!!!! failed to open file >> %s <<", pfile);
        return false;
    }

    LOGD("check file locking [-ex|-nb] status >> %s <<", pfile);
    bool locked = true;
    bool exceed_1000 = false;
    bool exceed_10000 = false;
    uint64_t retry = 0;
    while (flock(lockfd, LOCK_EX | LOCK_NB) != -1) {
        ++retry;
        if (retry > 10000) { // > 10ms
            if (!exceed_10000) {
                LOGW("?????? retry to wait for locking file >> %s << exceed %d times, so break it",
                     pfile, retry - 1);
            }
            exceed_10000 = true;
            locked = false;
            break;
        } else if (retry > 1000) { // > 1ms
            if (!exceed_1000) {
                LOGW("?????? retry to wait for locking file >> %s << exceed %d times, so relock it again",
                     pfile, retry - 1);
            }
            exceed_1000 = true;
            flock(lockfd, LOCK_EX);
        }
        usleep(0x3E8u);
    }

    if (locked) {
        LOGD("file has been locked >> %s <<", pfile);
    } else {
        LOGW("?????? file is not locked >> %s <<", pfile);
    }

    LOGD("retry to lock [-ex] file & wait... >> %s <<", pfile);
    int32_t iret = flock(lockfd, LOCK_EX);
    bool result = iret != -1;
    if (result) {
        LOGD("success to lock file >> %s <<", pfile);
    } else {
        LOGE("Ooooops!!!!!! failed to lock file >> %s <<", pfile);
    }
    return iret;
}

void keep_alive_set_sid(JNIEnv *env, jclass jclazz) {
    pid_t old_pid = getpid();
    LOGD("------ PID: %d, PPID: %d, PGID: %d, SID: %d", old_pid, getppid(), getpgrp(),
         getsid(old_pid));

    setsid();

    pid_t new_pid = getpid();
    LOGD("++++++ PID: %d, PPID: %d, PGID: %d, SID: %d", new_pid, getppid(), getpgrp(),
         getsid(new_pid));
}

void keep_alive_wait_file_lock(JNIEnv *env, jclass jclazz, jstring path) {
    const char *file_path = (char *) env->GetStringUTFChars(path, 0);
    wait_file_lock(file_path);
}

void keep_alive_lock_file(JNIEnv *env, jclass jclazz, jstring lockFilePath) {
    const char *pfile = (char *) env->GetStringUTFChars(lockFilePath, 0);
    lock_file(pfile);
}

}

static JNINativeMethod methods[] = {
        {"lockFile",     "(Ljava/lang/String;)V", (void *) keep_alive_lock_file},
        {"nativeSetSid", "()V",                   (void *) keep_alive_set_sid},
        {"waitFileLock", "(Ljava/lang/String;)V", (void *) keep_alive_wait_file_lock}
};

static int32_t registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                     int32_t numMethods) {
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }

    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;

    LOGI("###### JNI_OnLoad ######");

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    if (!registerNativeMethods(env, JAVA_CLASS, methods, sizeof(methods) / sizeof(methods[0]))) {
        return -1;
    }

    return JNI_VERSION_1_6;
}