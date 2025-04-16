package com.example.hipenjava.models;

public class LessonContent {
    private int id;
    private int lessonID;
    private String detail;
    private String type;

    public LessonContent() {}

    public LessonContent(int id, int lessonID, String detail, String type) {
        this.id = id;
        this.lessonID = lessonID;
        this.detail = detail;
        this.type = type;
    }

    public int getId() { return id; }
    public int getLessonID() { return lessonID; }
    public String getDetail() { return detail; }
    public String getType() { return type; }
}
