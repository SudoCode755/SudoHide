package com.sudocode.sudohide.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.sudocode.sudohide.ApplicationData;
import com.sudocode.sudohide.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//singleton

public class AppListGetter extends AsyncTask<Void, Void, Void> {


    private static AppListGetter instance = null;
    private static List<ApplicationData> userApps = null;
    private static List<ApplicationData> allApps = null;
    private final Context mContext;
    private OnDatAvailableListener mOnDatAvailableListener;
    private PackageManager mPackageManager;
    private List<ApplicationInfo> mInstalledApplications;
    private boolean isDone = false;
    private ProgressDialog mProgressDialog;
    private int mAppNumber;
    private int mCurrentProgress;
    private double mPercentage;

    private AppListGetter(Context thisActivity) {
        mContext = thisActivity;
    }

    public static AppListGetter getInstance(Context thisActivity) {
        if (instance == null) {
            instance = new AppListGetter(thisActivity);
            instance.execute();
        }
        return instance;
    }

    private void getAppsFromPM() {

        for (ApplicationInfo info : mInstalledApplications) {

            ApplicationData app = new ApplicationData(mPackageManager.getApplicationLabel(info).toString(), info.packageName, mPackageManager.getApplicationIcon(info));
            if (((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {

                userApps.add(app);
            }
            allApps.add(app);

            publishProgress();

        }

        SortList(allApps);
        SortList(userApps);
    }


    private void SortList(List<ApplicationData> appList) {
        Collections.sort(appList);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPackageManager = mContext.getPackageManager();
        mInstalledApplications = mPackageManager.getInstalledApplications(0);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mAppNumber = mInstalledApplications.size();
        mCurrentProgress = 0;
        mProgressDialog.setMessage(mContext.getString(R.string.loading_apps)+"\n"+"0/"+ mAppNumber +"\n"+"0%");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();


        userApps = new ArrayList<>();
        allApps = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        getAppsFromPM();
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mProgressDialog.dismiss();
        mInstalledApplications.clear();
        mOnDatAvailableListener.onDataAvailable();
        isDone = true;
    }

    @Override
    protected void onProgressUpdate(Void...params)
    {
        mCurrentProgress++;
        mPercentage = ((double) mCurrentProgress)/((double)mAppNumber);
        mProgressDialog.setMessage(mContext.getString(R.string.loading_apps)+"\n"+mCurrentProgress+"/"+mInstalledApplications.size()+"\n"+ ((int)(mPercentage*100))+"%");

    }

    public void setOnDataAvailableListener(OnDatAvailableListener onDatAvailableListener) {
        this.mOnDatAvailableListener = onDatAvailableListener;
    }

    List<ApplicationData> getAvailableData(boolean showSystemApps) {
        return showSystemApps ? allApps : userApps;
    }

    public void callOnDataAvailable() {
        if (isDone) mOnDatAvailableListener.onDataAvailable();
    }


    public interface OnDatAvailableListener {

        void onDataAvailable();


    }


}
