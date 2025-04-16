package com.example.hipenjava.models;

public class Course {
    private String name;
    private int duration;
    private String level;
    private String description;
    private int lessonNum,id;
    //for list page
    private String image;


    public Course() {}
    public Course(String name,int id, int duration, String level, String description, int lessonNum,String image) {
        this.id=id;
        this.name = name;
        this.duration = duration;
        this.level = level;
        this.description = description;
        this.lessonNum = lessonNum;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLessonNum() {
        return lessonNum;
    }

    public void setLessonNum(int ls) {
        this.lessonNum = ls;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String imageUrl) {
        this.image = imageUrl;
    }
}

