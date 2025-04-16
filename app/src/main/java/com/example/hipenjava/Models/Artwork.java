package com.example.hipenjava.Models;

import com.google.firebase.firestore.DocumentId;

public class Artwork {
    @DocumentId
    private String id;
    private String categoryId;
    private String title;
    private String artist;
    private String year;
    private String imageUrl;
    private String description;

    // Empty constructor required for Firestore
    public Artwork() {
    }

    public Artwork(String categoryId, String title, String artist, String year, String imageUrl, String description) {
        this.categoryId = categoryId;
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

/*
public class Artwork implements Serializable {
    @DocumentId
    private String id;
    private String title;
    private String artist;
    private String year;
    private String imageUrl;
    private String categoryId;
    private String description;

    public Artwork() {
        // Required empty constructor for Firebase
    }

    public Artwork(String id, String title, String artist, String year, String imageUrl, String categoryId, String description) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
*/
