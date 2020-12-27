package com.keepalive.daemon.core;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;

import com.keepalive.daemon.core.scheduler.FutureScheduler;
import com.keepalive.daemon.core.scheduler.SingleThreadFutureScheduler;
import com.keepalive.daemon.core.utils.Logger;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import static com.keepalive.daemon.core.utils.Logger.TAG;

public class DaemonMain {
    public DaemonEntity entity;

    private Parcel serviceData;
    private Parcel broadcastData;
    private Parcel instrumentationData;
    private IBinder binder;

    private static volatile FutureScheduler futureScheduler;

    private DaemonMain(DaemonEntity entity) {
        this.entity = entity;
    }

    public static void main(String[] args) {
        Logger.i(TAG, "@_@");
        if (futureScheduler == null) {
            synchronized (DaemonMain.class) {
                if (futureScheduler == null) {
                    futureScheduler = new SingleThreadFutureScheduler(
                            "daemonmain-holder",
                            Thread.MAX_PRIORITY,
                            true
                    );
                }
            }
        }

        DaemonEntity entity = DaemonEntity.create(args[0]);
        if (entity != null) {
            new DaemonMain(entity).execute();
        }
        Process.killProcess(Process.myPid());
    }

    private void execute() {
        try {
            initAmsBinder();
            assembleParcel();
            NativeKeepAlive.nativeSetSid();
            try {
                Logger.v(TAG, ">>>> invoke setArgV0(): niceName=" + entity.niceName);
                Process.class.getMethod("setArgV0", new Class[]{String.class}).invoke(null,
                        new Object[]{entity.niceName});
            } catch (Throwable th) {
                Logger.e(TAG, "Failed to invoke setArgV0(): ", th);
            }
            for (int i = 1; i < entity.args.length; i++) {
                futureScheduler.scheduleFuture(new DaemonRunnable(this, i), 0);
            }
            String lockFile = entity.args[0];
            Logger.v(TAG, "[NativeKeepAlive.waitFileLock] wait file lock begin: " + lockFile);
            NativeKeepAlive.waitFileLock(lockFile);
            Logger.v(TAG, "[NativeKeepAlive.waitFileLock] wait file lock end: " + lockFile);
            startService();
            broadcastIntent();
            startInstrumentation();
            Logger.v(TAG, "[" + entity.niceName + "] start android finish");
        } catch (Throwable th) {
            IBinderManager.thrown(th);
        }
    }

    public void startInstrumentation() {
        if (instrumentationData != null) {
            try {
                binder.transact(IBinderManager.startInstrumentation(), instrumentationData, null, 1);
            } catch (Throwable th) {
                IBinderManager.thrown(th);
            }
        }
    }

    public void broadcastIntent() {
        if (broadcastData != null) {
            try {
                binder.transact(IBinderManager.broadcastIntent(), broadcastData, null, 1);
            } catch (Throwable th) {
                IBinderManager.thrown(th);
            }
        }
    }

    public void startService() {
        if (serviceData != null) {
            try {
                binder.transact(IBinderManager.startService(), serviceData, null, 1);
            } catch (Throwable th) {
                IBinderManager.thrown(th);
            }
        }
    }

    private void assembleParcel() {
        assembleServiceParcel();
        assembleBroadcastParcel();
        assembleInstrumentationParcel();
    }

