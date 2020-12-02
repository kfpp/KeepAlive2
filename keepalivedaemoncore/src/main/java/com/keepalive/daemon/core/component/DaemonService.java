package com.keepalive.daemon.core.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import com.keepalive.daemon.core.notification.NotifyResidentService;
import com.keepalive.daemon.core.utils.Logger;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class DaemonService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ContextCompat.startForegroundService(this,
                    new Intent().setClassName(getPackageName(), NotifyResidentService.class.getName()));
        } catch (Throwable th) {
            Logger.e(TAG, "failed to start foreground service: " + th.getMessage());
        }
        startService(new Intent().setClassName(getPackageName(), AssistService1.class.getName()));
        startService(new Intent().setClassName(getPackageName(), AssistService2.class.getName()));
    }
}
