package com.example.myplugin;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.myplugin.databinding.ViewTextBinding;
import com.qihoo360.replugin.RePlugin;

public class TextRecyclerView extends FrameLayout {
    ViewTextBinding binding;

    public TextRecyclerView(@NonNull Context context) {
        super(RePlugin.getPluginContext()==null?context:RePlugin.getPluginContext());
        init();
    }

    private void init(){
        if(RePlugin.getPluginContext()!=null){
            binding = DataBindingUtil.inflate(LayoutInflater.from(RePlugin.getPluginContext()),R.layout.view_text,this,true);
        }
        else{
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.view_text,this,true);
        }
    }

    public void setText(String string){
        binding.tvText.setText(string);
    }
    public void seButton(String string){
        binding.btnFunction.setText(string);
    }
}
