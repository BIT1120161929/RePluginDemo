package com.test.minidouyin.network.service;

import com.test.minidouyin.network.beans.FeedsResponse;
import com.test.minidouyin.network.beans.PostVideoResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * Post和Get 方法
 */
public interface VideoListService {
    // url: (POST) http://test.androidcamp.bytedance.com/mini_douyin/invoke/video?student_id={student_id}&user_name={user_name}
    // body
    // + cover_image，file
    // + video, file
    // response
    // {
    //    "result": {},
    //    "url": "https://lf1-hscdn-tos.pstatp
    //    .com/obj/developer-baas/baas/tt7217xbo2wz3cem41/a8efa55c5c22de69_1560563154288.mp4",
    //    "success": true
    //}

    @Multipart
    @POST("mini_douyin/invoke/video")
    Call<PostVideoResponse> postVideo(
            @Query("student_id")String studentId,
            @Query("user_name")String userName,
            @Part MultipartBody.Part image, @Part MultipartBody.Part video);

    // response
    // {
    //    "feeds":[
    //        {
    //            "student_id":"2220186666",
    //            "user_name":"doudou",
    //            "image_url":"https://sf6-hscdn-tos.pstatp
    //            .com/obj/developer-baas/baas/tt7217xbo2wz3cem41/9c6bbc2aa5355504_1560563154279
    //            .jpg",
    //            "_id":"5d044dd222e26f0024157401",
    //            "video_url":"https://lf1-hscdn-tos.pstatp
    //            .com/obj/developer-baas/baas/tt7217xbo2wz3cem41/a8efa55c5c22de69_1560563154288
    //            .mp4",
    //            "createdAt":"2019-06-15T01:45:54.368Z",
    //            "updatedAt":"2019-06-15T01:45:54.368Z",
    //        }
    //        ...
    //    ],
    //    "success":true
    //}

    @GET("mini_douyin/invoke/video")
    Call<FeedsResponse> feedRequest();

}
