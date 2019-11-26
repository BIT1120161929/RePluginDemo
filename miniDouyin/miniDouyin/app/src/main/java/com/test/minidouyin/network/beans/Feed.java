package com.test.minidouyin.network.beans;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

import lombok.Getter;

@Getter
public class Feed {

    @SerializedName("student_id")
    private String studentId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("video_url")
    private String videoURL;
}
