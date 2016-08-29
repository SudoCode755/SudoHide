package com.sudocode.sudohide.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sudocode.sudohide.Constants;
import com.sudocode.sudohide.R;

import java.util.Map;
import java.util.TreeMap;

public class CheckBoxAdapter extends AppListAdapter {

    private final SharedPreferences pref;
    private final String currentPkgName;
    private final Map<String, Boolean> changedItems;
    private final String currentApplicationLabel;

    public CheckBoxAdapter(Context context, String pkgName, boolean showSystemApps) {

        super(context, showSystemApps);
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

    public Map<String, Boolean> getChangedItems() {
        return changedItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.preference_checkbox, null, false);
        }

        final TextView title = (TextView) convertView.findViewById(R.id.checkbox_app_name);
        final ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        final TextView subTitle = (TextView) convertView.findViewById(R.id.checkbox_package_name);


        final String sTitle = mDisplayItems.get(position).getTitle();
        final String key = mDisplayItems.get(position).getKey();
        final Drawable dIcon = mDisplayItems.get(position).getIcon();


        title.setText(sTitle);
        title.setTextColor(mContext.obtainStyledAttributes((new TypedValue()).data, new int[]{R.attr.editTextColor}).getColor(0, 0));
        icon.setImageDrawable(dIcon);

        String key_subTitle = key;
        if (!PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Constants.KEY_SHOW_PACKAGE_NAME, false)) {
            key_subTitle = "";
        }

        subTitle.setText(key_subTitle);

        final String pref_key = key + ":" + currentPkgName;

        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.chkCheckBox);
        if(changedItems.containsKey(pref_key))
        {
            checkBox.setChecked(changedItems.get(pref_key));
        }
        else
        {
            checkBox.setChecked(pref.getBoolean(pref_key, false));
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                boolean value = cb.isChecked();
                String toastMessage = currentApplicationLabel + " will " + (value ? "" : "not ") + " be hidden from " + sTitle;
                Toast.makeText(mContext, toastMessage, Toast.LENGTH_SHORT).show();
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

}
