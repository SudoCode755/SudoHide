package com.sudocode.sudohide.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudocode.sudohide.Constants;
import com.sudocode.sudohide.R;

import java.util.Set;

public class MainAdapter extends AppListAdapter {

    private final Set<String> mHidingConfigurationKeySet;


    public MainAdapter(Context context, boolean showSystemApps) {
        super(context, showSystemApps);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mHidingConfigurationKeySet = PreferenceManager.getDefaultSharedPreferences(mContext).getAll().keySet();
    }

    public String getKey(int position) {
        return mDisplayItems.get(position).getKey();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null, false);
        }

        final TextView title = (TextView) convertView.findViewById(R.id.app_name);
        final ImageView icon = (ImageView) convertView.findViewById(R.id.app_icon);
        final TextView subTitle = (TextView) convertView.findViewById(R.id.package_name);

        final String sTitle = mDisplayItems.get(position).getTitle();
        final String key = mDisplayItems.get(position).getKey();
        final Drawable dIcon = mDisplayItems.get(position).getIcon();


        title.setText(sTitle);
        icon.setImageDrawable(dIcon);
        String key_subTitle = key;
        if (!PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Constants.KEY_SHOW_PACKAGE_NAME, false)) {
            key_subTitle = "";
        }
        subTitle.setText(key_subTitle);

        title.setTextColor(mContext.obtainStyledAttributes((new TypedValue()).data, new int[]{R.attr.editTextColor}).getColor(0, 0));
        subTitle.setTextColor(mContext.obtainStyledAttributes((new TypedValue()).data, new int[]{R.attr.colorControlNormal}).getColor(0, 0));

        if (appIsHidden(key)) {
            int color = mContext.obtainStyledAttributes((new TypedValue()).data, new int[]{R.attr.colorAccent}).getColor(0, 0);
            subTitle.setTextColor(color);
            title.setTextColor(color);
        }


        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = mContext.getPackageManager().getLaunchIntentForPackage(key);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(it);
            }
        });

        return convertView;
    }

    private boolean appIsHidden(String packageName) {
        for (String key : mHidingConfigurationKeySet) {
            if (key.endsWith(packageName)) {
                return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(key, false);
            }
        }
        return false;
    }
}

