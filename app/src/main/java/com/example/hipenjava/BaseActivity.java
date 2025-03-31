package com.example.hipenjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNavigationView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.menu.bottom_nav_menu);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(this, HomeActivity.class));
                } else if (itemId == R.id.navigation_courses) {
                    startActivity(new Intent(this, CourseListActivity.class));
//                } else if (itemId == R.id.navigation_draw) {
//                    startActivity(new Intent(this, DrawActivity.class));
//                } else if (itemId == R.id.navigation_achievements) {
//                    startActivity(new Intent(this, AchievementsActivity.class));
//                } else if (itemId == R.id.navigation_community) {
//                    startActivity(new Intent(this, CommunityActivity.class));
                }

                finish(); // Close current activity to prevent stacking
                return true;
            });
        }
    }
}
