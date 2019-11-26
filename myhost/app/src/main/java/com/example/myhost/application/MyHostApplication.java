package com.example.myhost.application;

import android.content.Context;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.RePluginApplication;
import com.qihoo360.replugin.RePluginConfig;

/**
 * 通过继承式实现
 * 需要重写Application，继承自RePluginApplication就行.
 * 下面的代码都不是必须的。
 * 最后去manifest中使用即可
 */
public class MyHostApplication extends RePluginApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        RePlugin.App.onCreate();
        RePlugin.enableDebugger(this, true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        RePlugin.App.attachBaseContext(this,
                new RePluginConfig()
                        //不开启签名校验
                        .setVerifySign(false)
                        //是否打印更详细日志
                        .setPrintDetailLog(true)
                        //当插件没有指定的类时，是否使用宿主的类
                        .setUseHostClassIfNotFound(true)
                        //在插件安装时，是否将文件移动到app_p_a目录下，默认为true
                        .setMoveFileWhenInstalling(false));
    }
}
