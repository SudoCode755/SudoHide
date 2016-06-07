package com.sudocode.sudohide;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.sudocode.sudohide.Adapters.CheckBoxAdapter;

import java.util.Iterator;
import java.util.Map;

import static com.sudocode.sudohide.MainActivity.pref;

public class AppHideConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_hide_configuration);
        final String packageName = getIntent().getStringExtra(Constants.KEY_PACKAGE_NAME);
        PackageManager packageManager = this.getPackageManager();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String applicationLabel = packageManager.getApplicationLabel(applicationInfo).toString().trim();
        ActionBar supportActionBar = getSupportActionBar();
        if ((supportActionBar != null)) {
            supportActionBar.setTitle("Hides From");
            supportActionBar.setSubtitle(applicationLabel + " Configuration");
        }

        CheckBox ckbHideFromSystem = (CheckBox) findViewById(R.id.hide_from_system);
        ListView sub_listView = (ListView) findViewById(R.id.subsettings_listview);
        assert sub_listView != null;

        final CheckBoxAdapter sub_adapter = new CheckBoxAdapter(AppHideConfigurationActivity.this, packageName);
        sub_listView.setAdapter(sub_adapter);

        sub_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox itemCheckBock = (CheckBox) view.findViewById(R.id.chkCheckBox);
                itemCheckBock.performClick();
            }
        });

        AppCompatEditText settingInputSearch = (AppCompatEditText) findViewById(R.id.subSettingsInputSearch);
        Button applyButton = (Button) findViewById(R.id.configApplyButton);

        assert settingInputSearch != null;
        settingInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                sub_adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        final boolean[] isCheckedChanged = {false};
        final boolean[] checkedValue = new boolean[1];
        final String pref_key = packageName + Constants.KEY_HIDE_FROM_SYSTEM;
        assert ckbHideFromSystem != null;
        ckbHideFromSystem.setChecked(pref.getBoolean(pref_key, false));
        ckbHideFromSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isCheckedChanged[0] = true;
                checkedValue[0] = isChecked;
            }
        });

        assert applyButton != null;
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean changes = false;
                Iterator it = sub_adapter.getChangedItems().entrySet().iterator();
                while (it.hasNext()) {
                    changes = true;
                    Map.Entry pair = (Map.Entry) it.next();
                    pref.edit()
                            .putBoolean((String) pair.getKey(), ((Boolean) pair.getValue()))
                            .apply();
                    it.remove();
                }
                if (isCheckedChanged[0]) {
                    pref.edit().putBoolean(pref_key, checkedValue[0]).apply();
                    changes = true;
                }
                if (changes) {
                    Toast.makeText(AppHideConfigurationActivity.this, "Changes Applied", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}


