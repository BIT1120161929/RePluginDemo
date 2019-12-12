package com.example.myplugin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myplugin.databinding.ActivityMainBinding;
import com.qihoo360.replugin.RePlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


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

            switch (position) {
                case 0:
                    ((TextRecyclerView) (holder.itemView)).setText("回到宿主工程MainActivity");
                    ((TextRecyclerView) (holder.itemView)).seButton("跳转");
                    ((TextRecyclerView) (holder.itemView)).binding.btnFunction.setOnClickListener(view -> {
                        Intent intent = RePlugin.createIntent("com.example.myhost", "com.example.myhost.MainActivity");
                        if (intent == null) return;
                        /**
                         * 这就是个flag就是Activity的四种启动模式……
                         * 估计是因为plugin2 的点击事件是在Activity里写的，所以startActivity调用的是被Activity重写过的stratActivity。
                         */
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        view.getContext().startActivity(intent);
                    });
                    break;
                case 1:
                    ((TextRecyclerView) (holder.itemView)).setText("使用宿主的类");
                    ((TextRecyclerView) (holder.itemView)).seButton("使用并替换标题");
                    ((TextRecyclerView) (holder.itemView)).binding.btnFunction.setOnClickListener(view->{
                        ClassLoader hostClassLoader = RePlugin.getHostClassLoader();
                        ((TextRecyclerView) (holder.itemView)).seButton("点击了按钮");
                        try {
                            if(hostClassLoader!=null){
                                Class hostTestClass = hostClassLoader.loadClass("com.example.myhost.TestClass");
                                Object testClass = hostTestClass.newInstance();
                                Method getName = hostTestClass.getDeclaredMethod("getName");
                                Method getDescription = hostTestClass.getDeclaredMethod("getDescription");
                                Method setName = hostTestClass.getDeclaredMethod("setName",String.class);
                                Method setDescription = hostTestClass.getDeclaredMethod("setDescription",String.class);

                                setName.invoke(testClass,"testName");
                                setDescription.invoke(testClass,"testDescription");
                                if(testClass!=null){
                                    ((TextRecyclerView) (holder.itemView)).setText(getName.invoke(testClass) + "   "+getDescription.invoke(testClass));
                                }
                            }else{
                                ((TextRecyclerView) (holder.itemView)).setText("Class加载失败");
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            ((TextRecyclerView) (holder.itemView)).setText("ClassNotFoundException");
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
                    break;
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
