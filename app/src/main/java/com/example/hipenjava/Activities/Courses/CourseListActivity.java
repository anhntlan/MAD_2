package com.example.hipenjava.Activities.Courses;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.Activities.HomeActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.R;
import com.example.hipenjava.models.Course;
import com.example.hipenjava.models.CourseAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;

    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> courseList;
    private List<Course> filteredList = new ArrayList<>();
    private EditText searchEditText;
    private DatabaseReference courseRef;
    private ImageButton btnNotification,btnMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list); // tạo layout này chứa RecyclerView

        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseList = new ArrayList<>();
//        adapter = new CourseAdapter(this, courseList);
//        recyclerView.setAdapter(adapter);
        adapter = new CourseAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        searchEditText = findViewById(R.id.searchEditText);
        loadCourses();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCourses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
//
//        courseRef = FirebaseDatabase.getInstance().getReference("courses");
//
//        courseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                courseList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Course course = dataSnapshot.getValue(Course.class);
//                    courseList.add(course);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(CourseListActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
//            }
//        });
        btnNotification = findViewById(R.id.btnNotification);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

           if (id == R.id.navigation_home) {
                Intent intent = new Intent(CourseListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_draw) {
                Intent intent = new Intent(CourseListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_challenge) {
                Intent intent = new Intent(CourseListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_community) {
                Intent intent = new Intent(CourseListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

    }
    private void loadCourses() {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses");

        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Course course = dataSnapshot.getValue(Course.class);
                    courseList.add(course);
                }
                filteredList.clear();
                filteredList.addAll(courseList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filterCourses(String keyword) {
        filteredList.clear();
        for (Course course : courseList) {
            if (course.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(course);
            }
        }
        adapter.notifyDataSetChanged();
    }

}
