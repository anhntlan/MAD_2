package com.example.hipenjava.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.hipenjava.Activities.Post.MainActivityPost;
import com.example.hipenjava.R;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase if needed
        FirebaseApp.initializeApp(this);

        // Find the community button
        ImageButton btnCom = findViewById(R.id.btnCom);
        if (btnCom != null) {
            btnCom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainActivityPost.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("MainActivity", "btnCom not found in layout");
        }
    }
}
