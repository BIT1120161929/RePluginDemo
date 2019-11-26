package com.test.minidouyin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.test.minidouyin.R;
import com.test.minidouyin.network.beans.Feed;

import java.util.List;

public class RecyclerView4UserInfoAdapter extends RecyclerView.Adapter<RecyclerView4UserInfoAdapter.UserInfoViewHolder> {

    private Context mContext;
    private List<Feed> feedList;

    public RecyclerView4UserInfoAdapter(Context mContext, List<Feed> feedList) {
        this.mContext = mContext;
        this.feedList = feedList;
    }

    @NonNull
    @Override
    public UserInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserInfoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user_info,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserInfoViewHolder holder, int position) {
        Glide.with(mContext).load(feedList.get(position).getImageUrl()).into(holder.item_iv_cover);
        holder.item_tv_name.setText("VideoName");
        holder.item_tv_description.setText("VideoDescription");
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    class UserInfoViewHolder extends RecyclerView.ViewHolder{
        private ImageView item_iv_cover;
        private TextView item_tv_name;
        private TextView item_tv_description;

        public UserInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            item_iv_cover = itemView.findViewById(R.id.item_user_iv_cover);
            item_tv_name = itemView.findViewById(R.id.item_video_tv_name);
            item_tv_description = itemView.findViewById(R.id.item_user_tv_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v,
                                feedList.get(getPosition()).getVideoURL(),
                                feedList.get(getPosition()).getUserName(),
                                feedList.get(getPosition()).getStudentId());
                    }
                }
            });
        }
    }


    /**
     * 点击RecyclerView某条的监听
     */
    public interface OnItemClickListener{
        /**
         * 当RecyclerView某条被点击时回调
         * @param view 点击item的视图
         * @param videoUrl 点击得到的数据
         */
        public void OnItemClick(View view,String videoUrl,String name,String id);
    }

    private RecyclerView4UserInfoAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(RecyclerView4UserInfoAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
