package com.sudocode.sudohide.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudocode.sudohide.ApplicationData;
import com.sudocode.sudohide.R;

import java.util.ArrayList;
import java.util.List;

public class ShowConfigurationAdapter extends AppListAdapter {


    private final Context mContext;
    private final LayoutInflater mInflater;
    private final String mPackageName;
    private final SharedPreferences pref;

    public ShowConfigurationAdapter(Context context, String packageName) {
        super(context, true);
        mContext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mInflater = LayoutInflater.from(context);
        mPackageName = packageName;
        populateDisplayList();
    }

    private void populateDisplayList() {

        List<ApplicationData> appList = new ArrayList<>();

        for (ApplicationData app : mDisplayItems) {
            final String pref_key = app.getKey() + ":" + mPackageName;
            if (pref.getBoolean(pref_key, false)) {
                appList.add(app);
            }
        }
        mDisplayItems = appList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null, false);
        }

        final TextView title = (TextView) convertView.findViewById(R.id.app_name);
        final ImageView icon = (ImageView) convertView.findViewById(R.id.app_icon);

        final String sTitle = mDisplayItems.get(position).getTitle();
        final Drawable dIcon = mDisplayItems.get(position).getIcon();

        title.setText(sTitle);
        icon.setImageDrawable(dIcon);


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return mDisplayItems.get(position).hashCode();
    }

    @Override
    public int getCount() {
        return mDisplayItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mDisplayItems.get(position);
    }


}
