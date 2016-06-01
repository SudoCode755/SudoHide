package com.sudocode.sudohide;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.TreeMap;

class CheckBoxAdapter extends AppListAdapter {

    private final SharedPreferences pref;
    private final String currentPkgName;
    private final Map<String, Boolean> changedItems;
    private final String currentApplicationLabel;

    public Map<String, Boolean> getChangedItems() {
        return changedItems;
    }

    public CheckBoxAdapter(Context context, String pkgName) {

        super(context, false);
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        currentPkgName = pkgName;
        changedItems = new TreeMap<>();
        PackageManager packageManager = mContext.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(currentPkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        currentApplicationLabel = packageManager.getApplicationLabel(applicationInfo).toString().trim();


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.preference_checkbox, null, false);
        }

        final TextView title = (TextView) convertView.findViewById(R.id.title);
        final ImageView icon = (ImageView) convertView.findViewById(R.id.icon);


        final String sTitle = displayItems.get(position).getTitle();
        final String key = displayItems.get(position).getKey();
        final Drawable dIcon = displayItems.get(position).getIcon();


        title.setText(sTitle);
        icon.setImageDrawable(dIcon);

        final String pref_key = key + ":" + currentPkgName;

        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.chkCheckBox);
        if (pref.getBoolean(pref_key, false)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }


        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                boolean value = cb.isChecked();
                String ToastMessage = currentApplicationLabel + " will " + (value ? "" : "not ") + " be hidden from " + sTitle;
                Toast.makeText(mContext, ToastMessage, Toast.LENGTH_LONG).show();
                addValue(value, pref_key);
            }
        });

        return convertView;
    }

    private void addValue(boolean value, String pref_key) {
        if (changedItems.containsKey(pref_key)) {
            changedItems.remove(pref_key);
        } else {
            changedItems.put(pref_key, value);
        }
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AppListFilter(this);
        }
        return filter;
    }
}
