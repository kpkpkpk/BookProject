package com.kp.bookproject.Entity;

import androidx.annotation.NonNull;

public class Account {
    private String username;
    private String imageUrl;

    public Account(String username, String imageUrl) {
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
