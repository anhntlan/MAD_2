package com.example.hipenjava.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.Activities.BaseActivity;
import com.example.hipenjava.R;


public class HomeActivity extends BaseActivity {
    private ImageButton btnNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // both header, bottom navigation
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.contentFrame));

//        btnNotification = findViewById(R.id.btnNotification);
//
//        btnNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
//                startActivity(intent);
//            }
//        });

        LinearLayout navigationCourses = findViewById(R.id.navigation_courses_home);

        navigationCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
            }
        });
    }

    }
