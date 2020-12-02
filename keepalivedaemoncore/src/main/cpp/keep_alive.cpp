
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
int lock_file(const char *lock_file_path) {
    LOGD("try to lock file >> %s <<", lock_file_path);
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    LOGD("open [%s] : %d", lock_file_path, lockFileDescriptor);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(lock_file_path, O_CREAT,
                                  S_IRUSR | S_IXUSR | S_IRWXG | S_IWGRP | S_IRWXO | S_IXOTH);
        LOGD("open [%s] : %d", lock_file_path, lockFileDescriptor);
    }
    int lockRet = flock(lockFileDescriptor, LOCK_EX);
    LOGD("flock [%s:%d] : %d", lock_file_path, lockFileDescriptor, lockRet);
    if (lockRet == -1) {
        LOGE("failed to lock file >> %s <<", lock_file_path);
        return 0;
    } else {
        LOGD("success to lock file >> %s <<", lock_file_path);
        return 1;
    }
}

bool wait_file_lock(const char *lock_file_path) {
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    if (lockFileDescriptor == -1)
        lockFileDescriptor = open(lock_file_path, O_CREAT,
                                  S_IRUSR | S_IXUSR | S_IRWXG | S_IWGRP | S_IRWXO | S_IXOTH);
    while (flock(lockFileDescriptor, LOCK_EX | LOCK_NB) != -1) {
        usleep(0x3E8u);
    }

    LOGD("retry to lock file >> %s << %d", lock_file_path, -1);

    int err_no = flock(lockFileDescriptor, LOCK_EX);
    LOGD("flock [%s:%d] : %d", lock_file_path, lockFileDescriptor, err_no);
    bool ret = err_no != -1;
    if (ret) {
        LOGD("success to lock file >> %s <<", lock_file_path);
    } else {
        LOGD("failed to lock file >> %s <<", lock_file_path);
    }
    return ret;
}

void keep_alive_set_sid(JNIEnv *env, jclass jclazz) {
    LOGD("------ original child process PID is: %d", getpid());
    LOGD("------ original child process PGID is: %d", getpgrp());
    LOGD("------ original child process SID is: %d", getsid(0));
    setsid();
    LOGD("++++++ changed child process PID is: %d", getpid());
    LOGD("++++++ changed child process PGID is: %d", getpgrp());
    LOGD("++++++ changed child process SID is: %d", getsid(0));
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

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
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