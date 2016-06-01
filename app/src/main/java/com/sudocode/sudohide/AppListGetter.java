package com.sudocode.sudohide;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//singleton

public class AppListGetter {


    private List<ApplicationData> userApps = null;
    private List<ApplicationData> allApps = null;

    private final Context activity;

    private static AppListGetter instance = null;

    public static AppListGetter getInstance(Context thisActivity) {
        if (instance == null) {
            instance = new AppListGetter(thisActivity);
        }
        return instance;
    }


    private AppListGetter(Context thisActivity) {
        activity = thisActivity;
    }


    private List<ApplicationData> getUserApps() {
        if (userApps == null) {

            PackageManager pm = activity.getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);


            userApps = new ArrayList<>();
            for (ApplicationInfo info : apps) {

                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                    userApps.add(new ApplicationData(pm.getApplicationLabel(info).toString(), info.packageName, pm.getApplicationIcon(info)));
                }
            }

            SortList(userApps);
        }
        return userApps;
    }


    public List<ApplicationData> getAllApps() {
        if (allApps == null) {

            PackageManager pm = activity.getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);


            allApps = new ArrayList<>();
            for (ApplicationInfo info : apps) {

                allApps.add(new ApplicationData(pm.getApplicationLabel(info).toString(), info.packageName, pm.getApplicationIcon(info)));

            }

            SortList(allApps);
        }
        return allApps;
    }

    private void SortList(List<ApplicationData> appList) {
        Collections.sort(appList);
    }


    public List<ApplicationData> getAppList(boolean showSystemApps) {

        return showSystemApps ? getAllApps() : getUserApps();
    }
}
