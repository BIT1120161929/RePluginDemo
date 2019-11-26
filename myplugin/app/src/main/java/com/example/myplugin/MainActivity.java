package com.example.myplugin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.myplugin.databinding.ActivityMainBinding;
import com.qihoo360.replugin.RePlugin;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyAdapter adapter;
    private static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"before onCreate");
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        Log.e(TAG,"before refreshList");
        refreshList();
    }

    private void refreshList(){
        adapter = new MyAdapter();
        Log.e(TAG,"before setAdapter");
        binding.rv.setAdapter(adapter);
        Log.e(TAG,"before setLayoutManager");
        if(RePlugin.getPluginContext()!=null){
            binding.rv.setLayoutManager(new LinearLayoutManager(RePlugin.getPluginContext()));
        }else{
            binding.rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }

    }

    public static class DataHolder<T>{
        public T string;

        public DataHolder(T string) {
            this.string =string;
        }
    }

    public static class MyAdapter extends RecyclerView.Adapter{
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BaseViewHolder(new TextRecyclerView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            DataHolder dataHolder =new DataHolder((position+1)+"");
            ((TextRecyclerView)(holder.itemView)).setText("这是第"+dataHolder.string+"个功能");
            ((TextRecyclerView)(holder.itemView)).seButton("这是第"+dataHolder.string+"个功能按钮");
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder{

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
