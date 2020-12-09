package com.keepalive.daemon.core.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.keepalive.daemon.core.Constants;
import com.keepalive.daemon.core.component.DaemonBaseService;
import com.keepalive.daemon.core.daemon.MainProcessReceiver;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.NotificationUtil;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class NotifyResidentService extends DaemonBaseService {

    @Override
    public final void onCreate() {
        super.onCreate();
        sendBroadcast(new Intent(this, MainProcessReceiver.class));
        doStart();
//        stopSelf();
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ " +
                "intent: " + intent + ", startId: " + startId);

        doStartCommand(intent, flags, startId);

        Bundle bundle = intent.getBundleExtra("noti_data");
        if (bundle == null) {
            Logger.w(TAG, "Oops!!! The notification bundle data is empty.");
            return super.onStartCommand(intent, flags, startId);
        }
        Notification noti = NotificationUtil.createNotification(
                this,
                bundle.getInt(Constants.NOTI_SMALL_ICON_ID, 0),
                bundle.getInt(Constants.NOTI_LARGE_ICON_ID, 0),
                bundle.getString(Constants.NOTI_TITLE),
                bundle.getString(Constants.NOTI_TEXT),
                bundle.getBoolean(Constants.NOTI_ONGOING, true),
                bundle.getInt(Constants.NOTI_PRIORITY, NotificationCompat.PRIORITY_DEFAULT),
                bundle.getInt(Constants.NOTI_IMPORTANCE, NotificationManager.IMPORTANCE_DEFAULT),
                bundle.getString(Constants.NOTI_TICKER_TEXT),
                (PendingIntent) bundle.getParcelable(Constants.NOTI_PENDING_INTENT),
                (RemoteViews) bundle.getParcelable(Constants.NOTI_REMOTE_VIEWS)
        );
        NotificationUtil.showNotification(this, noti);
        stopSelf(startId);
//        return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public final void onDestroy() {
        doRelease();
        super.onDestroy();
    }

    protected void doStart() {
    }

    protected void doStartCommand(Intent intent, int flags, int startId) {
    }

    protected void doRelease() {
    }
}
