package com.example.hipenjava.models;

public class Lesson {
    private String name;
    private int duration;
    private int id, couseId;
    private String description;

    // Constructor, getter và setter
    public Lesson() {
    }

    public Lesson(String name, int duration, int id, String description, int couseId) {
        this.name = name;
        this.duration = duration;
        this.id = id;
        this.description = description;
        this.couseId = couseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public int getCouseId() {
        return couseId;
    }
    public void setCouseId(int couseId) {
        this.couseId = couseId;
    }
}
