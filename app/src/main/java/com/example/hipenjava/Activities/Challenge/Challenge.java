package com.example.hipenjava.Activities.Challenge;

public class Challenge {
    private String title;
    private String status;
    private int imageResId;

    public Challenge(String title, String status, int imageResId) {
        this.title = title;
        this.status = status;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public int getImageResId() { return imageResId; }
}
