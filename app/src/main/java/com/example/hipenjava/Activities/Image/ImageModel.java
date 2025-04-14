package com.example.hipenjava.Activities.Image;

import com.google.firebase.Timestamp;

public class ImageModel {
    private String id;         // Firebase key
    private String name;
    private String imageUrl;   // URL từ Cloudinary
    private Timestamp date;    // yyyy-MM-dd
    private String userId;     // ID người dùng Firebase

    public ImageModel() {
        // Firebase yêu cầu constructor rỗng
    }

    public ImageModel(String id, String name, String imageUrl, Timestamp date, String userId) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.date = date;
        this.userId = userId;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Hàm định dạng thời gian theo kiểu "Hôm nay", "1 ngày trước", "X ngày trước"
    public String getFormattedDate() {
        return DateUtils.getRelativeTime(date); // Dùng DateUtils để lấy thời gian tương đối
    }
}
