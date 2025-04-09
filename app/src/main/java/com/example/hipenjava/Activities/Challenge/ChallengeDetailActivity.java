package com.example.hipenjava.Activities.Challenge;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hipenjava.R;

public class ChallengeDetailActivity extends AppCompatActivity {
    private TextView textTimer;
    private Button btnUpload, btnVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize UI elements
        textTimer = findViewById(R.id.textTimer);
        btnUpload = findViewById(R.id.btnUpload);
        btnVote = findViewById(R.id.btnVote);

        // Start Countdown Timer
        startCountdownTimer(6 * 60 * 60 * 1000); // 6 hours in milliseconds

        // Button Click Listeners
        btnUpload.setOnClickListener(v -> openUploadActivity());
        btnVote.setOnClickListener(v -> openVoteActivity());
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
                textTimer.setText("Hết thời gian!");
            }
        }.start();
    }

    private void openUploadActivity() {
        Intent intent = new Intent(this, UploadArtworkActivity.class);
        startActivity(intent);
    }

    private void openVoteActivity() {
        Intent intent = new Intent(this, VoteActivity.class);
        startActivity(intent);
    }
}
