package com.keepalive.daemon.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.keepalive.daemon.core.component.DaemonInstrumentation;
import com.keepalive.daemon.core.component.DaemonService;
import com.keepalive.daemon.core.daemon.DaemonReceiver;
import com.keepalive.daemon.core.notification.NotifyResidentService;
import com.keepalive.daemon.core.utils.HiddenApiWrapper;
import com.keepalive.daemon.core.utils.ServiceHolder;

public class DaemonHolder {

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiWrapper.exemptAll();
        }
    }

    private DaemonHolder() {
    }

    private static class Holder {
        private volatile static DaemonHolder INSTANCE = new DaemonHolder();
    }

    public static DaemonHolder getInstance() {
        return Holder.INSTANCE;
    }

    public void attach(Context context) {
        if (inDaemonProcess()) {
            JavaDaemon.getInstance().fire(
                    context,
                    new Intent(context, DaemonService.class),
                    new Intent(context, DaemonReceiver.class),
                    new Intent(context, DaemonInstrumentation.class)
            );
        }

        if (inMainProcess(context)) {
//            ServiceHolder.fireService(context, NotifyResidentService.class, true);
            ServiceHolder.bindService(context, NotifyResidentService.class);
        }
    }

    public boolean inDaemonProcess() {
        return JavaDaemon.getInstance().inDaemonProcess();
    }

    public boolean inMainProcess(Context context) {
        return JavaDaemon.getInstance().inMainProcess(context);
    }
}
