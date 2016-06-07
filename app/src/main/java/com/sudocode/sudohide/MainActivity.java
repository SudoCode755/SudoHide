package com.sudocode.sudohide;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sudocode.sudohide.Adapters.MainAdapter;
import com.sudocode.sudohide.Adapters.ShowConfigurationAdapter;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    static public SharedPreferences pref;
    private final Handler handler = new Handler();
    private ListView listView;
    private MainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isXposedActive() == false) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(R.string.app_name);
            alertDialog.setMessage(getString(R.string.sudoHide_module_not_active));
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Exit App", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();
        }
        pref = getSharedPreferences(BuildConfig.APPLICATION_ID + "_preferences", Constants.OVERRIDE_MODE_WORLD_READABLE);
        mAdapter = new MainAdapter(this, pref.getBoolean(Constants.KEY_SHOW_SYSTEM_APP, false));
        AppCompatEditText inputSearch = (AppCompatEditText) findViewById(R.id.searchInput);

        assert inputSearch != null;
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                MainActivity.this.mAdapter.getFilter().filter(cs);
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


        listView = (ListView) findViewById(R.id.mListView);
        assert listView != null;
        listView.setAdapter(mAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AppHideConfigurationActivity.class);
                intent.putExtra(Constants.KEY_PACKAGE_NAME, mAdapter.getKey(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String pkgName = mAdapter.getKey(position);

                View settingsDisplay = getLayoutInflater().inflate(R.layout.settingsdisplay, null, false);
                ListView sub_listView = (ListView) settingsDisplay.findViewById(R.id.settingsDisplayListViewID);
                ShowConfigurationAdapter subListAdapter = new ShowConfigurationAdapter(MainActivity.this, pkgName);
                sub_listView.setAdapter(subListAdapter);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.title_hide_app)
                        .setView(settingsDisplay)
                        .show();

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem showSystemApps = menu.findItem(R.id.action_show_system_app);
        showSystemApps.setChecked(pref.getBoolean(Constants.KEY_SHOW_SYSTEM_APP, false));
        refresh(showSystemApps.isChecked());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_restart_launcher) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    restartLauncher();
                }
            });
            return true;
        } else if (id == R.id.action_show_system_app) {

            boolean showSystemApps = !item.isChecked();
            item.setChecked(showSystemApps);
            pref.edit()
                    .putBoolean(Constants.KEY_SHOW_SYSTEM_APP, showSystemApps)
                    .apply();
            refresh(showSystemApps);
        }

        return super.onOptionsItemSelected(item);
    }

    private void restartLauncher() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (RunningAppProcessInfo process : processes) {
            if (!process.processName.equals(MainActivity.class.getPackage().getName())) {
                am.killBackgroundProcesses(process.processName);
            }
        }
    }


    private void refresh(boolean showSystemApp) {
        mAdapter = new MainAdapter(this, showSystemApp);
        listView.setAdapter(mAdapter);

    }

    //Method is hooked by framework.
    @SuppressWarnings("SameReturnValue")
    private boolean isXposedActive() {
        return false;
    }

}
