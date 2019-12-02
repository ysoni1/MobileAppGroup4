package com.gsu.csc.petman;

import android.app.Application;

public class GlobalVariables extends Application {

    private String accessToken;
    private int userId;
    private String username;
    private String email;

    public static final String TAG = "petman";

    public int getUserId() {
        return userId;
    }

    public void setUserId(int ownerId) {
        this.userId = ownerId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void reset() {
        this.accessToken = null;
        this.userId = -1;
        this.username = null;
        this.email = null;
    }


}
