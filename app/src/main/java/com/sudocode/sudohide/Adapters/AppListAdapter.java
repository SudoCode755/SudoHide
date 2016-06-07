package com.sudocode.sudohide.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.sudocode.sudohide.ApplicationData;

import java.util.ArrayList;
import java.util.List;


abstract class AppListAdapter extends BaseAdapter implements Filterable {

    private final boolean mShowSystemApps;
    List<ApplicationData> mDisplayItems = new ArrayList<>();
    Context mContext;
    LayoutInflater mInflater;
    Filter filter;


    AppListAdapter(Context context, final boolean mShowSystemApps) {
        super();
        this.mContext = context;

        final AppListGetter appListGetter = AppListGetter.getInstance(context);
        appListGetter.setOnDataAvailableListener(new AppListGetter.OnDatAvailableListener() {
            @Override
            public void onDataAvailable() {

                mDisplayItems = appListGetter.getAvailableData(mShowSystemApps);
                AppListAdapter.this.notifyDataSetChanged();
            }
        });

        appListGetter.callOnDataAvailable();

        this.mShowSystemApps = mShowSystemApps;
        this.mInflater = LayoutInflater.from(context);

    }

    public boolean isShowSystemApps() {
        return mShowSystemApps;
    }

    public void setDisplayItems(List<ApplicationData> displayItems) {
        this.mDisplayItems = displayItems;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return mDisplayItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mDisplayItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDisplayItems.get(position).hashCode();
    }


    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AppListFilter(this);
        }
        return filter;
    }
}
