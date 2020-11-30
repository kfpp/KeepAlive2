package com.sogou;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.keepalive.daemon.core.DaemonHolder;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.ServiceHolder;
import com.sogou.daemon.R;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Logger.v(Logger.TAG, "attachBaseContext");
        DaemonHolder.getInstance().attach(base, this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        new ServiceHolder.Builder(this)
                .smallIconId(R.drawable.notify_panel_notification_icon_bg)
                .title(getApplicationInfo().loadLabel(getPackageManager()))
                .text("Hello, world!")
                .importance(NotificationManager.IMPORTANCE_NONE)
                .pendingIntent(pi)
                .fire();
    }
}
