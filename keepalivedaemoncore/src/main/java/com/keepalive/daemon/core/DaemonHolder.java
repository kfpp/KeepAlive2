package com.keepalive.daemon.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.keepalive.daemon.core.component.DaemonInstrumentation;
import com.keepalive.daemon.core.component.DaemonService;
import com.keepalive.daemon.core.daemon.DaemonReceiver;
import com.keepalive.daemon.core.utils.HiddenApiWrapper;
import com.keepalive.daemon.core.utils.Utils;

import static com.keepalive.daemon.core.Constants.COLON_SEPARATOR;
import static com.keepalive.daemon.core.Constants.PROCS;

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

    public void attach(Context base) {
        JavaDaemon.getInstance().fire(
                base,
                new Intent(base, DaemonService.class),
                new Intent(base, DaemonReceiver.class),
                new Intent(base, DaemonInstrumentation.class)
        );
    }

    public boolean inDaemonProcess() {
        String processName = Utils.getProcessName();
        for (String proc : PROCS) {
            if (!processName.endsWith(COLON_SEPARATOR + proc)) {
                continue;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean inMainProcess(Context context) {
        String processName = Utils.getProcessName();
        if (context.getPackageName().equals(processName)) {
            return true;
        }
        return false;
    }
}
