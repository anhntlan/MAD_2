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

import com.example.hipenjava.Activities.HomeActivity;
import com.example.hipenjava.Activities.MenuActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.R;
import com.example.hipenjava.models.Course;
import com.example.hipenjava.models.CourseAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourseHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;

    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> courseList;
    private List<Course> filteredList = new ArrayList<>();
    private EditText searchEditText;
    private DatabaseReference courseRef;
    private ImageButton btnNotification,btnMenu;

    private List<Course> continueLearningList = new ArrayList<>();
    private CardView allLevelFilter, beginnerFilter, intermediateFilter, advancedFilter;
    private String currentLevel = "all";
    private RecyclerView recyclerViewContinueLearning;
    private CourseAdapter continueLearningAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_home); // tạo layout này chứa RecyclerView

        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseList = new ArrayList<>();
        adapter = new CourseAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        // continueLearning courses
        TextView continueLearningLabel = findViewById(R.id.continueLearningLabel);
        ImageView continueLearningArrow = findViewById(R.id.continueLearningArrow);

        View.OnClickListener continueLearningClickListener = v -> {
            Intent intent = new Intent(CourseHomeActivity.this, ContinueLearningActivity.class);
            startActivity(intent);
        };

        continueLearningLabel.setOnClickListener(continueLearningClickListener);
        continueLearningArrow.setOnClickListener(continueLearningClickListener);

        // 1 continue courses
        recyclerViewContinueLearning = findViewById(R.id.recyclerViewContinueLearning);
        recyclerViewContinueLearning.setLayoutManager(new LinearLayoutManager(this));
        continueLearningAdapter = new CourseAdapter(this, continueLearningList);
        recyclerViewContinueLearning.setAdapter(continueLearningAdapter);
        loadContinueLearningCourses();

        //click to go to course list
        TextView coursesLabel = findViewById(R.id.coursesLabel);
        ImageView allCourseArrow = findViewById(R.id.coursesArrow);

        View.OnClickListener courseListClickListener = v -> {
            Intent intent = new Intent(CourseHomeActivity.this, CourseListActivity.class);
            startActivity(intent);
        };

        coursesLabel.setOnClickListener(courseListClickListener);
        allCourseArrow.setOnClickListener(courseListClickListener);


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
        loadCourses();
    }
    private void loadContinueLearningCourses() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null){
            Log.e("ContinueLearning", "User not logged in");
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("course_user");
        ref.orderByChild("userID").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        continueLearningList.clear();
                        int count =0;
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if(count >= 2) break; // Limit to 5 courses
                            Integer courseId = data.child("courseID").getValue(Integer.class);
                            Log.d("ContinueLearning", "Found courseID: " + courseId);

                            if (courseId != null) {
                                for (Course course : courseList) {
                                    if (course.getId() == courseId) {
                                        continueLearningList.add(course);
                                        count++;
                                        break;
                                    }
                                }
                            }
                        }
                        continueLearningAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CourseHomeActivity.this, "Lỗi tải danh sách tiếp tục học", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void loadCourses() {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses");

        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                int count = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(count >= 2) break; // Limit to 5 courses

                    Course course = dataSnapshot.getValue(Course.class);
                    courseList.add(course);
                    count++;

                }
                filteredList.clear();
                filteredList.addAll(courseList);
                adapter.notifyDataSetChanged();

               loadContinueLearningCourses();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }



}
