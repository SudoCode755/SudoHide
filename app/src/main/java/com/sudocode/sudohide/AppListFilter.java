package com.sudocode.sudohide;


import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

class AppListFilter extends Filter {
    private final AppListAdapter callingInstance;

    public AppListFilter(AppListAdapter thisCallingInstance) {
        super();
        callingInstance = thisCallingInstance;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        String prefix = constraint.toString().toLowerCase();
        ArrayList<ApplicationData> filteredList = new ArrayList<>();

        List<ApplicationData> mItemList = AppListGetter.getInstance(callingInstance.getContext()).getAppList(callingInstance.isShowSystemApps());

        if (prefix.isEmpty() || prefix.length() == 0) {
            filterResults.values = mItemList;
            filterResults.count = mItemList.size();
        } else {
            for (ApplicationData stringObjectMap : mItemList) {
                String val =  stringObjectMap.getTitle().toLowerCase();

                if (val.startsWith(prefix)) {
                    filteredList.add(stringObjectMap);
                }
            }
            filterResults.values = filteredList;
            filterResults.count = filteredList.size();
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        callingInstance.setDisplayItems( (List<ApplicationData>) results.values);
        callingInstance.notifyDataSetChanged();
    }

}