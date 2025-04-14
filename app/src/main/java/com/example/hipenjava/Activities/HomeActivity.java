package com.example.hipenjava.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.Activities.BaseActivity;
import com.example.hipenjava.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends BaseActivity {
    private ImageButton btnNotification,btnMenu;
    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // both header, bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.contentFrame));

        btnNotification = findViewById(R.id.btnNotification);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MenuActivity.class);
            startActivity(intent);
        });
        LinearLayout navigationCourses = findViewById(R.id.navigation_courses_home);

        navigationCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
            }
        });
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_courses) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
//            }           else if (id == R.id.navigation_home) {
//                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
//                startActivity(intent);
//                return true;
            } else if (id == R.id.navigation_draw) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_challenge) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_community) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

}