    /**
     * public ComponentName startService(IApplicationThread caller, Intent service,
     *              String resolvedType, String callingPackage, int userId) throws RemoteException {
     *      Parcel data = Parcel.obtain();
     *      Parcel reply = Parcel.obtain();
     *      data.writeInterfaceToken(IActivityManager.descriptor);
     *      data.writeStrongBinder(caller != null ? caller.asBinder() : null);
     *      service.writeToParcel(data, 0);
     *      data.writeString(resolvedType);
     *      data.writeString(callingPackage);
     *      data.writeInt(userId);
     *      mRemote.transact(START_SERVICE_TRANSACTION, data, reply, 0);
     *      reply.readException();
     *      ComponentName res = ComponentName.readFromParcel(reply);
     *      data.recycle();
     *      reply.recycle();
     *      return res;
     * }
     */
    private void assembleServiceParcel() {
        Logger.d(TAG, "@_@");
        serviceData = Parcel.obtain();
        serviceData.writeInterfaceToken("android.app.IActivityManager");
        serviceData.writeStrongBinder(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceData.writeInt(1);
        }
        entity.serviceIntent.writeToParcel(serviceData, 0);
        serviceData.writeString(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceData.writeInt(0); // 0 : WTF!!!
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            serviceData.writeString(entity.serviceIntent.getComponent().getPackageName());
        }
        serviceData.writeInt(0);
    }

    @SuppressLint("WrongConstant")
    private void assembleBroadcastParcel() {
        Logger.d(TAG, "@_@");
        broadcastData = Parcel.obtain();
        broadcastData.writeInterfaceToken("android.app.IActivityManager");
        broadcastData.writeStrongBinder(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            broadcastData.writeInt(1);
        }
        entity.broadcastIntent.setFlags(32);
        entity.broadcastIntent.writeToParcel(broadcastData, 0);
        broadcastData.writeString(null);
        broadcastData.writeStrongBinder(null);
        broadcastData.writeInt(-1);
        broadcastData.writeString(null);
        broadcastData.writeInt(0);
        broadcastData.writeStringArray(null);
        broadcastData.writeInt(-1);
        broadcastData.writeInt(0);
        broadcastData.writeInt(0);
        broadcastData.writeInt(0);
        broadcastData.writeInt(0);
    }

    private void assembleInstrumentationParcel() {
        Logger.d(TAG, "@_@");
        instrumentationData = Parcel.obtain();
        instrumentationData.writeInterfaceToken("android.app.IActivityManager");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instrumentationData.writeInt(1);
        }
        entity.instrumentationIntent.getComponent().writeToParcel(instrumentationData, 0);
        instrumentationData.writeString(null);
        instrumentationData.writeInt(0);
        instrumentationData.writeInt(0);
        instrumentationData.writeStrongBinder(null);
        instrumentationData.writeStrongBinder(null);
        instrumentationData.writeInt(0);
        instrumentationData.writeString(null);
    }

    private void initAmsBinder() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object invoke = cls.getMethod("getDefault", new Class[0]).invoke(cls, new Object[0]);
            Field field = invoke.getClass().getDeclaredField("mRemote");
            field.setAccessible(true);
            binder = (IBinder) field.get(invoke);
            field.setAccessible(false);
            Logger.v(TAG, "initAmsBinder: mRemote == iBinder " + binder);
        } catch (Throwable th) {
            IBinderManager.thrown(th);
        }

        if (binder == null) {
            try {
                binder = (IBinder) Class.forName("android.os.ServiceManager").getMethod(
                        "getService", new Class[]{String.class}).invoke(null,
                        new Object[]{"activity"});
            } catch (Throwable th) {
                IBinderManager.thrown(th);
            }
        }
    }

    static class DaemonRunnable implements Runnable {
        private WeakReference<DaemonMain> thiz;
        private String lockFile;
        private String niceName;

        private DaemonRunnable(DaemonMain thiz, int index) {
            this.thiz = new WeakReference<>(thiz);
            this.lockFile = this.thiz.get().entity.args[index];
            this.niceName = this.thiz.get().entity.niceName;
        }

        @Override
        public void run() {
            Logger.v(TAG, "[NativeKeepAlive.waitFileLock] wait file lock begin: " + lockFile);
            NativeKeepAlive.waitFileLock(lockFile);
            Logger.v(TAG, "[NativeKeepAlive.waitFileLock] wait file lock end: " + lockFile);
            thiz.get().startService();
            thiz.get().broadcastIntent();
            thiz.get().startInstrumentation();
            Logger.v(TAG, "[" + niceName + "] start android finish");
        }
    }
}
