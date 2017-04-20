package com.victor.frouter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MyApplication extends MultiDexApplication implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "MyApplication";
    private static Context context;
    private static final String APATCH_PATH = "/out.apatch";
    private Map<String, String> infos = new HashMap<String, String>();

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        collectDeviceInfo(getApplicationContext());
        final String crahInfo = BuildCrashInfo(ex);
        saveCrashInfo2File(crahInfo);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        Intent intent = new Intent(MyApplication.this, WelcomeActivity.class);
//        final PendingIntent restartIntent = PendingIntent.getActivity(
//        		MyApplication.this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//        //退出程序
//        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
//                restartIntent); // 1秒钟后重启应用
        System.exit(0);
    }

    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    private String BuildCrashInfo(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }

    private String saveCrashInfo2File(String crashInfo) {
        if (null == crashInfo) return null;
        try {
            long timestamp = System.currentTimeMillis();
            String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".txt";

            String path = getSDExternalPath(context, true);
            if (null != path) {
                FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);
                fos.write(crashInfo.getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    /**
     * 返回SD卡的根目录。
     *
     * @return 如果没有sd卡 那么返回null
     */
    public static String getSDPath(boolean writable) {
        File sdDir = null;
        String root = "";
        String state = Environment.getExternalStorageState();
        if (writable && state.equals(Environment.MEDIA_MOUNTED)) {
            sdDir = Environment.getExternalStorageDirectory();
            root = sdDir.toString();
        } else if (!writable
                && (state.equals(Environment.MEDIA_MOUNTED) || state
                .equals(Environment.MEDIA_MOUNTED_READ_ONLY))) {
            sdDir = Environment.getExternalStorageDirectory();
            root = sdDir.toString();
        }
        Log.w("FileService", "root path = " + root);
        return root;
    }

    public static String getSDExternalPath(Context context, boolean writable) {
        String sdDir = getSDPath(writable);
        if (null != sdDir) {
            String path = sdDir + File.separator + "smartore";
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
            return path;
        } else {
            return context.getCacheDir().getPath();
        }
    }
}
