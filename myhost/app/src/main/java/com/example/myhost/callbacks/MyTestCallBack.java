package com.example.myhost.callbacks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.RePluginCallbacks;
import com.qihoo360.replugin.model.PluginInfo;
import com.qihoo360.replugin.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MyTestCallBack extends RePluginCallbacks {
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public MyTestCallBack(Context context) {
        super(context);
    }

    @Override
    public boolean onPluginNotExistsForActivity(Context context, String plugin, Intent intent, int process) {
        //卡一下菊花
        ProgressDialog pd = ProgressDialog.show(context, "Installing", "Please wait");
        mHandler.postDelayed(()->{
            //耗时操作放到子线程中做
            simulateInstallExternalPlugin(context,plugin,intent);
            pd.dismiss();
        },1000);
        return true;
    }

    private void simulateInstallExternalPlugin(Context context,String plugin,Intent intent){
        String apkName = plugin+".apk";
        String apkPath = "external"+File.separator+apkName;
        String pluginFilePath = context.getFilesDir().getAbsolutePath()+File.separator+apkName;
        File pluginFile = new File(pluginFilePath);

        //TODO:改为下载apk即可
        if(pluginFile.exists()){
            FileUtils.deleteQuietly(pluginFile);
        }
        copyAssetsFileToAppFiles(context,apkPath,apkName);


        PluginInfo info = null;
        if(pluginFile.exists()){
            info = RePlugin.install(pluginFilePath);
        }
        if(info!=null){
            //不能直接使用上面传来的intent，因为在未安装插件的情况下，RePlugin没有将live解析成对应的packageName，所以还需要自己重新创建一下Intent
            Intent rePluginIntent = RePlugin.createIntent(info.getName(),intent.getComponent().getClassName());
            RePlugin.startActivity(context,rePluginIntent);
        }else{
            Toast.makeText(context, "install plugin failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从assets目录中复制某文件内容
     *
     * @param assetFileName assets目录下的Apk源文件路径
     * @param newFileName   复制到/data/data/package_name/files/目录下文件名
     */
    private void copyAssetsFileToAppFiles(Context context,String assetFileName, String newFileName) {
        InputStream is = null;
        FileOutputStream fos = null;
        int buffsize = 1024;

        try {
            is = context.getAssets().open(assetFileName);
            fos = context.openFileOutput(newFileName, Context.MODE_PRIVATE);
            int byteCount = 0;
            byte[] buffer = new byte[buffsize];
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
