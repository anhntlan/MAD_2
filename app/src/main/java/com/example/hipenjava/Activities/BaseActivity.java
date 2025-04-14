package com.example.hipenjava.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.Activities.Post.MainActivityPost;
import com.example.hipenjava.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected ImageButton btnMenu, btnNotification;
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base); // Ensure correct layout

        setupViews();
        setupListeners();
    }

    private void setupViews() {
        // Find header buttons
        btnMenu = findViewById(R.id.btnMenu);
        btnNotification = findViewById(R.id.btnNotification);

        // Find BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void setupListeners() {
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                Log.d("BaseActivity", "Menu Clicked");
                Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();
                openMenu();
            });
        } else {
            Log.e("BaseActivity", "btnMenu is NULL");
        }

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                Log.d("BaseActivity", "Notification Clicked");
                Toast.makeText(this, "Notification Clicked", Toast.LENGTH_SHORT).show();
                openNotifications();
            });
        } else {
            Log.e("BaseActivity", "btnNotification is NULL");
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d("BaseActivity", "Bottom Nav Clicked: " + item.getTitle());

            if (itemId == R.id.navigation_draw) {
                startActivity(new Intent(this, CourseListActivity.class));
                return true;
            } else if (itemId == R.id.navigation_courses) {
                startActivity(new Intent(this, CourseListActivity.class));
                return true;
            } else if (itemId == R.id.navigation_community) {
                startActivity(new Intent(this, MainActivityPost.class));
                return true;
            } else if (itemId == R.id.navigation_challenge) {
                startActivity(new Intent(this, CourseListActivity.class));
                return true;
            }

            return false;
        });

    }

    private void openMenu() {
        Log.d("BaseActivity", "openMenu() called");
        // TODO: Implement menu logic (e.g., open drawer)
    }

    private void openNotifications() {
        Log.d("BaseActivity", "openNotifications() called");
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }
}
