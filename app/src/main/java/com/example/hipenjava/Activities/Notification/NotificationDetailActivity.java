package com.example.hipenjava.Activities.Notification;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hipenjava.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

//        String formattedDetail = Html.fromHtml(detail, Html.FROM_HTML_MODE_LEGACY).toString();
//        notificationDetail.setText(formattedDetail);
        notificationName.setText(name);
        loadWebContent(detail);
// Set the detail text and enable link clicking
//        notificationDetail.setText(Html.fromHtml(detail, Html.FROM_HTML_MODE_LEGACY));
        notificationDetail.setMovementMethod(LinkMovementMethod.getInstance()); // Enable link clicking


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
                    notificationDetail.setText(Html.fromHtml(htmlContent.toString(), Html.FROM_HTML_MODE_LEGACY, source -> {
                        final Drawable[] drawable = {null};
                        Thread imageLoaderThread = new Thread(() -> {
                            try {
                                InputStream inputStream = (InputStream) new URL(source).getContent();
                                drawable[0] = Drawable.createFromStream(inputStream, "src");
                                drawable[0].setBounds(0, 0, drawable[0].getIntrinsicWidth(), drawable[0].getIntrinsicHeight());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        imageLoaderThread.start();
                        try {
                            imageLoaderThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return drawable[0];
                    }, null));
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(NotificationDetailActivity.this, "Không thể tải nội dung thông báo", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

}