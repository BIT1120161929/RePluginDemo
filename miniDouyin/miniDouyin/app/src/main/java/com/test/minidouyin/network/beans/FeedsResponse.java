package com.test.minidouyin.network.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;

@Getter
public class FeedsResponse {

    @SerializedName("feeds")
    private List<Feed> feeds;
    @SerializedName("success")
    private boolean success;
}
