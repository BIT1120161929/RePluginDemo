package com.test.minidouyin.fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;


import com.test.minidouyin.R;
import com.test.minidouyin.utils.OnDoubleClickListener;
import com.test.minidouyin.utils.TransportUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.test.minidouyin.activity.MainActivity.PLAY;

/**
 * 播放界面
 */
public class VideoPlayFragment extends Fragment {

    private VideoView videoView;
    private String videoURL;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(!isVisibleToUser&&videoView!=null){
            videoView.pause();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //在onCreate中注册EventBus
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.pause();
        videoView = null;
        //在onDestroy中注销EventBus
        EventBus.getDefault().unregister(this);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_play,container,false);
        videoView = view.findViewById(R.id.fr_videop_vv_play);


        videoView.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if(videoView.isPlaying())videoView.pause();
                else if(!videoView.isPlaying())videoView.start();
            }
        }));

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });

        return view;
    }

    /**
     * EventBus3已经可以不使用onEvent开头命名，但是要使用@Subscribe并且指定线程模型，如@Subscribe(threadMode = ThreadMode.MAIN)。
     * 默认为POSTING，也就是与post方法同一线程
     * MAIN是运行在UIThread中，所以要注意不要阻塞UI线程
     * MAIN_OEDERED按照post顺序执行
     * BACKGROUND启动后台线程完成任务，如果post方法不是在主线程执行的，那直接在那个线程执行。如果是UIThreadpost的，那就会使用一个后台线程执行
     * ASYNC启动异步线程完成任务
     * @param transportUtils
     */
    @Subscribe
    public void onEventMainThread(TransportUtils transportUtils){
        if(transportUtils.PALY == PLAY){
            videoURL = transportUtils.videoURL;
            Uri uri = Uri.parse(videoURL);
            videoView.setVideoURI(uri);
            videoView.start();
        }
    }
}
