package com.keepalive.daemon.core;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * app_process [vm-options] cmd-dir [options] start-class-name [main-options]
 * <p>
 * vm-options – VM 选项
 * cmd-dir –父目录 (/system/bin)
 * options –运行的参数 :
 * –zygote
 * –start-system-server
 * –application (api>=14)
 * –nice-name=nice_proc_name (api>=14)
 * start-class-name –包含main方法的主类  (com.android.commands.am.Am)
 * main-options –启动时候传递到main方法中的参数
 */
class AppProcessRunnable implements Runnable {
    private DaemonEnv env;
    private String[] args;
    private String niceName;

    AppProcessRunnable(DaemonEnv env, String[] args, String niceName) {
        this.env = env;
        this.args = args;
        this.niceName = niceName;
    }

    @Override
    public void run() {
        DaemonEntity entity = new DaemonEntity();
        entity.niceName = niceName;
        entity.args = args;
        entity.serviceIntent = env.serviceIntent;
        entity.broadcastIntent = env.broadcastIntent;
        entity.instrumentationIntent = env.instrumentationIntent;

        List<String> list = new LinkedList<>();
        list.add("export CLASSPATH=$CLASSPATH:" + env.publicSourceDir);
        if (env.nativeLibraryDir.contains("arm64")) {
            list.add("export _LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + env.nativeLibraryDir);
            list.add("export LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + env.nativeLibraryDir);
            list.add(String.format("%s / %s %s --application --nice-name=%s &",
                    new Object[]{new File("/system/bin/app_process64").exists() ?
                            "app_process64" : "app_process", DaemonMain.class.getCanonicalName(),
                            entity.toString(), niceName}));
        } else {
            list.add("export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + env.nativeLibraryDir);
            list.add("export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + env.nativeLibraryDir);
            list.add(String.format("%s / %s %s --application --nice-name=%s &",
                    new Object[]{new File("/system/bin/app_process32").exists() ?
                            "app_process32" : "app_process", DaemonMain.class.getCanonicalName(),
                            entity.toString(), niceName}));
        }
        File file = new File(File.separator);
        String[] strArr = new String[list.size()];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = list.get(i);
        }
        ShellExecutor.execute(file, null, strArr);
    }
}
