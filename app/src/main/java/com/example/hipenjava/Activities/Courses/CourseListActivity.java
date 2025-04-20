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

public class CourseListActivity extends AppCompatActivity {

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
            setContentView(R.layout.course_list); // tạo layout này chứa RecyclerView

            recyclerView = findViewById(R.id.recyclerViewCoursesList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            courseList = new ArrayList<>();
            adapter = new CourseAdapter(this, filteredList);
            recyclerView.setAdapter(adapter);


//        SEARCH BAR
            searchEditText = findViewById(R.id.searchEditText);
            loadCourses();
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterCoursesName(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });




            //  filter buttons
            allLevelFilter = findViewById(R.id.allLevelFilter);
            beginnerFilter = findViewById(R.id.beginnerFilter);
            intermediateFilter = findViewById(R.id.intermediateFilter);
            advancedFilter = findViewById(R.id.advancedFilter);

            allLevelFilter.setOnClickListener(v -> {
                setActiveFilter(allLevelFilter);
                currentLevel = "all";
                filterCoursesLevel(searchEditText.getText().toString());
            });

            beginnerFilter.setOnClickListener(v -> {
                setActiveFilter(beginnerFilter);
                currentLevel = "cơ bản";
                filterCoursesLevel(searchEditText.getText().toString());
            });

            intermediateFilter.setOnClickListener(v -> {
                setActiveFilter(intermediateFilter);
                currentLevel = "trung bình";
                filterCoursesLevel(searchEditText.getText().toString());
            });

            advancedFilter.setOnClickListener(v -> {
                setActiveFilter(advancedFilter);
                currentLevel = "nâng cao";
                filterCoursesLevel(searchEditText.getText().toString());
            });


            bottomNavigation = findViewById(R.id.bottomNavigation);
            bottomNavigation.setSelectedItemId(R.id.navigation_courses);

            bottomNavigation.setOnNavigationItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(com.example.hipenjava.Activities.Courses.CourseListActivity.this, HomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_draw) {
                    Intent intent = new Intent(com.example.hipenjava.Activities.Courses.CourseListActivity.this, HomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_challenge) {
                    Intent intent = new Intent(com.example.hipenjava.Activities.Courses.CourseListActivity.this, HomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_community) {
                    Intent intent = new Intent(com.example.hipenjava.Activities.Courses.CourseListActivity.this, HomeActivity.class);
                    startActivity(intent);
                    return true;
                }
                else if (id == R.id.navigation_courses) {
                    Intent intent = new Intent(com.example.hipenjava.Activities.Courses.CourseListActivity.this, CourseHomeActivity.class);
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


        private void setActiveFilter(CardView activeFilter) {
            // Reset all filters
            allLevelFilter.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            beginnerFilter.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            intermediateFilter.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            advancedFilter.setCardBackgroundColor(getResources().getColor(android.R.color.white));

            // Set active filter
            activeFilter.setCardBackgroundColor(getResources().getColor(R.color.darkPink));
        }

        private void filterCoursesLevel(String keyword) {
            filteredList.clear();
            for (Course course : courseList) {
                boolean matchesKeyword = course.getName().toLowerCase().contains(keyword.toLowerCase());
                boolean matchesLevel = currentLevel.equals("all") || course.getLevel().toLowerCase().equals(currentLevel);

                if (matchesKeyword && matchesLevel) {
                    filteredList.add(course);
                }
            }
            adapter.notifyDataSetChanged();
        }
        private void filterCoursesName(String keyword) {
            filteredList.clear();
            for (Course course : courseList) {
                if (course.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(course);
                }
            }
            adapter.notifyDataSetChanged();
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

//                    loadContinueLearningCourses();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }



    }
