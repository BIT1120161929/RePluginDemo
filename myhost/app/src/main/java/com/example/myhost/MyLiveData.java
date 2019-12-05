package com.example.myhost;

import android.arch.lifecycle.LiveData;
import android.util.Log;

/**
 * 尝试Extend LiveData
 */
public class MyLiveData<T> extends LiveData<T> {

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

    @Override
    protected void onActive() {
        Log.d("MyLiveData","start observing");
    }

    @Override
    protected void onInactive() {
        Log.d("MyLiveData","end observing");
    }
}
