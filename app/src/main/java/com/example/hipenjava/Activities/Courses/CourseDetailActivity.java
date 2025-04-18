
package com.example.hipenjava.Activities.Courses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.HomeActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {

    TextView courseName, courseDuration, courseLevel, courseLessonNum, courseDescription;
    ImageView courseImage;
    Button enrollBtn;
    int selectedCourseId;

    private boolean isUserEnrolled = false;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);


        courseName = findViewById(R.id.course_name);
        courseDuration = findViewById(R.id.course_duration);
        courseLevel = findViewById(R.id.course_level);
        courseLessonNum = findViewById(R.id.course_lessons);
        courseDescription = findViewById(R.id.course_description);
        courseImage = findViewById(R.id.course_image);
        enrollBtn = findViewById(R.id.enroll_button);

        // normal back button results in error for this page
        ImageButton btnBack = findViewById(R.id.btnBackCourseDetail);
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        selectedCourseId = getIntent().getIntExtra("courseID", -1);
        if (selectedCourseId == -1) {
            Toast.makeText(this, "Lỗi: courseID không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String name = intent.getStringExtra("name");
        int duration = intent.getIntExtra("duration", 0);
        String level = intent.getStringExtra("level");
        int lessonNum = intent.getIntExtra("lessonNum", 0);
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("image");

        courseName.setText(name);
        courseDuration.setText(String.valueOf(duration));
        courseLevel.setText(level);
        courseLessonNum.setText(String.valueOf(lessonNum));
        courseDescription.setText(description);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.img_placeholder)
                .into(courseImage);

        // Check if the user has already started the course
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) {
            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("course_user");
            ref.orderByChild("userID").equalTo(currentUserId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean hasStarted = false;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Integer courseId = data.child("courseID").getValue(Integer.class);
                                if (courseId != null && courseId == selectedCourseId) {
                                    hasStarted = true;
                                    break;
                                }
                            }

                            if (hasStarted) {

                                DatabaseReference lessonsRef = FirebaseDatabase.getInstance().getReference("lesson");
                                DatabaseReference userLessonsRef = FirebaseDatabase.getInstance().getReference("user_lessons").child(currentUserId);

                                lessonsRef.orderByChild("courseID").equalTo(selectedCourseId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot lessonsSnapshot) {
                                                List<Integer> lessonIds = new ArrayList<>();
                                                for (DataSnapshot lessonSnapshot : lessonsSnapshot.getChildren()) {
                                                    Integer lessonId = lessonSnapshot.child("id").getValue(Integer.class);
                                                    if (lessonId != null) {
                                                        lessonIds.add(lessonId);
                                                    }
                                                }

                                                userLessonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot userLessonsSnapshot) {
                                                        boolean allCompleted = lessonIds.stream().mapToInt(lessonId -> lessonId).allMatch(lessonId -> userLessonsSnapshot.hasChild(String.valueOf(lessonId)));

                                                        if (allCompleted) {
                                                            enrollBtn.setText("Xem lại bài đã học");
                                                        } else {
                                                            enrollBtn.setText("Tiếp tục học");
                                                        }

//                                                        enrollBtn.setOnClickListener(v -> {
//                                                            if (allCompleted) {
//                                                                // Navigate to a review activity or handle accordingly
//                                                                Toast.makeText(CourseDetailActivity.this, "Xem lại bài đã học", Toast.LENGTH_SHORT).show();
//                                                            } else {
//                                                                // Navigate to CourseLearningActivity
//                                                                Intent i = new Intent(CourseDetailActivity.this, CourseLearningActivity.class);
//                                                                i.putExtra("courseID", selectedCourseId);
//                                                                startActivity(i);
//                                                            }
//                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(CourseDetailActivity.this, "Lỗi khi kiểm tra trạng thái bài học!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(CourseDetailActivity.this, "Lỗi khi tải danh sách bài học!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                enrollBtn.setText("Bắt đầu học");
                            }

                            boolean finalHasStarted = hasStarted;
                            enrollBtn.setOnClickListener(v -> {
                                if (finalHasStarted) {
                                    // Navigate to CourseLearningActivity
                                    Intent i = new Intent(CourseDetailActivity.this, CourseLearningActivity.class);
                                    i.putExtra("courseID", selectedCourseId);
                                    startActivity(i);
                                } else {
                                    // Enroll the user in the course
                                    HashMap<String, Object> enrollData = new HashMap<>();
                                    enrollData.put("userID", currentUserId);
                                    enrollData.put("courseID", selectedCourseId);

                                    ref.push().setValue(enrollData)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(CourseDetailActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                                Intent i = new Intent(CourseDetailActivity.this, CourseLearningActivity.class);
                                                i.putExtra("courseID", selectedCourseId);
                                                startActivity(i);
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(CourseDetailActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show()
                                            );
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(CourseDetailActivity.this, "Lỗi khi kiểm tra trạng thái học!", Toast.LENGTH_SHORT).show();
                        }
                    });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Quay lại màn hình trước (CourseListActivity)
                }
            });

        }
    }




