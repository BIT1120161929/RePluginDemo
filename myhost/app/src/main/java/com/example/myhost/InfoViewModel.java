package com.example.myhost;

import android.arch.lifecycle.ViewModel;

/**
 * 已安装插件信息的ViewModel
 * 通过将LiveData设置为单例，使得多个Activity中的同一对象都能实时更新
 */
public class InfoViewModel extends ViewModel {

    private static MyLiveData<String> currentInfo;

    public MyLiveData<String> getCurrentInfo() {
        synchronized (this){
            if(currentInfo==null){
                currentInfo = new MyLiveData<>();
            }
        }
        return currentInfo;
    }
}
