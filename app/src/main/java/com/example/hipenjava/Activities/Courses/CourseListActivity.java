package com.example.hipenjava.Activities.Courses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private DatabaseReference courseRef;
    private ImageButton btnNotification,btnMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list); // tạo layout này chứa RecyclerView

        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseList = new ArrayList<>();
        adapter = new CourseAdapter(this, courseList);
        recyclerView.setAdapter(adapter);

        courseRef = FirebaseDatabase.getInstance().getReference("courses");

        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Course course = dataSnapshot.getValue(Course.class);
                    courseList.add(course);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourseListActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
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
//        adapter.setOnItemClickListener(new CourseAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Course course) {
//                if (course != null && course.getId()!= 0) {
//                    Intent intent = new Intent(CourseListActivity.this, CourseDetailActivity.class);
//                    intent.putExtra("courseId", course.getId()); // Gửi ID khóa học
//                    startActivity(intent);
//                } else {
//                    Log.e("CourseListActivity", "courseId is null or empty");
//                    Toast.makeText(CourseListActivity.this, "Không thể tải thông tin khóa học", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

}
