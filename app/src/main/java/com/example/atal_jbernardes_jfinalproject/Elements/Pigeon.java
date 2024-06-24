package com.example.atal_jbernardes_jfinalproject.Elements;

import java.util.Date;

public class Pigeon {

    private String pigeonId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    private String parentId = null;

    public Pigeon(){

    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    private String userId, content;

    private Date timestamp;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    private int likeCount;

    public Pigeon(String userId, String content) {
        this.userId = userId;
        this.content = content;
        this.likeCount = 0;
        timestamp = new Date();
    }


    public String getPigeonId() {
        return pigeonId;
    }

    public void setPigeonId(String pigeonId) {
        this.pigeonId = pigeonId;
    }
}
