//package com.example.hipenjava.Activities.Courses;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.bumptech.glide.Glide;
//import com.example.hipenjava.Activities.HomeActivity;
//import com.example.hipenjava.R;
//import com.example.hipenjava.models.Course;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class CourseDetailActivity extends AppCompatActivity {
//
//    private ImageView courseImageView;
//    private TextView courseTitleTextView;
//    private TextView courseDurationTextView;
//    private TextView courseLevelTextView;
//    private TextView courseLessonsCountTextView;
//    private TextView courseDescriptionTextView;
//    private Button enrollButton;
//
//    private DatabaseReference databaseReference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.course_detail);
//
//        // Khởi tạo các thành phần
//        courseImageView = findViewById(R.id.courseImage);
//        courseTitleTextView = findViewById(R.id.courseTitle);
//        courseDurationTextView = findViewById(R.id.courseDuration);
//        courseLevelTextView = findViewById(R.id.courseLevel);
//        courseLessonsCountTextView = findViewById(R.id.courseLessonsCount);
//        courseDescriptionTextView = findViewById(R.id.courseDescription);
//        enrollButton = findViewById(R.id.enrollButton);
//
//        // Lấy dữ liệu khóa học từ Intent
//        Intent intent = getIntent();
//        String courseId = intent.getStringExtra("courseId");
//
//
//        if (courseId == null || courseId.isEmpty()) {
//            Log.e("CourseDetailActivity", "courseId is null or empty");
//            Toast.makeText(this, "Không thể tải thông tin khóa học", Toast.LENGTH_SHORT).show();
//            finish(); // Đóng Activity nếu không có courseId
//            return;
//        }
//
//
//        // Lấy reference đến node "courses" trong Firebase
//        databaseReference = FirebaseDatabase.getInstance().getReference("courses").child(courseId);
//
//        // Đọc dữ liệu từ Firebase
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    Course course = dataSnapshot.getValue(Course.class);
//                    if (course != null) {
//                        // Hiển thị thông tin khóa học
//                        courseTitleTextView.setText(course.getName());
//                        courseDurationTextView.setText(course.getDuration());
//                        courseLevelTextView.setText(course.getLevel());
//                        courseLessonsCountTextView.setText(String.valueOf(course.getLessonCount()));
//                        courseDescriptionTextView.setText(course.getDescription());
//
//                        // Tải hình ảnh từ URL
//                        Glide.with(CourseDetailActivity.this)
//                                .load(course.getImage())
//                                .into(courseImageView);
//                    }
//                } else {
//                    Log.e("CourseDetailActivity", "Khóa học không tồn tại");
//                    Toast.makeText(CourseDetailActivity.this, "Khóa học không tồn tại", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("CourseDetailActivity", "Lỗi khi đọc dữ liệu từ Firebase", databaseError.toException());
//            }
//        });
//
//        // Xử lý nút đăng ký học
////        enrollButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                // Chuyển sang trang tiến độ khóa học
////                Intent intent = new Intent(CourseDetailActivity.this, HomeActivity.class);
////                intent.putExtra("courseId", courseId);
////                startActivity(intent);
////            }
////        });
//    }
//}
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

import java.util.HashMap;

public class CourseDetailActivity extends AppCompatActivity {

    TextView courseName, courseDuration, courseLevel, courseLessonNum, courseDescription;
    ImageView courseImage;
    Button enrollBtn;
    int selectedCourseId;


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
                                enrollBtn.setText("Tiếp tục học");
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




