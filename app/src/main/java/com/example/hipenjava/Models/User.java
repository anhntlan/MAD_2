package com.example.hipenjava.Models;

public class User {
    private String userName;
    private String userEmail;
    private String userId;
    private String address; // New field for user address
    private String fullName; // New field for user's full name
    private String avatar; // New field for user's avatar (URL or base64 encoded string)
    private String role; // New field for user's role (e.g., admin, user, etc.)

    private String password;

    // Default constructor (required for Firestore)
    public User() {
    }

    // Constructor to initialize all fields
    public User(String userName, String userEmail, String userId, String password, String address,
                String fullName, String avatar, String role) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
        this.password = password;
        this.address = address;
        this.fullName = fullName;
        this.avatar = avatar;
        this.role = role;
    }

    // Getters and Setters for each field

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

