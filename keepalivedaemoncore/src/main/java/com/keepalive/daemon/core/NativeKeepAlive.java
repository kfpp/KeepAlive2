package com.keepalive.daemon.core;

public class NativeKeepAlive {

    public static native void lockFile(String lockFile);

    public static native void nativeSetSid();

    public static native void waitFileLock(String lockFile);

    static {
        try {
            System.loadLibrary("phonix");
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
