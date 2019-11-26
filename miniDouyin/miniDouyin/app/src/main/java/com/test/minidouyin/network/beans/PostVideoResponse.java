package com.test.minidouyin.network.beans;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

import lombok.Getter;

@Getter
public class PostVideoResponse {

    @SerializedName("success")
    private boolean success;
    @SerializedName("url")
    private URL url;
}