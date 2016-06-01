package com.sudocode.sudohide;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;


abstract class AppListAdapter extends BaseAdapter implements Filterable {

    List<ApplicationData> displayItems;

    public boolean isShowSystemApps() {
        return showSystemApps;
    }

    private final boolean showSystemApps;
    Context mContext;
    LayoutInflater mInflater;
    Filter filter;

    AppListAdapter(Context mContext, boolean showSystemApps) {
        super();
        this.displayItems = AppListGetter.getInstance(mContext).getAppList(showSystemApps);
        this.showSystemApps = showSystemApps;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);

    }

    public void setDisplayItems(List<ApplicationData> displayItems) {
        this.displayItems = displayItems;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return displayItems.size();
    }

    @Override
    public Object getItem(int position) {
        return displayItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return displayItems.get(position).hashCode();
    }

}
