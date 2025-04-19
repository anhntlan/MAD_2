package com.example.hipenjava.Activities.Courses;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.Activities.Auth.LoginActivity;
import com.example.hipenjava.Activities.HomeActivity;
import com.example.hipenjava.Activities.MenuActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.R;
import com.example.hipenjava.models.Course;
import com.example.hipenjava.models.CourseAdapter;
import com.example.hipenjava.models.Lesson;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;

    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> courseList;
    private List<Course> filteredList = new ArrayList<>();
    private EditText searchEditText;
    private DatabaseReference courseRef;
    private ImageButton btnNotification,btnMenu;

    private List<Course> continueLearningList = new ArrayList<>(), completedCourseList = new ArrayList<>();

    private CardView allLevelFilter, beginnerFilter, intermediateFilter, advancedFilter;
    private String currentLevel = "all";
    private RecyclerView recyclerViewContinueLearning, recyclerViewCompletedLearning;
    private CourseAdapter continueLearningAdapter,completedCourseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_home);


        courseList = new ArrayList<>();
        adapter = new CourseAdapter(CourseHomeActivity.this, filteredList);
//        recyclerView.setAdapter(adapter);

        // continueLearning courses
        TextView continueLearningLabel = findViewById(R.id.continueLearningLabel);
        ImageView continueLearningArrow = findViewById(R.id.continueLearningArrow);

        View.OnClickListener continueLearningClickListener = v -> {
            Intent intent = new Intent(CourseHomeActivity.this, ContinueLearningActivity.class);
            startActivity(intent);
        };

        continueLearningLabel.setOnClickListener(continueLearningClickListener);
        continueLearningArrow.setOnClickListener(continueLearningClickListener);



        //click to go to course list
        TextView coursesLabel = findViewById(R.id.coursesLabel);
        ImageView allCourseArrow = findViewById(R.id.coursesArrow);

        View.OnClickListener courseListClickListener = v -> {
            Intent intent = new Intent(CourseHomeActivity.this, CourseListActivity.class);
            startActivity(intent);
        };

        coursesLabel.setOnClickListener(courseListClickListener);
        allCourseArrow.setOnClickListener(courseListClickListener);

        // course completed
        TextView completedLearningLabel = findViewById(R.id.coursesCompletedLabel);
        ImageView completedLearningArrow = findViewById(R.id.coursesCompletedArrow);

        View.OnClickListener completedClickListener = v -> {
            Intent intent = new Intent(CourseHomeActivity.this, CourseCompletedActivity.class);

            startActivity(intent);
        };

        completedLearningLabel.setOnClickListener(completedClickListener);
        completedLearningArrow.setOnClickListener(completedClickListener);

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(CourseHomeActivity.this, MenuActivity.class);
            startActivity(intent);
        });


        // notification
        btnNotification = findViewById(R.id.btnNotification);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseHomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

           if (id == R.id.navigation_home) {
                Intent intent = new Intent(CourseHomeActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_draw) {
                Intent intent = new Intent(CourseHomeActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_challenge) {
                Intent intent = new Intent(CourseHomeActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_community) {
                Intent intent = new Intent(CourseHomeActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
