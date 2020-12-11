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

    private static final boolean PRIVATE_TAG = true;
    public static final String TAG = "phonix-" + BuildConfig.VERSION_NAME;

    private static boolean isLoggable(int level) {
        return Log.isLoggable(TAG, level);
    }

    @SuppressLint("LogTagMismatch")
    public static void d(String tag, String message) {
        if (isLoggable(DEBUG)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            Log.d(tag, extraString + message, null);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void v(String tag, String message) {
        if (isLoggable(VERBOSE)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            Log.v(tag, extraString + message, null);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void i(String tag, String message) {
        if (isLoggable(INFO)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            Log.i(tag, extraString + message);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void w(String tag, String message) {
        if (isLoggable(WARN)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            Log.w(tag, extraString + message);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void e(String tag, String message) {
        if (isLoggable(ERROR)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            Log.e(tag, extraString + message);
        }
    }

    @SuppressLint("LogTagMismatch")
    public static void e(String tag, String message, Throwable e) {
        if (isLoggable(ERROR)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            Log.e(tag, extraString + message, e);
        }
    }

    private static boolean privateTag() {
        return PRIVATE_TAG;
    }

    @SuppressLint("DefaultLocale")
    private static String getMethodNameAndLineNumber() {
        StackTraceElement element[] = Thread.currentThread().getStackTrace();
        if (element != null && element.length >= 4) {
            String methodName = element[4].getMethodName();
            int lineNumber = element[4].getLineNumber();
            return String.format("%s.%s : %d ---> ", getClassName(),
                    methodName, lineNumber, Locale.CHINESE);
        }
        return null;
    }

    private static String getTag() {
        StackTraceElement element[] = Thread.currentThread().getStackTrace();
        if (element != null && element.length >= 4) {
            String className = element[4].getClassName();
            if (className == null) {
                return null;
            }
            int index = className.lastIndexOf(".");
            if (index != -1) {
                className = className.substring(index + 1);
            }
            index = className.indexOf('$');
            if (index != -1) {
                className = className.substring(0, index);
            }
            return className;
        }
        return null;
    }

    private static String getClassName() {
        StackTraceElement element[] = Thread.currentThread().getStackTrace();
        if (element != null && element.length >= 4) {
            String className = element[5].getClassName();
            if (className == null) {
                return null;
            }
            int index = className.lastIndexOf(".");
            if (index != -1) {
                className = className.substring(index + 1);
            }
            index = className.indexOf('$');
            if (index != -1) {
                className = className.substring(0, index);
            }
            return className;
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