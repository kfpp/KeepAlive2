package com.keepalive.daemon.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import com.keepalive.daemon.core.scheduler.FutureScheduler;
import com.keepalive.daemon.core.scheduler.SingleThreadFutureScheduler;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.keepalive.daemon.core.Constants.COLON_SEPARATOR;
import static com.keepalive.daemon.core.Constants.PROCS;
import static com.keepalive.daemon.core.utils.Logger.TAG;

public class JavaDaemon {
    private volatile static FutureScheduler scheduler;
    private static final String lockFileSuffix = "d";

    private JavaDaemon() {
        if (scheduler == null) {
            synchronized (JavaDaemon.class) {
                if (scheduler == null) {
                    scheduler = new SingleThreadFutureScheduler("javadaemon-holder", false);
                }
            }
        }
    }

    private static class Holder {
        private volatile static JavaDaemon INSTANCE = new JavaDaemon();
    }

    public static JavaDaemon getInstance() {
        return Holder.INSTANCE;
    }

    public void fire(Context context, Intent serviceIntent, Intent broadcastIntent,
                     Intent instrumentationIntent) {
        DaemonEnv env = new DaemonEnv();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        env.publicSourceDir = applicationInfo.publicSourceDir;
        env.nativeLibraryDir = applicationInfo.nativeLibraryDir;
        env.serviceIntent = serviceIntent;
        env.broadcastIntent = broadcastIntent;
        env.instrumentationIntent = instrumentationIntent;
        env.processName = Utils.getProcessName();

        fire(context, env, PROCS);
    }

    private void fire(Context context, DaemonEnv env, String[] args) {
        Logger.i(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! fire(): "
                + "env=" + env + ", args=" + Arrays.toString(args));
        boolean isHitted = false;
        String processName = env.processName;
        if (processName.startsWith(context.getPackageName()) && processName.contains(COLON_SEPARATOR)) {
            String proc = processName.substring(processName.lastIndexOf(COLON_SEPARATOR) + 1);
            List<String> list = new ArrayList();
            for (String arg : args) {
                if (arg.equals(proc)) {
                    isHitted = true;
                } else {
                    list.add(arg);
                }
            }
            if (isHitted) {
                Logger.v(TAG, "[NativeKeepAlive.lockFile] begin: " + proc);
                NativeKeepAlive.lockFile(context.getFilesDir() + File.separator + proc + lockFileSuffix);
                Logger.v(TAG, "[NativeKeepAlive.lockFile] end: " + proc);
                String[] strArr = new String[list.size()];
                for (int i = 0; i < strArr.length; i++) {
                    strArr[i] = context.getFilesDir() + File.separator + list.get(i) + lockFileSuffix;
                }
                scheduler.scheduleFuture(new AppProcessRunnable(env, strArr, "daemon"), 0);
            }
        }
    }
}
