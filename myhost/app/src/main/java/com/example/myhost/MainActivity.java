package com.example.myhost;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.myhost.databinding.ActivityMainBinding;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 目前有一个问题需要解决，就是打开插件Activity之后，Activity页面会变小。目前不知道是什么原因。
 * 已经解决，是因为使用了androidx的原因，将sdk版本调整为28，并且所有的support库都调整为对应版本即可。
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private InfoViewModel model;

    public static final int REQUEST_PERMISSIONS = 123;
    public String[] permissionArray = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        getPermission();

        //of使得ViewModel在Activity之内有效
        model = ViewModelProviders.of(this).get(InfoViewModel.class);
        //初始化Observer，也就是LiveData发生变化后的操作
        final Observer<String> infoObserver = info-> binding.tvInfo.setText(info);
        //设置Observer为LiveData的observer
        model.getCurrentInfo().observe(this,infoObserver);

        /**
         * 该插件以jar包的形式存放在main/assets/plugins文件夹下，展示的是利用  包名  来打开对应的Activity
         * 这块的包名，就是plugin的包名，可以去plugin的module下的gradle文件中查看applicationId属性。
         */
        binding.btnPlugin.setOnClickListener(view-> RePlugin.startActivity(this,RePlugin.createIntent("com.example.myplugin","com.example.myplugin.MainActivity")));
        /**
         * 通过别名打开插件，其实就是jar包的名字。
         * 发现一个和tricky的地方，就是如果使用同一份插件但是改了一个别名的话是打不开的。
         */
        binding.btnAlias.setOnClickListener(view-> RePlugin.startActivity(this,RePlugin.createIntent("plugin2","com.example.myplugin2.MainActivity")));

        /**
         * 目前还打不开，估计是因为这个插件中的Context的问题，需要判断是否获取本插件的Context。目前只是一个猜测，还需要继续验证。已经解决！！
         * 不是因为context的问题，虽然context也是一个坑。是因为RePlugin的问题，会导致部分继承自androidx的AppCompatActivity不会替换成功，需要手动替换所有的AppCompatActivity。
         * 权限问题需要拉到宿主app中。
         */
        binding.btnDouyin.setOnClickListener(view-> RePlugin.startActivity(this,RePlugin.createIntent("douyin","com.test.minidouyin.activity.MainActivity")));

        /**
         * 尝试打开官方的demo
         * 打得开……
         */
        binding.btnDemo.setOnClickListener(view-> RePlugin.startActivity(this,RePlugin.createIntent("demo","com.qihoo360.replugin.sample.demo1.MainActivity")));

        /**
         * 模拟下载安装外置插件，利用的是重写RePluginCallbacks中的onPluginNotExistsForActivity方法
         */
        binding.btnInstall.setOnClickListener(view-> RePlugin.startActivity(this,RePlugin.createIntent("demo3","com.qihoo360.replugin.sample.demo3.MainActivity")));

        binding.btnViewModel.setOnClickListener(view->{
            Intent intent = new Intent(MainActivity.this, ViewModelActivity.class);
            startActivity(intent);
        });

        /**
         * 显示已安装插件信息
         */
        binding.btnGetInfo.setOnClickListener(view->{
            StringBuilder result = new StringBuilder();
            for (PluginInfo info : RePlugin.getPluginInfoList()) {
                result.append(info);
                result.append("\n");
            }
            model.getCurrentInfo().setValue(result.toString());
        });

        /**
         * 使用插件中的类
         */
        binding.btnUsePluginClass.setOnClickListener(view->{
            //获取插件的ClassLoader
            ClassLoader pluginClassLoader = RePlugin.fetchClassLoader("com.example.myplugin");
            try {
                //加载对应的类
                Class pluginClass = pluginClassLoader.loadClass("com.example.myplugin.TestPluginClass");
                Object pluginTestInstance = pluginClass.newInstance();
                Method getName = pluginClass.getDeclaredMethod("getName");
                Method getVersion = pluginClass.getDeclaredMethod("getVersion");
                Method setName = pluginClass.getDeclaredMethod("setName",String.class);
                Method setVersion = pluginClass.getDeclaredMethod("setVersion",Integer.class);

                setName.invoke(pluginTestInstance,"testName");
                setVersion.invoke(pluginTestInstance,1231231);

                model.getCurrentInfo().setValue(getName.invoke(pluginTestInstance)+"   "+getVersion.invoke(pluginTestInstance));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissionArray, REQUEST_PERMISSIONS);
        }
    }
}
