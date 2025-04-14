package com.example.hipenjava.Activities.Courses;

import android.os.Bundle;

import com.example.hipenjava.Activities.BaseActivity;

import com.example.hipenjava.R;

public class CourseListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        // both header, bottom navigation
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.contentFrame));
    }
}