package com.test.minidouyin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qihoo360.replugin.RePlugin;
import com.test.minidouyin.R;
import com.test.minidouyin.adapter.RecyclerView4VideoListAdapter;
import com.test.minidouyin.network.beans.Feed;
import com.test.minidouyin.network.beans.FeedsResponse;
import com.test.minidouyin.network.service.VideoListService;
import com.test.minidouyin.utils.TransportUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 播放列表
 */
public class VideoListFragment extends Fragment {
    private static final String TAG = "VideoListFragment";
    private RecyclerView videoRecyclerView;

    public VideoListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context==null?RePlugin.getPluginContext():context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list,container,false);
        videoRecyclerView = view.findViewById(R.id.fr_videol_rv_videolist);
        DividerItemDecoration divider = new DividerItemDecoration(container.getContext()==null?RePlugin.getPluginContext():container.getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(container.getContext()==null?RePlugin.getPluginContext():container.getContext(),R.drawable.dimension_cool_divider));
        videoRecyclerView.addItemDecoration(divider);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()==null?RePlugin.getPluginContext():container.getContext()));

        fetchFeed();

        return view;
    }

    public void fetchFeed(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //生成接口对象并且返回Call对象，并没有真正执行call方法
        Call<FeedsResponse> call = retrofit.create(VideoListService.class).feedRequest();
        //发送请求，同步调用execute方法，异步调用enqueue方法
        call.enqueue(new Callback<FeedsResponse>() {
            @Override
            public void onResponse(Call<FeedsResponse> call, Response<FeedsResponse> response) {

                if(response.body()!=null){
                    List<Feed> feedList = response.body().getFeeds();
                    Collections.shuffle(feedList);
                    RecyclerView4VideoListAdapter mAdapter = new RecyclerView4VideoListAdapter(getActivity()==null?RePlugin.getPluginContext():getActivity(),feedList);
                    videoRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new RecyclerView4VideoListAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClick(View view, final String videoUrl, final String name, final String id) {
                            new Handler().postDelayed(new Runnable(){

                                @Override
                                public void run() {
                                    EventBus.getDefault().post(new TransportUtils(videoUrl,name,id));
                                    //忽略层级
//                                    EventBus testBus = EventBus.builder().eventInheritance(false).build();
//                                    testBus.post(new TransportUtils(videoUrl,name,id));

                                    //为EventBus提供线程池来进行后台任务
//                                    EventBus.builder().executorService(Executors.newFixedThreadPool(10));
//                                    EventBus.builder().executorService(new ThreadPoolExecutor(10,10,0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>()));

                                    //在没有订阅者的时候保持静默
//                                    EventBus eventBus = EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).build();
//                                    eventBus.postSticky(new TransportUtils(videoUrl,name,videoUrl));
                                }
                            },800);

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<FeedsResponse> call, Throwable t) {}
        });
    }
}
