package com.example.familyshare;

public class Post {
    private String description;
    private String postImageUrl;
    private String username;
    private String time;

    public Post(String description, String postImageUrl, String username, String time) {
        this.description = description;
        this.postImageUrl = postImageUrl;
        this.username = username;
        this.time = time;
    }
    public Post(){}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
