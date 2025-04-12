package com.example.hipenjava.Activities.Courses;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hipenjava.R;
import com.example.hipenjava.models.LessonContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LessonDetailActivity extends AppCompatActivity {

    private TextView lessonNameText, lessonTextContent;
    private VideoView lessonVideoContent;
    private Button btnComplete;
    private ImageButton btnBack;
    private int lessonID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        lessonNameText = findViewById(R.id.lessonName);
        lessonTextContent = findViewById(R.id.lessonTextContent);
        lessonVideoContent = findViewById(R.id.lessonVideoContent);
        btnComplete = findViewById(R.id.btnComplete);
        btnBack = findViewById(R.id.btnBackLesson);

        lessonID = getIntent().getIntExtra("lessonID", -1);
        String lessonName = getIntent().getStringExtra("lessonName");
        lessonNameText.setText(lessonName);

        btnBack.setOnClickListener(v -> finish());

        btnComplete.setOnClickListener(v ->
                Toast.makeText(this, "Bài học đã hoàn thành!", Toast.LENGTH_SHORT).show()
        );

        loadLessonContent();
    }

    private void loadLessonContent() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lesson_content");

        ref.orderByChild("lessonID").equalTo(lessonID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            LessonContent content = data.getValue(LessonContent.class);
                            if (content.getType().equals("text")) {
                                lessonTextContent.setText(content.getDetail());
                                lessonTextContent.setVisibility(View.VISIBLE);
                            } else if (content.getType().equals("video")) {
                                Uri uri = Uri.parse(content.getDetail());
                                lessonVideoContent.setVideoURI(uri);
                                lessonVideoContent.setVisibility(View.VISIBLE);
                                lessonVideoContent.start();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
