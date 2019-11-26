package com.test.minidouyin.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qihoo360.replugin.RePlugin;
import com.test.minidouyin.view.DragRecyclerView;
import com.test.minidouyin.activity.MainActivity;
import com.test.minidouyin.R;
import com.test.minidouyin.adapter.RecyclerView4UserInfoAdapter;
import com.test.minidouyin.network.beans.Feed;
import com.test.minidouyin.network.beans.FeedsResponse;
import com.test.minidouyin.network.service.VideoListService;
import com.test.minidouyin.utils.TransportUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 用户信息
 */
public class UserInfoFragment extends Fragment {
    private DragRecyclerView rvCreation;
    private CircleImageView iv_icon;
    private TextView tv_name;
    private TextView tv_id;
    private List<Feed> userFeedList;
    private String username;
    private List<Feed> feedList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info,container,false);
        rvCreation = view.findViewById(R.id.fr_ui_rv_creation);
        iv_icon = view.findViewById(R.id.fr_ui_ci_icon);
        tv_name = view.findViewById(R.id.fr_ui_tv_name);
        tv_id = view.findViewById(R.id.fr_ui_tv_id);

        rvCreation.setLayoutManager(new LinearLayoutManager(container.getContext()==null?RePlugin.getPluginContext():container.getContext()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<FeedsResponse> call = retrofit.create(VideoListService.class).feedRequest();
        call.enqueue(new Callback<FeedsResponse>() {
            @Override
            public void onResponse(Call<FeedsResponse> call, Response<FeedsResponse> response) {
                if(response.body()!=null){
                    feedList = response.body().getFeeds();

                    RecyclerView4UserInfoAdapter mAdapter = new RecyclerView4UserInfoAdapter(getActivity()==null?RePlugin.getPluginContext():getActivity(),feedList);
                    rvCreation.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new RecyclerView4UserInfoAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClick(View view, String videoUrl, String name, String id) {
                            EventBus.getDefault().post(new TransportUtils(videoUrl,name,id));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<FeedsResponse> call, Throwable t) {

            }
        });
        return view;
    }

    @Subscribe
    public void onEventUserInfo(TransportUtils transportUtils){
        if(transportUtils.PALY == MainActivity.PLAY){
            tv_name.setText(transportUtils.userName);
            tv_id.setText(transportUtils.studentId);
            username = transportUtils.userName;
            userFeedList = new ArrayList<>();
            for(Feed feed:feedList){
                if(username==null){
                    userFeedList.add(new Feed());
                }
                if(feed.getUserName().equals(username)){
                    userFeedList.add(feed);
                }
            }
            RecyclerView4UserInfoAdapter mAdapter = new RecyclerView4UserInfoAdapter(getActivity()==null?RePlugin.getPluginContext():getActivity(),userFeedList);
            rvCreation.setAdapter(mAdapter);
            rvCreation.setAdapter(new RecyclerView4UserInfoAdapter(getActivity()==null?RePlugin.getPluginContext():getActivity(),userFeedList));
        }
    }

}
