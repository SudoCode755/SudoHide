package com.sudocode.sudohide;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Set;

class MainAdapter extends AppListAdapter {

    private final Set<String> mHidingConfigurationKeySet;


    public MainAdapter(Context context, boolean showSystemApps) {
        super(context, showSystemApps);
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mHidingConfigurationKeySet = PreferenceManager.getDefaultSharedPreferences(mContext).getAll().keySet();
    }

    public String getKey(int position) {
        return displayItems.get(position).getKey();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null, false);
        }

        final TextView title = (TextView) convertView.findViewById(R.id.app_name);
        final ImageView icon = (ImageView) convertView.findViewById(R.id.app_icon);

        final String sTitle = displayItems.get(position).getTitle();
        final String key = displayItems.get(position).getKey();
        final Drawable dIcon = displayItems.get(position).getIcon();

        title.setText(sTitle);
        icon.setImageDrawable(dIcon);


        int color = appIsHidden(key) ? Color.RED : Color.WHITE;
        title.setTextColor(color);


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
                return true;
            }
        }
        return false;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AppListFilter(this);
        }
        return filter;
    }

}

