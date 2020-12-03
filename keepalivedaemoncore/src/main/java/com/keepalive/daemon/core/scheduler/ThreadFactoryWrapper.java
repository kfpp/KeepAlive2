package com.keepalive.daemon.core.scheduler;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadFactoryWrapper implements ThreadFactory {
    private static final String TAG = "ThreadFactory";
    private String source;
    private int priority;

    public ThreadFactoryWrapper(String source, int priority) {
        this.source = source;
        this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);

        thread.setPriority(priority);
        thread.setName("Core-" + thread.getName() + "-" + source);
        thread.setDaemon(true);

        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread th, Throwable tr) {
                Log.e(TAG, String.format("Thread [%s] with error [%s]",
                        th.getName(), tr.getMessage()));
            }
        });

        return thread;
    }
}
