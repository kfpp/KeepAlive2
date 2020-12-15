package com.sogou;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.keepalive.daemon.core.Constants;
import com.keepalive.daemon.core.utils.NotificationUtil;
import com.keepalive.daemon.core.utils.ServiceHolder;

import java.lang.ref.WeakReference;

public class ResidentService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification noti = NotificationUtil.createNotification(
                this,
                intent.getIntExtra(Constants.NOTI_SMALL_ICON_ID, 0),
                intent.getIntExtra(Constants.NOTI_LARGE_ICON_ID, 0),
                intent.getStringExtra(Constants.NOTI_TITLE),
                intent.getStringExtra(Constants.NOTI_TEXT),
                intent.getBooleanExtra(Constants.NOTI_ONGOING, true),
                intent.getIntExtra(Constants.NOTI_PRIORITY, NotificationCompat.PRIORITY_DEFAULT),
                intent.getIntExtra(Constants.NOTI_IMPORTANCE, NotificationManager.IMPORTANCE_DEFAULT),
                intent.getStringExtra(Constants.NOTI_TICKER_TEXT),
                (PendingIntent) intent.getParcelableExtra(Constants.NOTI_PENDING_INTENT),
                (RemoteViews) intent.getParcelableExtra(Constants.NOTI_REMOTE_VIEWS)
        );

        NotificationUtil.showNotification(this, noti);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationUtil.cancelNotification();
    }

    public static class Builder {
        private WeakReference<Context> wrCtx;
        private Intent intent;

        public Builder(Context context) {
            wrCtx = new WeakReference<>(context);
            intent = new Intent(context, ResidentService.class);
        }

        public Builder smallIconId(int iconId) {
            intent.putExtra(Constants.NOTI_SMALL_ICON_ID, iconId);
            return this;
        }

        public Builder largeIconId(int iconId) {
            intent.putExtra(Constants.NOTI_LARGE_ICON_ID, iconId);
            return this;
        }

        public Builder title(CharSequence title) {
            intent.putExtra(Constants.NOTI_TITLE, title);
            return this;
        }

        public Builder text(String text) {
            intent.putExtra(Constants.NOTI_TEXT, text);
            return this;
        }

        public Builder ongoing(boolean ongoing) {
            intent.putExtra(Constants.NOTI_ONGOING, ongoing);
            return this;
        }

        public Builder priority(int priority) {
            intent.putExtra(Constants.NOTI_PRIORITY, priority);
            return this;
        }

        public Builder importance(int importance) {
            intent.putExtra(Constants.NOTI_IMPORTANCE, importance);
            return this;
        }

        public Builder tickerText(String tickerText) {
            intent.putExtra(Constants.NOTI_TICKER_TEXT, tickerText);
            return this;
        }

        public Builder pendingIntent(Parcelable pendingIntent) {
            intent.putExtra(Constants.NOTI_PENDING_INTENT, pendingIntent);
            return this;
        }

        public Builder remoteViews(Parcelable remoteViews) {
            intent.putExtra(Constants.NOTI_REMOTE_VIEWS, remoteViews);
            return this;
        }

        public Builder attach(Class<? extends Service> serviceClass) {
            intent.setClass(wrCtx.get(), serviceClass);
            return this;
        }

        public Builder stopSelf(boolean stopSelf) {
            intent.putExtra(Constants.SVC_STOP_SELF, stopSelf);
            return this;
        }

        public void fire() {
            try {
                ServiceHolder.fireService(wrCtx.get(), intent, true);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }
}
