package com.example.hipenjava.models;


public class Notification {
    private int id;
    private String name;
    private String timeUp;
    private String detail;
    private String type;

    private boolean read;
    public Notification() {
    }


    public Notification(int id, String name, String detail, String timeUp,boolean read) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.timeUp = timeUp;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeUp() {
        return timeUp;
    }

    public void setTimeUp(String timeUp) {
        this.timeUp = timeUp;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}