package com.keepalive.daemon.core;

import android.content.Intent;

public class DaemonEnv {
    public String processName;
    public String publicSourceDir;

    public String nativeLibraryDir;
    public Intent serviceIntent;
    public Intent broadcastIntent;
    public Intent instrumentationIntent;

    @Override
    public String toString() {
        return "DaemonEnv{" +
                "processName='" + processName + '\'' +
                ", publicSourceDir='" + publicSourceDir + '\'' +
                ", nativeLibraryDir='" + nativeLibraryDir + '\'' +
                ", serviceIntent=" + serviceIntent +
                ", broadcastIntent=" + broadcastIntent +
                ", instrumentationIntent=" + instrumentationIntent +
                '}';
    }
}
