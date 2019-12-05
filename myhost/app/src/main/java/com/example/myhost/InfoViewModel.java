package com.example.myhost;

import android.arch.lifecycle.ViewModel;

/**
 * 已安装插件信息的ViewModel
 */
public class InfoViewModel extends ViewModel {

    private MyLiveData<String> currentInfo;

    public MyLiveData<String> getCurrentInfo() {
        if(currentInfo==null){
            currentInfo = new MyLiveData<>();
        }
        return currentInfo;
    }
}
