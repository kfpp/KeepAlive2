
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
int32_t lock_file(const char *lock_file_path) {
    LOGD("try to lock file >> %s <<", lock_file_path);
    int32_t lockFileDescriptor = open(lock_file_path, O_RDONLY);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(lock_file_path, O_CREAT,
                                  S_IRUSR | S_IXUSR | S_IRWXG | S_IWGRP | S_IRWXO | S_IXOTH);
        if (lockFileDescriptor != -1) {
            LOGD("success to create file >> %s <<", lock_file_path);
        } else {
            LOGE("failed to create file >> %s <<", lock_file_path);
            return 0;
        }
    } else {
        LOGD("success to open file >> %s <<", lock_file_path);
    }
    LOGD("retry to lock file >> %s <<", lock_file_path);
    int32_t lockRet = flock(lockFileDescriptor, LOCK_EX);
    LOGD("end to flock file >> %s <<", lock_file_path);
    if (lockRet == -1) {
        LOGE("failed to lock file >> %s <<", lock_file_path);
        return 0;
    } else {
        LOGD("success to lock file >> %s <<", lock_file_path);
        return 1;
    }
}

bool wait_file_lock(const char *lock_file_path) {
    LOGD("wait to lock file >> %s <<", lock_file_path);
    int32_t lockFileDescriptor = open(lock_file_path, O_RDONLY);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(lock_file_path, O_CREAT,
                                  S_IRUSR | S_IXUSR | S_IRWXG | S_IWGRP | S_IRWXO | S_IXOTH);
        if (lockFileDescriptor != -1) {
            LOGD("success to create file >> %s <<", lock_file_path);
        } else {
            LOGE("failed to create file >> %s <<", lock_file_path);
            return false;
        }
    }

    LOGD("retry to wait for locking file >> %s <<", lock_file_path);
    while (flock(lockFileDescriptor, LOCK_EX | LOCK_NB) != -1) {
        usleep(0x3E8u);
    }
    LOGD("end to lock file >> %s <<", lock_file_path);

    LOGD("retry to lock file >> %s <<", lock_file_path);
    int32_t lockRet = flock(lockFileDescriptor, LOCK_EX);
    LOGD("end to lock file >> %s <<", lock_file_path);
    bool ret = lockRet != -1;
    if (ret) {
        LOGD("success to lock file >> %s <<", lock_file_path);
    } else {
        LOGD("failed to lock file >> %s <<", lock_file_path);
    }
    return ret;
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
    const char *lock_file_path = (char *) env->GetStringUTFChars(lockFilePath, 0);
    lock_file(lock_file_path);
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