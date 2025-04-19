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

        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseList = new ArrayList<>();
        adapter = new CourseAdapter(CourseHomeActivity.this, filteredList);
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
        continueLearningAdapter = new CourseAdapter(CourseHomeActivity.this, continueLearningList);
        recyclerViewContinueLearning.setAdapter(continueLearningAdapter);
//        loadContinueLearningCourses();

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
        recyclerViewCompletedLearning = findViewById(R.id.recyclerViewCompletedCourses);
        recyclerViewCompletedLearning.setLayoutManager(new LinearLayoutManager(this));
        completedCourseAdapter = new CourseAdapter(CourseHomeActivity.this, completedCourseList);
        recyclerViewCompletedLearning.setAdapter(completedCourseAdapter);
//        loadCompletedCourses();

        loadCourses();
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

    private void loadCompletedCourses() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) {
            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference lessonsRef = FirebaseDatabase.getInstance().getReference("lesson");
        DatabaseReference userLessonsRef = FirebaseDatabase.getInstance().getReference("user_lessons").child(currentUserId);

        lessonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot lessonsSnapshot) {
                userLessonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userLessonsSnapshot) {
                        HashMap<Integer, List<Integer>> courseLessonsMap = new HashMap<>();
                        List<Integer> completedCourseIds = new ArrayList<>();

                        // Group lessons by courseID
                        for (DataSnapshot lessonSnapshot : lessonsSnapshot.getChildren()) {
                            int courseId = lessonSnapshot.child("courseID").getValue(Integer.class);
                            int lessonId = lessonSnapshot.child("id").getValue(Integer.class);

                            courseLessonsMap.putIfAbsent(courseId, new ArrayList<>());
                            courseLessonsMap.get(courseId).add(lessonId);
                        }

                        // Check if all lessons of a course are completed
                        for (Map.Entry<Integer, List<Integer>> entry : courseLessonsMap.entrySet()) {
                            int courseId = entry.getKey();
                            List<Integer> lessonIds = entry.getValue();

                            boolean allCompleted = true;
                            for (int lessonId : lessonIds) {
                                if (!userLessonsSnapshot.hasChild(String.valueOf(lessonId))) {
                                    allCompleted = false;
                                    break;
                                }
                            }

                            if (allCompleted) {
                                completedCourseIds.add(courseId);
                                break;

                            }
                        }

                        // Load completed courses
                        loadCompletedCoursesDetails(completedCourseIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CourseHomeActivity.this, "Lỗi tải danh sách bài học đã hoàn thành", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourseHomeActivity.this, "Lỗi tải danh sách bài học", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCompletedCoursesDetails(List<Integer> completedCourseIds) {
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference("courses");
        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Course> completedCourses = new ArrayList<>();
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null && completedCourseIds.contains(course.getId())) {
                        completedCourses.add(course);
                    }
                }

                displayCompletedCourses(completedCourses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourseHomeActivity.this, "Lỗi tải danh sách khóa học", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCompletedCourses(List<Course> completedCourses) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCompletedCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CourseAdapter adapter = new CourseAdapter(CourseHomeActivity.this, completedCourses);
        recyclerView.setAdapter(adapter);
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

        DatabaseReference lessonsRef = FirebaseDatabase.getInstance().getReference("lesson");
        DatabaseReference userLessonsRef = FirebaseDatabase.getInstance().getReference("user_lessons").child(currentUserId);

        lessonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot lessonsSnapshot) {
                userLessonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userLessonsSnapshot) {
                        HashMap<Integer, List<Integer>> courseLessonsMap = new HashMap<>();
                        continueLearningList.clear();

                        // Group lessons by courseID
                        for (DataSnapshot lessonSnapshot : lessonsSnapshot.getChildren()) {
                            Integer courseId = lessonSnapshot.child("courseID").getValue(Integer.class);
                            Integer lessonId = lessonSnapshot.child("id").getValue(Integer.class);


                            if (courseId != null && lessonId != null) {
                                courseLessonsMap.putIfAbsent(courseId, new ArrayList<>());
                                courseLessonsMap.get(courseId).add(lessonId);
                            }
//                            courseLessonsMap.putIfAbsent(courseId, new ArrayList<>());
//                            courseLessonsMap.get(courseId).add(lessonId);
                        }

                        // Check courses where some lessons are completed but not all
                        int count = 0;
                        for (Map.Entry<Integer, List<Integer>> entry : courseLessonsMap.entrySet()) {
                            if(count>=1) break;
                            int courseId = entry.getKey();
                            List<Integer> lessonIds = entry.getValue();

                            boolean hasCompletedSome = false;
                            boolean hasUncompleted = false;

                            for (int lessonId : lessonIds) {
                                if (userLessonsSnapshot.hasChild(String.valueOf(lessonId))) {
                                    hasCompletedSome = true;
                                } else {
                                    hasUncompleted = true;
                                }
                                if (hasCompletedSome && hasUncompleted) {
                                    break;
                                }
                            }

                            if (hasCompletedSome && hasUncompleted) {
                                for (Course course : courseList) {
                                    if (course.getId() == courseId) {
                                        continueLearningList.add(course);
                                        count++;
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourseHomeActivity.this, "Lỗi tải danh sách bài học", Toast.LENGTH_SHORT).show();
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
                    if(count >= 1) break; // Limit to 5 courses

                    Course course = dataSnapshot.getValue(Course.class);
                    if (course != null) {
                        courseList.add(course);
                        count++;
                    }

                }
                filteredList.clear();
                filteredList.addAll(courseList);
                adapter.notifyDataSetChanged();

               loadContinueLearningCourses();
                loadCompletedCourses();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }



}
