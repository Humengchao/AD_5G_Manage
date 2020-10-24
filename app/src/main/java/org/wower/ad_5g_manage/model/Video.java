package org.wower.ad_5g_manage.model;

import java.io.Serializable;

public class Video implements Serializable {
    private int id;
    private String url;
    private String uploadTime;
    private int belong;
    private int playedNumber;
    private String videoName;
    private int level;  // 视频的优先级


    public Video() {
    }

    public Video(String url, int belong, String videoName, int level) {
        this.url = url;
        this.belong = belong;
        this.videoName = videoName;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getBelong() {
        return belong;
    }

    public void setBelong(int belong) {
        this.belong = belong;
    }

    public int getPlayedNumber() {
        return playedNumber;
    }

    public void setPlayedNumber(int playedNumber) {
        this.playedNumber = playedNumber;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
