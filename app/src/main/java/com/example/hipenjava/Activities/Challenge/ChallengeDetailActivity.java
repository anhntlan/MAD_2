package com.example.hipenjava.Activities.Challenge;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
    private Button btnUpload, btnVote;

    private Toolbar toolbar;

    private Challenge challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

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

        Glide.with(ChallengeDetailActivity.this)
                .load(challenge.getImageUrl())
                .into(image);
        textView.setText(challenge.getDescription());

        // Initialize UI elements
        textTimer = findViewById(R.id.textTimer);
        btnUpload = findViewById(R.id.btnUpload);
        btnVote = findViewById(R.id.btnVote);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(challenge.getName());
        }

        // Button Click Listeners
        btnUpload.setOnClickListener(v -> openUploadActivity());
        btnVote.setOnClickListener(v -> openVoteActivity());

        // Start Countdown Timer
        long timeLeft = challenge.getTimeEndInLong() - System.currentTimeMillis();
        if (timeLeft <= 0){
            textTimer.setText("Đã kết thúc");

            btnUpload.setEnabled(false);   // Disable interaction
            btnUpload.setClickable(false); // Optional, extra safety

            btnUpload.setEnabled(false);   // Disable interaction
            btnUpload.setClickable(false); // Optional, extra safety

        }else{
            startCountdownTimer(timeLeft);
        }
    }
}
