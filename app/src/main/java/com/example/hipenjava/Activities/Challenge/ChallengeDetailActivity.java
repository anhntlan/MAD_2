package com.example.hipenjava.Activities.Challenge;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.hipenjava.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChallengeDetailActivity extends AppCompatActivity {
    private TextView textTimer;
    private Button btnUpload, btnVote, btnResult;

    private ImageView btnBack;

    private Challenge challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        getChallengeFromIntent();
    }

    private void startCountdownTimer(long timeMillis) {
        new CountDownTimer(timeMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;
                textTimer.setText(String.format("Thời gian còn lại: %02d:%02d:%02d", hours, minutes, seconds));
            }

            public void onFinish() {
                textTimer.setText("Đã kết thúc");
            }
        }.start();
    }

    private void openUploadActivity() {
        Intent intent = new Intent(this, UploadArtworkActivity.class);
        intent.putExtra("challengeId", challenge.getId());
        startActivity(intent);
    }

    private void openVoteActivity() {
        Intent intent = new Intent(this, VoteActivity.class);
        intent.putExtra("challengeId", challenge.getId());
        startActivity(intent);
    }

    private void openResultActivity(){
        Intent intent = new Intent(this, ChallengeResultActivity.class);
        intent.putExtra("challengeId", challenge.getId());
        startActivity(intent);
    }

    private void getChallengeFromIntent(){
        String challengeId = getIntent().getStringExtra("challengeId");
        FirebaseFirestore.getInstance()
                .collection("challenge")
                .document(challengeId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String description = document.getString("description");
                        String timeStart = document.getString("timeStart");
                        String timeEnd = document.getString("timeEnd");
                        String imageUrl = document.getString("thumbNail");

                        challenge = new Challenge(challengeId, name, description, timeStart, timeEnd, imageUrl);
                        setupUIContent();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ChallengeDetail", "Failed to load challenge", e);
                });
    }

    private void setupUIContent(){
        ImageView image = findViewById(R.id.imageChallenge);
        TextView textView = findViewById(R.id.textDescription);
        TextView challengeName = findViewById(R.id.headerTitle);
        challengeName.setText(challenge.getName());

        Glide.with(ChallengeDetailActivity.this)
                .load(challenge.getImageUrl())
                .into(image);
        textView.setText(challenge.getDescription());

        // Initialize UI elements
        textTimer = findViewById(R.id.textTimer);
        btnUpload = findViewById(R.id.btnUpload);
        btnVote = findViewById(R.id.btnVote);
        btnResult = findViewById(R.id.btnResult);

        btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Or finish(); or custom logic
            }
        });

        // Button Click Listeners
        btnUpload.setOnClickListener(v -> openUploadActivity());
        btnVote.setOnClickListener(v -> openVoteActivity());
        btnResult.setOnClickListener(v -> openResultActivity());

        // Start Countdown Timer
        long timeLeft = challenge.getTimeEndInLong() - System.currentTimeMillis();
        if (timeLeft <= 0){
            textTimer.setText("Đã kết thúc");

            btnUpload.setEnabled(false);   // Disable interaction
            btnUpload.setClickable(false); // Optional, extra safety

            btnUpload.setEnabled(false);   // Disable interaction
            btnUpload.setClickable(false); // Optional, extra safety

            btnUpload.setVisibility(View.GONE);
            btnVote.setVisibility(View.GONE);
            btnResult.setVisibility(View.VISIBLE);

        }else{
            btnUpload.setVisibility(View.VISIBLE);
            btnVote.setVisibility(View.VISIBLE);
            btnResult.setVisibility(View.GONE);

            startCountdownTimer(timeLeft);
        }
    }
}
