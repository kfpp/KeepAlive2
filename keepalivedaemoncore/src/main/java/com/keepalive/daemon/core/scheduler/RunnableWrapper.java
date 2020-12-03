package com.keepalive.daemon.core.scheduler;

import android.util.Log;

public class RunnableWrapper implements Runnable {
    private static final String TAG = "Runnable";
    private Runnable runnable;

    RunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Throwable t) {
            Log.e(TAG, String.format("Run error [%s] of type [%s]",
                    t.getMessage(), t.getClass().getCanonicalName()));
        }
    }
}
