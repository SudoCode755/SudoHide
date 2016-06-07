package com.sudocode.sudoHideModule;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.util.Log;

import com.sudocode.sudohide.BuildConfig;
import com.sudocode.sudohide.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMain implements IXposedHookLoadPackage, IXposedHookZygoteInit {


    private static final String ANDROID_APP_APPLICATION_PACKAGE_MANAGER_CLASS_NAME = "android.app.ApplicationPackageManager";
    private static final String X_SUDOHIDE_TAG = "XSudohide";
    private static XSharedPreferences pref;
    private static boolean isInitialized = false;

    private static void logDebug(String msg) {
        if (BuildConfig.DEBUG) Log.d(X_SUDOHIDE_TAG, msg);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        pref = new XSharedPreferences(BuildConfig.APPLICATION_ID);
        pref.makeWorldReadable();
        isInitialized = false;
    }

    private String getCallingName(Object thiz) {
        int uid = Binder.getCallingUid();

        return (String) XposedHelpers.callMethod(thiz, "getNameForUid", uid);
    }

    private boolean shouldBlock(Object thiz, String callingName, String queryName) {
        String key = callingName + ":" + queryName;
        String key_hide_from_system = queryName + Constants.KEY_HIDE_FROM_SYSTEM;

        if (pref.getBoolean(key, false)) {

            logDebug(key + " true");
            return true;
        }
        if (pref.getBoolean(key_hide_from_system, false)) {

            // block system processes like android.uid.systemui:10015
            if (callingName.contains(":")) {
                logDebug(key + " true");
                return true;
            }

            // public ApplicationInfo getApplicationInfo(String packageName, int flags, int userId)
            // need to bypass enforceCrossUserPermission
            ApplicationInfo info = (ApplicationInfo) XposedHelpers.callMethod(thiz, "ApplicationInfo", callingName,
                    0, Binder.getCallingUid());
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                logDebug(key + " true");
                return true;
            }
        }
        logDebug(key + " false");
        return false;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        logDebug("Called handleLoadPackage");
        if (!isInitialized) {
            logDebug("handleLoadPackage: Hooking methods");
            isInitialized = true;
            Class<?> clsPMS = XposedHelpers.findClass(ANDROID_APP_APPLICATION_PACKAGE_MANAGER_CLASS_NAME, lpparam.classLoader);

            XposedBridge.hookAllMethods(clsPMS, "getApplicationInfo", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    logDebug("getApplicationInfo");
                    modifyHookedMethodArguments(param);
                }
            });


            XposedBridge.hookAllMethods(clsPMS, "getPackageInfo", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    logDebug("getPackageInfo");
                    modifyHookedMethodArguments(param);
                }
            });

            XposedBridge.hookAllMethods(clsPMS, "getInstalledApplications", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    logDebug("getInstalledApplications");
                    modifyHookedMethodResult(param, new ApplicationInfoData());
                }
            });

            XposedBridge.hookAllMethods(clsPMS, "getInstalledPackages", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    logDebug("getInstalledPackages");
                    modifyHookedMethodResult(param, new PackageInfoData());
                }
            });

            XposedBridge.hookAllMethods(clsPMS, "getPackagesForUid", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    logDebug("getPackagesForUid");
                    modifyHookedMethodResult(param, new PackageNameStringData());
                }
            });

            XposedBridge.hookAllMethods(clsPMS, "queryIntentActivities", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    logDebug("queryIntentActivities");
                    modifyHookedMethodResult(param, new ResolveInfoData());
                }
            });

            XposedBridge.hookAllMethods(clsPMS, "queryIntentActivityOptions", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    logDebug("queryIntentActivityOptions");
                    modifyHookedMethodResult(param, new ResolveInfoData());

                }
            });

        }

        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            XposedHelpers.findAndHookMethod(BuildConfig.APPLICATION_ID + ".MainActivity", lpparam.classLoader, "isXposedActive", XC_MethodReplacement.returnConstant(true));
        }
    }

    private void modifyHookedMethodArguments(XC_MethodHook.MethodHookParam param) {
        pref.reload();
        if (shouldBlock(param.thisObject, getCallingName(param.thisObject), (String) param.args[0])) {
            param.args[0] = "";
        }
    }


    private <T> void modifyHookedMethodResult(XC_MethodHook.MethodHookParam param, InfoData<T> infoData) throws Throwable {

        List<T> mList = infoData.resultToList(param.getResultOrThrowable());
        if (mList == null) {
            return;
        }
        List<T> result = new ArrayList<>();
        pref.reload();
        for (T info : mList) {

            if (shouldBlock(param.thisObject, getCallingName(param.thisObject), infoData.getPackageName(info))) {
                continue;
            }
            result.add(info);
        }

        param.setResult(infoData.getResultObject(result));

    }

    private abstract class InfoData<Type> {
        abstract String getPackageName(Type info);

        public Object getResultObject(List<Type> modifiedResult) {
            return modifiedResult;
        }

        public List<Type> resultToList(Object result) {
            return (List<Type>) result;
        }
    }

    private class ResolveInfoData extends InfoData<ResolveInfo> {
        public ResolveInfoData() {
        }

        @Override
        public String getPackageName(ResolveInfo info) {
            return info.activityInfo.packageName;
        }

    }

    private class PackageInfoData extends InfoData<PackageInfo> {
        public PackageInfoData() {
        }

        @Override
        public String getPackageName(PackageInfo info) {
            return info.packageName;
        }
    }

    private class ApplicationInfoData extends InfoData<ApplicationInfo> {
        public ApplicationInfoData() {
        }

        @Override
        public String getPackageName(ApplicationInfo info) {
            return info.packageName;
        }
    }

    private class PackageNameStringData extends InfoData<String> {
        public PackageNameStringData() {
        }

        @Override
        public String getPackageName(String info) {
            return info;
        }

        @Override
        public String[] getResultObject(List<String> result) {
            return result.toArray(new String[result.size()]);
        }

        @Override
        public List<String> resultToList(Object result) {
            return result == null ? null : Arrays.asList((String[]) result);
        }

    }

}