package com.keepalive.daemon.core.utils;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;

import androidx.core.content.ContextCompat;

import com.keepalive.daemon.core.Constants;
import com.keepalive.daemon.core.notification.NotifyResidentService;

import java.lang.ref.WeakReference;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class ServiceHolder {

    public static void fireService(Context context, Class<? extends Service> clazz, boolean isForeground) {
        Logger.i(TAG, "call fireService(): service=" + clazz.getName() + ", isForeground=" + isForeground);
        Intent intent = new Intent(context, clazz);
        try {
            if (isForeground) {
//                intent.setAction(context.getPackageName() + ".resident.START_FOREGROUND_SERVICE");
                ContextCompat.startForegroundService(context, intent);
            } else {
                context.startService(intent);
            }
        } catch (Throwable t) {
            Logger.e(TAG, "Failed to start service " + clazz.getCanonicalName(), t);
            context.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    Logger.d(TAG, "ComponentName: " + componentName + ", IBinder: " + iBinder);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                }
            }, 0);
        }
    }

    public static class Builder {
        private WeakReference<Context> wrCtx;
        private Bundle bundle;
        private Class<? extends NotifyResidentService> serviceClass;

        public Builder(Context context) {
            wrCtx = new WeakReference<>(context);
            bundle = new Bundle();
        }

        public Builder smallIconId(int iconId) {
            bundle.putInt(Constants.NOTI_SMALL_ICON_ID, iconId);
            return this;
        }

        public Builder largeIconId(int iconId) {
            bundle.putInt(Constants.NOTI_LARGE_ICON_ID, iconId);
            return this;
        }

        public Builder title(CharSequence title) {
            bundle.putCharSequence(Constants.NOTI_TITLE, title);
            return this;
        }

        public Builder text(String text) {
            bundle.putString(Constants.NOTI_TEXT, text);
            return this;
        }

        public Builder ongoing(boolean ongoing) {
            bundle.putBoolean(Constants.NOTI_ONGOING, ongoing);
            return this;
        }

        public Builder priority(int priority) {
            bundle.putInt(Constants.NOTI_PRIORITY, priority);
            return this;
        }

        public Builder importance(int importance) {
            bundle.putInt(Constants.NOTI_IMPORTANCE, importance);
            return this;
        }

        public Builder tickerText(String tickerText) {
            bundle.putString(Constants.NOTI_TICKER_TEXT, tickerText);
            return this;
        }

        public Builder pendingIntent(Parcelable pendingIntent) {
            bundle.putParcelable(Constants.NOTI_PENDING_INTENT, pendingIntent);
            return this;
        }

        public Builder remoteViews(Parcelable remoteViews) {
            bundle.putParcelable(Constants.NOTI_REMOTE_VIEWS, remoteViews);
            return this;
        }

        public Builder attach(Class<? extends NotifyResidentService> serviceClass) {
            this.serviceClass = serviceClass;
            return this;
        }

        public void fire() {
            try {
                Intent intent = new Intent(wrCtx.get(),
                        serviceClass == null ? NotifyResidentService.class : serviceClass);
                intent.putExtra("noti_data", bundle);
                ContextCompat.startForegroundService(wrCtx.get(), intent);
            } catch (Throwable th) {
                Logger.e(TAG, "Failed to start service: " + th.getMessage());
            }
        }
    }
}
