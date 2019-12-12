package com.example.myhost;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.myhost.databinding.ActivityViewModelBinding;

/**
 * 测试单例模式下的LiveData是否可以影响多个Activity
 * 结果当然是没问题的，线程之间传递数据又多了一种方式
 */
public class ViewModelActivity extends AppCompatActivity {

    ActivityViewModelBinding binding;
    private InfoViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_model);

        model = ViewModelProviders.of(this).get(InfoViewModel.class);
        final Observer<String> observer = info->binding.ivLiveData.setText(info);
        model.getCurrentInfo().observe(this,observer);

        binding.ivLiveData.setOnClickListener(view-> model.getCurrentInfo().setValue("new String hahahahaha"));
    }
}
