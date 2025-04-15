package com.example.hipenjava.Activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.Image.EditImageActivity;
import com.example.hipenjava.Activities.Image.ImageActivity;
import com.example.hipenjava.Activities.Image.ImageAdapter;
import com.example.hipenjava.Activities.Image.ImageModel;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.Activities.BaseActivity;
import com.example.hipenjava.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.hipenjava.Activities.Image.ImageAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class HomeActivity extends BaseActivity {
    private ImageButton btnNotification,btnMenu;
    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // both header, bottom navigation
        loadLatestImages();
        bottomNavigation = findViewById(R.id.bottomNavigation);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.contentFrame));

        btnNotification = findViewById(R.id.btnNotification);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout navigationCourses = findViewById(R.id.navigation_courses_home);

        navigationCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout navigationImage = findViewById(R.id.navigation_image);

        navigationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ImageActivity.class);
                startActivity(intent);
            }
        });

        ImageView vv = findViewById(R.id.ViewImage);
        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ImageActivity.class);
                startActivity(intent);
            }
        });
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_courses) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
//            }           else if (id == R.id.navigation_home) {
//                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
//                startActivity(intent);
//                return true;
            } else if (id == R.id.navigation_draw) {
                Intent intent = new Intent(HomeActivity.this, ImageActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_challenge) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_community) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
    private final ImageAdapter.OnItemClickListener listener = new ImageAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(ImageModel imageModel) {
            loadImageAndNavigateToEdit(imageModel);
        }

        @Override
        public void onItemLongClick(ImageModel imageModel, int position) {

        }
    };
    private void loadImageAndNavigateToEdit(ImageModel imageModel) {
        String imageUrl = imageModel.getImageUrl();
        Log.d("Image URL", imageUrl);  // Log URL to check its validity

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)  // Placeholder image while loading
//                .error(R.drawable.error_image)  // Error image in case of failure
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            File file = saveImageToCache(resource, imageModel.getName());
                            navigateToEditImage(file.getAbsolutePath(), imageModel);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle image clearance if needed
                    }
                });
    }
    private File saveImageToCache(Bitmap bitmap, String imageName) throws IOException {
        File file = new File(getCacheDir(), imageName + "_edit.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        return file;
    }

    private void navigateToEditImage(String imagePath, ImageModel imageModel) {
        Intent intent = new Intent(HomeActivity.this, EditImageActivity.class);
        intent.putExtra("image_path", imagePath);
        intent.putExtra("image_name", imageModel.getName());
        intent.putExtra("image_id", imageModel.getId());
        intent.putExtra("is_new", false);
        startActivityForResult(intent, 1);

    }
    private void loadLatestImages() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView textNoImages = findViewById(R.id.textNoImages);
        ImageView image1 = findViewById(R.id.image1);
        ImageView image2 = findViewById(R.id.image2);
        ImageView image3 = findViewById(R.id.image3);

        // Ẩn cả 3 image khi chưa có dữ liệu
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);

        String currentUserId = user.getUid();
        CollectionReference imageRef = FirebaseFirestore.getInstance().collection("images");

        imageRef
                .whereEqualTo("userId", currentUserId)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(3) // Lấy tối đa 3 ảnh
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        textNoImages.setVisibility(View.VISIBLE);
                    } else {
                        textNoImages.setVisibility(View.GONE);

                        List<ImageView> imageViews = Arrays.asList(image1, image2, image3);
                        int index = 0;

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (index >= 3) break;

                            ImageModel image = doc.toObject(ImageModel.class);
                            String imageUrl = image.getImageUrl(); // Hoặc getImagePath() tùy bạn lưu

                            ImageView currentImageView = imageViews.get(index);
                            currentImageView.setVisibility(View.VISIBLE);

                            Glide.with(this)
                                    .load(imageUrl)
                                    .into(currentImageView);
                            currentImageView.setOnClickListener(v -> loadImageAndNavigateToEdit(image));
                            index++;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    textNoImages.setVisibility(View.VISIBLE);
                });
    }
    protected void onResume() {
        super.onResume();
        loadLatestImages(); // Reload after adding/editing
    }

}
