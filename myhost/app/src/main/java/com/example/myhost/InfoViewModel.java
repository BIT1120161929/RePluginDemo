package com.example.myhost;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * 已安装插件信息的ViewModel
 */
public class InfoViewModel extends ViewModel {

    private MutableLiveData<String> currentInfo;

    public MutableLiveData<String> getCurrentInfo() {
        if(currentInfo==null){
            currentInfo = new MutableLiveData<>();
        }
        return currentInfo;
    }
}
