package com.sogou;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.keepalive.daemon.core.BuildConfig;
import com.keepalive.daemon.core.DaemonHolder;
import com.keepalive.daemon.core.utils.Logger;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DaemonHolder.getInstance().attach(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        boolean inDaemonProcess = DaemonHolder.getInstance().inDaemonProcess();
        Logger.d(Logger.TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! inDaemonProcess: "
                + inDaemonProcess);
        if (!inDaemonProcess) {
            boolean inMainProcess = DaemonHolder.getInstance().inMainProcess(this);
            Logger.d(Logger.TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! inMainProcess: "
                    + inMainProcess);
            if (inMainProcess) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(this, "我胡汉山又回来啦，啊哈哈哈哈~~~~", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
