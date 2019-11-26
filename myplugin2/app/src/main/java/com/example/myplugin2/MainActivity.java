package com.example.myplugin2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.example.myplugin2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        Event move = new Event();
        move.event = view->{
            Intent intent = new Intent();
            intent.setClass(this,SecondActivity.class);
            startActivity(intent);
        };
        binding.setEvent(move);

    }
}


