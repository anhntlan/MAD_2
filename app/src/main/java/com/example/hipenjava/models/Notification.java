package com.example.hipenjava.models;


public class Notification {
    private int id;
    private String name,timeup;
    private String detail;
    private String type;

    public Notification() {
    }



    public Notification(int id, String name, String detail, String timeUp) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.timeup = timeUp;
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
        return timeup;
    }
    public void setTimeUp(String timeUp) {
        this.timeup = timeUp;
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