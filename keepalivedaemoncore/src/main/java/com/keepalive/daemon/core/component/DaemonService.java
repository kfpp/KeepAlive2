package com.keepalive.daemon.core.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.keepalive.daemon.core.notification.NotifyResidentService;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.ServiceHolder;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class DaemonService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        ServiceHolder.fireService(this, NotifyResidentService.class, true);
        startService(new Intent().setClassName(getPackageName(), AssistService1.class.getName()));
        startService(new Intent().setClassName(getPackageName(), AssistService2.class.getName()));
    }
}
