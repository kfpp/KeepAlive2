package com.keepalive.daemon.core.utils;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class ServiceHolder {

    public static void fireService(Context context, Class<? extends Service> clazz, boolean isForeground) {
        Intent intent = new Intent(context, clazz);
        fireService(context, intent, isForeground);
    }

    public static void fireService(Context context, Intent intent, boolean isForeground) {
        try {
            Logger.d(TAG, "!! " + (isForeground ? "startForegroundService: " : "startService: ")
                    + intent);
            if (isForeground) {
                ContextCompat.startForegroundService(context, intent);
            } else {
                context.startService(intent);
            }
        } catch (Throwable t) {
            Logger.e(TAG, "Failed to start service: ", t);
            bindService(context, intent);
        }
    }

    public static void bindService(Context context, Class<? extends Service> clazz) {
        Intent intent = new Intent(context, clazz);
        bindService(context, intent);
    }

    public static void bindService(Context context, Intent intent) {
        try {
            Logger.i(TAG, "!! " + intent);
            context.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    Logger.d(TAG, "<<-->> : " + componentName + ", IBinder: " + iBinder);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                }
            }, /*0*/Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT);
        } catch (Throwable t) {
            Logger.e(TAG, "Failed to start service: ", t);
        }
    }
}
