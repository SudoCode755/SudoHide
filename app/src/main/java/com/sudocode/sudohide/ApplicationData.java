package com.sudocode.sudohide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class ApplicationData implements Comparable<ApplicationData> {

    private final String title;
    private final String key;
    private final Drawable icon;

    public ApplicationData(String title, String key, Drawable icon) {
        this.title = title;
        this.key = key;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    public Drawable getIcon() {
        return icon;
    }


    @Override
    public int compareTo(@NonNull ApplicationData another) {
        return this.title.compareToIgnoreCase(another.title);
    }
}
