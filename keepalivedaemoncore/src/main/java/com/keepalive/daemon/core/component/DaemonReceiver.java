package com.keepalive.daemon.core.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.keepalive.daemon.core.utils.Logger;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class DaemonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Logger.v(TAG, "!! " + intent);
    }
}
