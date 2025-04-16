package com.example.hipenjava.Activities.Notification;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hipenjava.R;

public class NotificationDetailActivity extends AppCompatActivity {
    private TextView notificationName, notificationDetail;
    private ImageButton btnBack;
    private ImageView imgClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        btnBack = findViewById(R.id.btnBackNotiDetail);
        notificationName = findViewById(R.id.notiName);
        notificationDetail = findViewById(R.id.notificationContent);

        String name = getIntent().getStringExtra("notificationName");
        String detail = getIntent().getStringExtra("notificationDetail");
        String type = getIntent().getStringExtra("notificationType");

        String formattedDetail = Html.fromHtml(detail, Html.FROM_HTML_MODE_LEGACY).toString();
        notificationDetail.setText(formattedDetail);
        notificationName.setText(name);


         imgClass = findViewById(R.id.imgClass);

        // Show image for "class" type
        if ("class".equals(type)) {
            imgClass.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước (CourseListActivity)
            }
        });
    }
}