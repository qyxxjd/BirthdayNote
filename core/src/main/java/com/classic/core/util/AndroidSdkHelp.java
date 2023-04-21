package com.classic.core.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Android sdk help
 *
 * @author Classic
 * @date 2020/4/22 10:27 AM
 */
@SuppressWarnings("JavaDoc")
public class AndroidSdkHelp {

    /**
     * 去掉在Android P上的提醒弹窗警告
     *
     * > Detected problems with API compatibility(visit g.co/dev/appcompat for more info)
     * > Android P 后谷歌限制了开发者调用非官方公开API方法或接口，用反射直接调用源码就会有这样的提示弹窗出现。
     */
    @SuppressWarnings({"PrivateApi", "unchecked", "rawtypes", "DiscouragedPrivateApi", "CatchMayIgnoreException", "SoonBlockedPrivateApi", "unused"})
    public static void closeAndroidPDialog() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return;
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {}
    }

    /**
     * 修复 WebView 多进程闪退问题
     *
     * > java.lang.RuntimeException
     * > Using WebView from more than one process at once with the same data directory is not supported
     * > Android P行为变更，不可多进程使用同一个目录webView，需要为不同进程webView设置不同目录。
     *
     * @see {https://crbug.com/558377}
     * @see {https://stackoverflow.com/questions/62079558/fatal-exception-java-lang-runtimeexceptionusing-webview-from-more-than-one-pro}
     */
    public static void fixAndroidPWebViewCrash(@Nullable Context context) {
        if (null == context) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(context);
            if (!context.getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }
    private static String getProcessName(@Nullable Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }
}
