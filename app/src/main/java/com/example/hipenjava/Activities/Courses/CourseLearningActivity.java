package com.example.hipenjava.Activities.Courses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.R;
import com.example.hipenjava.models.Lesson;
import com.example.hipenjava.models.LessonAdapter;
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

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_course_learning);

            courseID = getIntent().getIntExtra("courseID", -1);
            recyclerView = findViewById(R.id.lessonRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LessonAdapter(this, lessonList);
            recyclerView.setAdapter(adapter);

            loadLessons();
        }

        private void loadLessons() {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lesson");
            ref.orderByChild("courseID").equalTo(courseID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            lessonList.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Lesson lesson = data.getValue(Lesson.class);
                                lessonList.add(lesson);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }
