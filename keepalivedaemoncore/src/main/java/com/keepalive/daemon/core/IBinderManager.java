package com.keepalive.daemon.core;

import com.keepalive.daemon.core.utils.Logger;

import java.lang.reflect.Field;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class IBinderManager {
    private static int startService;
    private static int broadcastIntent;
    private static int startInstrumentation;

    static {
        startService = invoke("TRANSACTION_startService",
                "START_SERVICE_TRANSACTION");
        broadcastIntent = invoke("TRANSACTION_broadcastIntent",
                "BROADCAST_INTENT_TRANSACTION");
        startInstrumentation = invoke("TRANSACTION_startInstrumentation",
                "START_INSTRUMENTATION_TRANSACTION");
    }

    private static int invoke(String str, String str2) {
        int result = -1;
        try {
            Class<?> cls = Class.forName("android.app.IActivityManager$Stub");
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            result = declaredField.getInt(cls);
            declaredField.setAccessible(false);
        } catch (Throwable th) {
            try {
                Class<?> cls2 = Class.forName("android.app.IActivityManager");
                Field declaredField2 = cls2.getDeclaredField(str2);
                declaredField2.setAccessible(true);
                result = declaredField2.getInt(cls2);
                declaredField2.setAccessible(false);
            } catch (Throwable th1) {
            }
        }
        Logger.d(TAG, "!! get transaction[" + str + "] : " + result);
        return result;
    }

    public static int startService() {
        return startService;
    }

    public static int broadcastIntent() {
        return broadcastIntent;
    }

    public static int startInstrumentation() {
        return startInstrumentation;
    }

    public static void thrown(Throwable th) {
        th.printStackTrace();
    }
}
