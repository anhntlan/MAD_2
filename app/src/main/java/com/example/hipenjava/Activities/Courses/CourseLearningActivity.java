package com.example.hipenjava.Activities.Courses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.R;
import com.example.hipenjava.models.Lesson;
import com.example.hipenjava.models.LessonAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class CourseLearningActivity extends AppCompatActivity {

        private RecyclerView recyclerView;
        private List<Lesson> lessonList = new ArrayList<>();
        private LessonAdapter adapter;
        private int courseID;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_course_learning);

            ImageButton btnBack = findViewById(R.id.btnBackLessonlist);

            courseID = getIntent().getIntExtra("courseID", -1);
//            recyclerView = findViewById(R.id.recyclerViewLessonofCourse);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LessonAdapter(this, lessonList);
//            recyclerView.setAdapter(adapter);

            loadLessons();
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Quay lại màn hình trước (CourseListActivity)
                }
            });
        }
        protected void onResume() {
            super.onResume();
            loadLessons(); // Reload lessons to update the UI
        }


    private void loadLessons() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lesson");
        DatabaseReference userLessonsRef = FirebaseDatabase.getInstance().getReference("user_lessons");
        String userId = FirebaseAuth.getInstance().getUid(); // Get the current user ID

        ref.orderByChild("courseID").equalTo(courseID)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    LinearLayout lessonContainer = findViewById(R.id.lessonContainer);
                    lessonContainer.removeAllViews(); // Clear previous views

                    userLessonsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Lesson lesson = data.getValue(Lesson.class);

                                // Create a CardView dynamically
                                CardView cardView = new CardView(CourseLearningActivity.this);
                                cardView.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));
                                // set background color
                                boolean isCompleted = userSnapshot.hasChild(String.valueOf(lesson.getId()));
                                cardView.setCardBackgroundColor(getResources().getColor(
                                        isCompleted ? R.color.completedLessonBackground : R.color.cardBackground
                                ));

                                cardView.setRadius(24);
                                cardView.setCardElevation(4);
                                cardView.setPadding(30, 30, 30, 30);
                                cardView.setUseCompatPadding(true);

                                // Create a RelativeLayout inside the CardView
                                RelativeLayout relativeLayout = new RelativeLayout(CourseLearningActivity.this);
                                relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                ));


                                // Create a LinearLayout for stacking lessonName and lessonDuration vertically
                                LinearLayout textContainer = new LinearLayout(CourseLearningActivity.this);
                                textContainer.setOrientation(LinearLayout.VERTICAL);
                                textContainer.setLayoutParams(new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                ));
                                textContainer.setPadding(44, 54, 44, 54);

                                // Add TextViews for lesson name and duration
                                TextView lessonName = new TextView(CourseLearningActivity.this);
                                lessonName.setText(lesson.getName());
                                lessonName.setTextSize(20);
                                lessonName.setTextColor(getResources().getColor(android.R.color.black));
                                lessonName.setTypeface(null, Typeface.BOLD);

                                TextView lessonDuration = new TextView(CourseLearningActivity.this);
                                lessonDuration.setText(lesson.getDuration() + " giờ");
                                lessonDuration.setTextSize(16);
                                lessonDuration.setTextColor(getResources().getColor(android.R.color.black));

                                // Add lessonName and lessonDuration to the LinearLayout
                                textContainer.addView(lessonName);
                                textContainer.addView(lessonDuration);
                                // Add TextViews for lesson name and duration
//                                TextView lessonName = new TextView(CourseLearningActivity.this);
//                                lessonName.setText(lesson.getName());
//                                lessonName.setTextSize(20);
//                                lessonName.setTextColor(getResources().getColor(android.R.color.black));
//                                lessonName.setTypeface(null, Typeface.BOLD);
//
//                                TextView lessonDuration = new TextView(CourseLearningActivity.this);
//                                lessonDuration.setText(lesson.getDuration() + " giờ");
//                                lessonDuration.setTextSize(16);
//                                lessonDuration.setTextColor(getResources().getColor(android.R.color.black));
//                                lessonDuration.setPadding(24,24,24,24);
                                // Add views to the layout
                                relativeLayout.addView(textContainer);


//                                // Add an ImageView for the arrow icon
//                                ImageView arrowIcon = new ImageView(CourseLearningActivity.this);
//                                arrowIcon.setImageResource(R.drawable.ic_chevron_right);
//                                arrowIcon.setColorFilter(getResources().getColor(android.R.color.black));
//                                RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(
//                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
//                                        RelativeLayout.LayoutParams.WRAP_CONTENT
//                                );
//                                arrowParams.addRule(RelativeLayout.ALIGN_PARENT_END);
//                                arrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
//                                arrowIcon.setLayoutParams(arrowParams);
//
//                                // Add the arrow icon to the RelativeLayout
//                                relativeLayout.addView(arrowIcon);


                                cardView.addView(relativeLayout);
                                lessonContainer.addView(cardView);

                                // Set click listener for the CardView
                                cardView.setOnClickListener(v -> {
                                    Intent intent = new Intent(CourseLearningActivity.this, LessonDetailActivity.class);
                                    intent.putExtra("lessonID", lesson.getId());
                                    intent.putExtra("lessonName", lesson.getName());
                                    startActivity(intent);
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
}