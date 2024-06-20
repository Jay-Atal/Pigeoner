package com.example.atal_jbernardes_jfinalproject.Elements;

public class User {
    private String userId;
    private String name;
    private String username;

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    private String bio;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String userCollectionId;

    public User(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String userId, String name, String username) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        bio = "I am a user on Pigeoner!";
    }
}
