package com.keepalive.daemon.core.daemon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.keepalive.daemon.core.utils.Logger;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class MainProcessReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.v(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! receiver: "
                + intent);
        boolean booleanExtra = intent.getBooleanExtra("main_proc_from_daemon", false);
        Logger.v(TAG, "booleanExtra: " + booleanExtra);
    }
}
