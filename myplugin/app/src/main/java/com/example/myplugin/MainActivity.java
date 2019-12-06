package com.example.myplugin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.myplugin.databinding.ActivityMainBinding;
import com.qihoo360.replugin.RePlugin;

import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyAdapter adapter;
    private static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "before onCreate");
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Log.e(TAG, "before refreshList");
        refreshList();
    }

    private void refreshList() {
        adapter = new MyAdapter();
        Log.e(TAG, "before setAdapter");
        binding.rv.setAdapter(adapter);
        Log.e(TAG, "before setLayoutManager");
        if (RePlugin.getPluginContext() != null) {
            binding.rv.setLayoutManager(new LinearLayoutManager(RePlugin.getPluginContext()));
        } else {
            binding.rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }

    }

    public class DataHolder<T> {
        public T string;

        public DataHolder(T string) {
            this.string = string;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BaseViewHolder(new TextRecyclerView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            DataHolder dataHolder = new DataHolder((position + 1) + "");
            ((TextRecyclerView) (holder.itemView)).setText("这是第" + dataHolder.string + "个功能");
            ((TextRecyclerView) (holder.itemView)).seButton("这是第" + dataHolder.string + "个功能按钮");
            if (position == 0) {
                ((TextRecyclerView) (holder.itemView)).binding.btnFunction.setOnClickListener(view -> {
                    Log.e("Plugin", "点击了按钮希望回到宿主的MainActivity");
                    Intent intent = RePlugin.createIntent("com.example.myhost","com.example.myhost.MainActivity");
                    if(intent==null)return;
                    /**
                     * 这就是个flag就是Activity的四种启动模式……
                     * 估计是因为plugin2 的点击事件是在Activity里写的，所以startActivity调用的是被Activity重写过的stratActivity。
                     */
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                });
            } else if (position == 1) {
                ((TextRecyclerView) (holder.itemView)).binding.btnFunction.setOnClickListener(view -> {
                    Log.e("Plugin", "点击了按钮希望进入测试页面");
                    Intent intent = new Intent(view.getContext(),TestActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
