package com.kp.bookproject.Entity;

import java.util.ArrayList;

public class Account {
    private String username;
    private String imageUrl;
    private ArrayList<Integer> liked;
    private ArrayList<Integer> tags_id;

    public Account(String username, String imageUrl, ArrayList<Integer> liked, ArrayList<Integer> tags_id) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.liked = liked;
        this.tags_id = tags_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<Integer> getLiked() {
        return liked;
    }

    public void setLiked(ArrayList<Integer> liked) {
        this.liked = liked;
    }

    public ArrayList<Integer> getTags_id() {
        return tags_id;
    }

    public void setTags_id(ArrayList<Integer> tags_id) {
        this.tags_id = tags_id;
    }
}
