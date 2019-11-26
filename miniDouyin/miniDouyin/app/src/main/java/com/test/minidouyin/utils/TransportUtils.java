package com.test.minidouyin.utils;

/**
 * EventBus的传输类，用来传递点击的item的信息
 *
 */
public class TransportUtils {

    public final int PALY = 1;

    public String videoURL;

    public String userName;

    public String studentId;

    public TransportUtils(String videoURL, String userName, String studentId) {
        this.videoURL = videoURL;
        this.userName = userName;
        this.studentId = studentId;
    }
}
