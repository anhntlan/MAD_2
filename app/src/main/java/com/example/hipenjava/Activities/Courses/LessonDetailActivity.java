package com.example.hipenjava.Activities.Courses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LessonDetailActivity extends AppCompatActivity {

    private TextView lessonNameText, lessonTextContent;
    private VideoView lessonVideoContent;
    private Button btnComplete;
    private ImageButton btnBack;
    private int lessonID;
    ImageButton fullscreenBtn ;
    RelativeLayout videocntainer;


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

        videocntainer = findViewById(R.id.videoContainer);
        fullscreenBtn = findViewById(R.id.fullscreenBtn);
        btnBack.setOnClickListener(v -> finish());

        btnComplete.setOnClickListener(v ->{
            String userId = FirebaseAuth.getInstance().getUid();

            DatabaseReference userLessonsRef = FirebaseDatabase.getInstance().getReference("user_lessons");

            // Lưu trạng thái hoàn thành bài học
            userLessonsRef.child(userId).child(String.valueOf(lessonID)).setValue(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
//                            Toast.makeText(this, "Bài học đã hoàn thành!", Toast.LENGTH_SHORT).show();
                            finish(); // Quay lại màn hình CourseLearningActivity
                        } else {
                            Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });


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
                       
                            if ("text".equals(content.getType())) {
                                videocntainer.setVisibility(View.GONE);
                                lessonTextContent.setVisibility(View.VISIBLE);
                                String url = content.getDetail();
                                loadWebContent(url);

                            }else if (content.getType().equals("video")) {
                                videocntainer.setVisibility(View.VISIBLE);
                                lessonTextContent.setVisibility(View.GONE);
                                String videoUrl = content.getDetail();
                                fullscreenBtn.setVisibility(View.VISIBLE);
                                fullscreenBtn.setOnClickListener(v -> {
                                    Intent intent = new Intent(LessonDetailActivity.this, FullScreenVideoActivity.class);
                                    intent.putExtra("video_url", videoUrl);
                                    startActivity(intent);
                                });
                                Uri uri = Uri.parse(content.getDetail());
                                lessonVideoContent.setVideoURI(uri);
                                lessonVideoContent.setVisibility(View.VISIBLE);
                                lessonVideoContent.start();

                                MediaController mediaController = new MediaController(LessonDetailActivity.this);
                                mediaController.setAnchorView(lessonVideoContent);

                                lessonVideoContent.setMediaController(mediaController);
                                lessonVideoContent.requestFocus();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
    private void loadWebContent(String url) {
        new Thread(() -> {
            try {
                URL webUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) webUrl.openConnection();
                connection.setConnectTimeout(5000);
                connection.connect();

                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder htmlContent = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    htmlContent.append(line);
                }

                reader.close();
                connection.disconnect();

                runOnUiThread(() -> {

                    lessonTextContent.setText(Html.fromHtml(htmlContent.toString(),
                            Html.FROM_HTML_MODE_LEGACY));
                    lessonTextContent.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(LessonDetailActivity.this,
                            "Không thể tải nội dung bài học", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

}
