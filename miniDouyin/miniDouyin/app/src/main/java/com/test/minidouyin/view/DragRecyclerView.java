package com.test.minidouyin.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;


import com.qihoo360.replugin.RePlugin;

public class DragRecyclerView extends RecyclerView {


    public DragRecyclerView(@NonNull Context context) {
        super(context==null? RePlugin.getPluginContext():context);
    }

    public DragRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context==null? RePlugin.getPluginContext():context, attrs);
    }

    public DragRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context==null? RePlugin.getPluginContext():context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
