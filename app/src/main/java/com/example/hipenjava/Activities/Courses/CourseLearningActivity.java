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

    private LinearLayout lessonContainer;
    private int courseID;
    private boolean needsRefresh = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_learning);

        ImageButton btnBack = findViewById(R.id.btnBackLessonlist);
        lessonContainer = findViewById(R.id.lessonContainer);

        courseID = getIntent().getIntExtra("courseID", -1);

        loadLessons();

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needsRefresh) {
            loadLessons();
            needsRefresh = false;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Mark for refresh when returning from LessonDetailActivity
        needsRefresh = true;
    }

    private void loadLessons() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lesson");
        DatabaseReference userLessonsRef = FirebaseDatabase.getInstance().getReference("user_lessons");
        String userId = FirebaseAuth.getInstance().getUid();

        // Always clear the container first to prevent duplicates
        if (lessonContainer != null) {
            lessonContainer.removeAllViews();
        }

        ref.orderByChild("courseID").equalTo(courseID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userLessonsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Lesson lesson = data.getValue(Lesson.class);
                                    if (lesson != null) {
                                        boolean isCompleted = userSnapshot.hasChild(String.valueOf(lesson.getId()));
                                        addLessonCard(lesson, isCompleted);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void addLessonCard(Lesson lesson, boolean isCompleted) {
        // Create a CardView dynamically
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardView.setCardBackgroundColor(getResources().getColor(
                isCompleted ? R.color.darkPink : R.color.cardBackground
        ));
        cardView.setRadius(24);
        cardView.setCardElevation(4);
        cardView.setPadding(30, 30, 30, 30);
        cardView.setUseCompatPadding(true);

        // Create a RelativeLayout inside the CardView
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));

        // Create a LinearLayout for stacking lessonName and lessonDuration vertically
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setPadding(44, 54, 44, 54);

        // Add TextViews for lesson name and duration
        TextView lessonName = new TextView(this);
        lessonName.setText(lesson.getName());
        lessonName.setTextSize(20);
        lessonName.setTextColor(getResources().getColor(android.R.color.black));
        lessonName.setTypeface(null, Typeface.BOLD);

        TextView lessonDuration = new TextView(this);
        lessonDuration.setText(lesson.getDuration() + " giờ");
        lessonDuration.setTextSize(16);
        lessonDuration.setTextColor(getResources().getColor(android.R.color.black));

        // Add lessonName and lessonDuration to the LinearLayout
        textContainer.addView(lessonName);
        textContainer.addView(lessonDuration);

        // Add the text container to the RelativeLayout
        relativeLayout.addView(textContainer);

        // Add the RelativeLayout to the CardView
        cardView.addView(relativeLayout);

        // Add the CardView to the lesson container
        lessonContainer.addView(cardView);

        // Set click listener
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, LessonDetailActivity.class);
            intent.putExtra("lessonID", lesson.getId());
            intent.putExtra("lessonName", lesson.getName());
            intent.putExtra("courseID", courseID);
            startActivity(intent);
        });
    }
}
