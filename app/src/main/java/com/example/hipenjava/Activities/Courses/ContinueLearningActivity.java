package com.example.hipenjava.Activities.Courses;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.List;

public class ContinueLearningActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> continueLearningList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_learning);

        ImageButton btnBack = findViewById(R.id.btnBackContinueLearning);
        TextView tvNoCourses = findViewById(R.id.tvNoCourses);

        recyclerView = findViewById(R.id.recyclerViewContinueLearning);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(this, continueLearningList);
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadContinueLearningCourses(tvNoCourses);
    }

    private void loadContinueLearningCourses(TextView tvNoCourses) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) {
            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("course_user");
        ref.orderByChild("userID").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        getAllCourses(allCourses -> {
                            continueLearningList.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Integer courseId = data.child("courseID").getValue(Integer.class);
                                if (courseId != null) {
                                    for (Course course : allCourses) {
                                        if (course.getId() == courseId) {
                                            continueLearningList.add(course);
                                            break;
                                        }
                                    }
                                }
                            }
                        adapter.notifyDataSetChanged();
                        tvNoCourses.setVisibility(continueLearningList.isEmpty() ? View.VISIBLE : View.GONE);

                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ContinueLearningActivity.this, "Lỗi tải danh sách tiếp tục học", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAllCourses(OnCoursesLoadedListener listener) {
        List<Course> allCourses = new ArrayList<>();
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses");

        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Course course = data.getValue(Course.class);
                    if (course != null) {
                        allCourses.add(course);
                    }
                }
                listener.onCoursesLoaded(allCourses);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ContinueLearningActivity.this, "Lỗi tải danh sách khóa học", Toast.LENGTH_SHORT).show();
            }
        });
//        return allCourses;
    }
    interface OnCoursesLoadedListener {
        void onCoursesLoaded(List<Course> courses);
    }
}