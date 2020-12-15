package com.keepalive.daemon.core.component;

import android.app.Service;

import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.ServiceHolder;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public abstract class DaemonBaseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(TAG, "@_@");
//        ServiceHolder.fireService(this, AssistService1.class, false);
//        ServiceHolder.fireService(this, AssistService2.class, false);
//        ServiceHolder.fireService(this, DaemonService.class, false);
        ServiceHolder.bindService(this, AssistService1.class);
        ServiceHolder.bindService(this, AssistService2.class);
        ServiceHolder.bindService(this, DaemonService.class);
    }
}
