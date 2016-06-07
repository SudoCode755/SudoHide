package com.sudocode.sudohide.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudocode.sudohide.ApplicationData;
import com.sudocode.sudohide.R;

import java.util.ArrayList;
import java.util.List;

public class ShowConfigurationAdapter extends BaseAdapter {


    private final Context mContext;
    private final LayoutInflater mInflater;
    private final String mPackageName;
    private final List<ApplicationData> displayItems = new ArrayList<>();
    private final SharedPreferences pref;
    private List<ApplicationData> allApps;
    public ShowConfigurationAdapter(Context context, String packageName) {
        super();
        mContext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mInflater = LayoutInflater.from(context);
        mPackageName = packageName;
        populateDisplayList();
    }

    private void populateDisplayList() {
        final AppListGetter appListGetter = AppListGetter.getInstance(mContext);

        appListGetter.setOnDataAvailableListener(new AppListGetter.OnDatAvailableListener() {
            @Override
            public void onDataAvailable() {
                allApps = appListGetter.getAvailableData(true);
            }
        });
        appListGetter.callOnDataAvailable();

        for (ApplicationData app : allApps) {
            final String pref_key = app.getKey() + ":" + mPackageName;
            if (pref.getBoolean(pref_key, false)) {
                displayItems.add(app);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null, false);
        }

        final TextView title = (TextView) convertView.findViewById(R.id.app_name);
        final ImageView icon = (ImageView) convertView.findViewById(R.id.app_icon);

        final String sTitle = displayItems.get(position).getTitle();
        final Drawable dIcon = displayItems.get(position).getIcon();

        title.setText(sTitle);
        icon.setImageDrawable(dIcon);


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return displayItems.get(position).hashCode();
    }

    @Override
    public int getCount() {
        return displayItems.size();
    }

    @Override
    public Object getItem(int position) {
        return displayItems.get(position);
    }


}
