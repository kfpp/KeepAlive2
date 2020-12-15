package com.keepalive.daemon.core.daemon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.keepalive.daemon.core.notification.NotifyResidentService;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.ServiceHolder;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class DaemonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i(TAG, "@_@");
        Logger.v(TAG, "!! " + intent);
//        ServiceHolder.fireService(context, NotifyResidentService.class, true);
        ServiceHolder.bindService(context, NotifyResidentService.class);
    }
}
