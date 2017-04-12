package com.example.admin.pulltorefreshrecycleview_master.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by admin on 2017/4/12.
 */

public class LayoutUtil {
    public static void setContentView(Activity activity, View view) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        activity.setContentView(view);
    }
}
