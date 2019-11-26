package com.test.minidouyin.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.loader.a.PluginAppCompatActivity;
import com.test.minidouyin.R;
import com.test.minidouyin.fragments.ShootVideoFragment;
import com.test.minidouyin.fragments.UserInfoFragment;
import com.test.minidouyin.fragments.VideoListFragment;
import com.test.minidouyin.fragments.VideoPlayFragment;
import com.test.minidouyin.utils.TransportUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 可以左右滑动的三格ViewPager2，从0号到2号分别为播放和个人信息的组合窗口，播放列表，拍摄和录制窗口
 * 右下方按钮为上传按钮
 */
public class MainActivity extends PluginAppCompatActivity {
    
    private final String TAG = "MainActivity";


    private ViewPager pager;
    private FloatingActionButton floatingActionButton;

    public static final Integer REFRESH = 110;

    public static final int REQUEST_PERMISSIONS = 123;
    public String[] permissionArray = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };

    public static final int PLAY = 1;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton = findViewById(R.id.ac_main_fb_post);

        //申请权限
        if (ContextCompat.checkSelfPermission(this!=null?this: RePlugin.getPluginContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissionArray, REQUEST_PERMISSIONS);
        }

        pager = findViewById(R.id.ac_main_vp_viewpager);
        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 4;
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:return new UserInfoFragment();
                    case 1:return new VideoPlayFragment();
                    case 2:return new VideoListFragment();
                    case 3:return new ShootVideoFragment();
                }
                return null;
            }
        });
        pager.setCurrentItem(2,false);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * URL传递到Activity
     * @param transportUtils 为传输的工具类
     */
    @Subscribe
    public void onEventMainThread2Play(TransportUtils transportUtils){
        if(transportUtils.PALY == PLAY){
            pager.setCurrentItem(0);
        }
    }
}
