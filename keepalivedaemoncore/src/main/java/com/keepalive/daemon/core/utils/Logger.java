package com.keepalive.daemon.core.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.keepalive.daemon.core.BuildConfig;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static android.util.Log.WARN;

public class Logger {

    public static final String TAG = "phonix-" + BuildConfig.VERSION_NAME;

    private static boolean isLoggable(int level) {
        return BuildConfig.DEBUG || Log.isLoggable(TAG, level);
    }

    @SuppressLint("LogTagMismatch")
    public static void d(String tag, String message) {
        if (isLoggable(DEBUG)) {
            String extraString = getMethodNameAndLineNumber();
            Log.d(tag, extraString + message, null);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void v(String tag, String message) {
        if (isLoggable(VERBOSE)) {
            String extraString = getMethodNameAndLineNumber();
            Log.v(tag, extraString + message, null);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void i(String tag, String message) {
        if (isLoggable(INFO)) {
            String extraString = getMethodNameAndLineNumber();
            Log.i(tag, extraString + message);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void w(String tag, String message) {
        if (isLoggable(WARN)) {
            String extraString = getMethodNameAndLineNumber();
            Log.w(tag, extraString + message);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void e(String tag, String message) {
        if (isLoggable(ERROR)) {
            String extraString = getMethodNameAndLineNumber();
            Log.e(tag, extraString + message);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void e(String tag, String message, Throwable e) {
        if (isLoggable(ERROR)) {
            String extraString = getMethodNameAndLineNumber();
            Log.e(tag, extraString + message, e);
        }
    }

    @SuppressLint("DefaultLocale")
    private static String getMethodNameAndLineNumber() {
        Throwable throwable = new Throwable();
        StackTraceElement[] stacks = throwable.fillInStackTrace().getStackTrace();
        StackTraceElement element = stacks[2];
        if (element != null) {
            String className = element.getClassName();
            String methodName = element.getMethodName();
            int lineNumber = element.getLineNumber();
            return String.format("%s.%s : %d ---> ", className.substring(className.lastIndexOf(".") + 1)
                    , methodName, lineNumber);
        }
        return null;
    }

    public static void recordOperation(String operation) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        String time = sdf.format(new Date(System.currentTimeMillis())) + " : ";
        try {
            File external = Environment.getExternalStorageDirectory();
            String dir = external.getAbsoluteFile() + File.separator
                    + "mysee/log";
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            if (external != null) {
                FileWriter fp = new FileWriter(
                        dir + File.separator + "log.txt", true);
                fp.write(time + operation + "\n");
                fp.close();
            }
        } catch (Exception e) {
            Log.d(TAG, "error : " + e);
        }
    }
}