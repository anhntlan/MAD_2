package com.example.hipenjava.Activities.Courses;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.R;
import com.example.hipenjava.models.Course;
import com.example.hipenjava.models.CourseAdapter;
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

public class CourseCompletedActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> completedCourseList = new ArrayList<>();

    private TextView tvNoCompletedCourse;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_course);

        ImageButton btnBack = findViewById(R.id.btnBackCompletedCourse);
         tvNoCompletedCourse = findViewById(R.id.tvNoCompletedCourse);

        recyclerView = findViewById(R.id.recyclerViewCompletedCourse);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(this, completedCourseList);
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadcompletedCourses(tvNoCompletedCourse);
    }

    private void loadcompletedCourses(TextView tvNoCourses) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) {
            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference lessonsRef = FirebaseDatabase.getInstance().getReference("lesson");
        DatabaseReference userLessonsRef = FirebaseDatabase.getInstance().getReference("user_lessons").child(currentUserId);
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference("courses");


        lessonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot lessonsSnapshot) {
                userLessonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userLessonsSnapshot) {
//                        HashMap<Integer, List<Integer>> courseLessonsMap = new HashMap<>();
                        List<Integer> completedCourseIds = new ArrayList<>();

                        // Group lessons by courseID
                        for (DataSnapshot lessonSnapshot : lessonsSnapshot.getChildren()) {
                            int courseId = lessonSnapshot.child("courseID").getValue(Integer.class);
                            int lessonId = lessonSnapshot.child("id").getValue(Integer.class);

//                            courseLessonsMap.putIfAbsent(courseId, new ArrayList<>());
//                            courseLessonsMap.get(courseId).add(lessonId);

                            if (userLessonsSnapshot.hasChild(String.valueOf(lessonId))) {
                                if (!completedCourseIds.contains(courseId)) {
                                    completedCourseIds.add(courseId);
                                }
                            }
                        }
                        // Check if all lessons of a course are completed
//

                        // Load completed courses
                        loadCompletedCoursesDetails(completedCourseIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CourseCompletedActivity.this, "Lỗi tải danh sách bài học đã hoàn thành", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourseCompletedActivity.this, "Lỗi tải danh sách bài học", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCompletedCoursesDetails(List<Integer> completedCourseIds) {
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference("courses");
        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                completedCourseList.clear();
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null && completedCourseIds.contains(course.getId())) {
                        completedCourseList.add(course);
                    }
                }

                adapter.notifyDataSetChanged();
                tvNoCompletedCourse.setVisibility(completedCourseList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourseCompletedActivity.this, "Lỗi tải danh sách khóa học", Toast.LENGTH_SHORT).show();
            }
        });

    }


}