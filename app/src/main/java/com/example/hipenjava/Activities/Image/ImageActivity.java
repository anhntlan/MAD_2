

package com.example.hipenjava.Activities.Image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.hipenjava.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<ImageModel> imageList;
    private ImageView btnAddNew, btnBack;
    BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_list);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.navigation_draw);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_courses) {
                Intent intent = new Intent(ImageActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_home) {
                Intent intent = new Intent(ImageActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
//            } else if (id == R.id.navigation_draw) {
//                Intent intent = new Intent(ImageActivity.this, ImageActivity.class);
//                startActivity(intent);
//                return true;
            } else if (id == R.id.navigation_challenge) {
                Intent intent = new Intent(ImageActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_community) {
                Intent intent = new Intent(ImageActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        initViews();
        setupRecyclerView();
        loadImages();
        setupListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnAddNew = findViewById(R.id.btnAddNew);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList, listener);
        recyclerView.setAdapter(imageAdapter);
    }

    private void setupListeners() {
        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(ImageActivity.this, EditImageActivity.class);
            intent.putExtra("is_new", true);
            startActivityForResult(intent, 1);
        });

        btnBack.setOnClickListener(v -> finish());
    }



    private void loadImages() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView textNoImages = findViewById(R.id.textNoImages); // Lấy TextView thông báo

        String currentUserId = user.getUid();
        CollectionReference imageRef = FirebaseFirestore.getInstance().collection("images");

        imageRef
                .whereEqualTo("userId", currentUserId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    imageList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        textNoImages.setVisibility(View.VISIBLE); // Hiện thông báo khi không có ảnh
                    } else {
                        textNoImages.setVisibility(View.GONE); // Ẩn thông báo nếu có ảnh
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            ImageModel image = doc.toObject(ImageModel.class);
                            image.setId(doc.getId());
                            imageList.add(image);
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    textNoImages.setVisibility(View.VISIBLE); // Hiện thông báo khi lỗi
                });
    }

    private final ImageAdapter.OnItemClickListener listener = new ImageAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(ImageModel imageModel) {
            loadImageAndNavigateToEdit(imageModel);
        }

        @Override
        public void onItemLongClick(ImageModel imageModel, int position) {
            showDeleteDialog(imageModel, position);
        }
    };

//    private void loadImageAndNavigateToEdit(ImageModel imageModel) {
//        String imageUrl = imageModel.getImageUrl();
//        Log.d("Image URL", imageUrl);  // Log URL to check its validity
//
//        Glide.with(this)
//                .asBitmap()
//                .load(imageUrl)
//                .placeholder(R.drawable.placeholder)  // Placeholder image while loading
////                .error(R.drawable.error_image)  // Error image in case of failure
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        try {
//                            File file = saveImageToCache(resource, imageModel.getName());
//                            navigateToEditImage(file.getAbsolutePath(), imageModel);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Toast.makeText(ImageActivity.this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        // Handle image clearance if needed
//                    }
//                });
//    }
private void loadImageAndNavigateToEdit(ImageModel imageModel) {
    String imageUrl = imageModel.getImageUrl();
    Log.d("Image URL", imageUrl);  // Log để kiểm tra đường dẫn ảnh

    Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)  // Ảnh tạm thời khi đang tải
//            .error(R.drawable.error_image)        // Ảnh lỗi nếu tải thất bại
            .into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    try {
                        // Lưu ảnh đã tải vào cache
                        File file = saveImageToCache(resource, imageModel.getName());

                        // Điều hướng sang EditImageActivity
                        navigateToEditImage(file.getAbsolutePath(), imageModel);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ImageActivity.this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    // Không cần xử lý gì ở đây
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
        Intent intent = new Intent(ImageActivity.this, EditImageActivity.class);
        intent.putExtra("image_path", imagePath);
        intent.putExtra("image_name", imageModel.getName());
        intent.putExtra("image_id", imageModel.getId());
        intent.putExtra("is_new", false);
        startActivityForResult(intent, 1);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadImages(); // reload danh sách ảnh sau khi quay lại từ EditImageActivity
        }
    }

    private void showDeleteDialog(ImageModel imageModel, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa ảnh")
                .setMessage("Bạn có chắc chắn muốn xóa ảnh này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteImage(imageModel, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteImage(ImageModel imageModel, int position) {
        FirebaseFirestore.getInstance()
                .collection("images") // Using a common collection for all users
                .document(imageModel.getId()) // Delete image by document ID
                .delete()
                .addOnSuccessListener(aVoid -> {
                    imageList.remove(position);
                    imageAdapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Đã xóa ảnh", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi xóa ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages(); // Reload after adding/editing
    }
}


