//package com.example.hipenjava.Activities.Image;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.Toast;
//import androidx.annotation.Nullable;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.CustomTarget;
//import com.bumptech.glide.request.transition.Transition;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.*;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.example.hipenjava.R;
//import com.google.firebase.firestore.Query;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//public class ImageActivity extends AppCompatActivity{
//    private RecyclerView recyclerView;
//    private ImageAdapter imageAdapter;
//    private List<ImageModel> imageList;
//    private ImageView btnAddNew, btnBack;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.image_list);
//
//        recyclerView = findViewById(R.id.recyclerView);
//        btnAddNew = findViewById(R.id.btnAddNew);
//        btnBack = findViewById(R.id.btnBack);
//
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        imageList = new ArrayList<>();
//        imageAdapter = new ImageAdapter(this, imageList, listener);
//        recyclerView.setAdapter(imageAdapter);
//
//        // Thêm ảnh mới
//        btnAddNew.setOnClickListener(v -> {
//            Intent intent = new Intent(ImageActivity.this, EditImageActivity.class);
//            intent.putExtra("is_new", true);
//            startActivity(intent);
//        });
//
//        // Quay lại màn hình trước
//        btnBack.setOnClickListener(v -> finish());
//
//        loadImages();
//    }
//
//    private void loadImages() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null) {
//            Toast.makeText(ImageActivity.this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String currentUserId = user.getUid();
//
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        CollectionReference imageRef = firestore.collection("users").document(currentUserId).collection("images");
//
//        imageRef
//                .orderBy("date", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    imageList.clear();
//                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                        ImageModel image = doc.toObject(ImageModel.class);
//                        image.setId(doc.getId()); // lấy id document Firestore làm id
//                        imageList.add(image);
//                    }
//                    imageAdapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(ImageActivity.this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                );
//    }
//
//    private final ImageAdapter.OnItemClickListener listener = new ImageAdapter.OnItemClickListener() {
//        @Override
//        public void onItemClick(ImageModel imageModel) {
//            // Dùng Glide để tải ảnh thành Bitmap
//            Glide.with(ImageActivity.this)
//                    .asBitmap()
//                    .load(imageModel.getImageUrl())
//                    .into(new CustomTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            try {
//                                // Lưu Bitmap vào file tạm
//                                File file = new File(getCacheDir(), imageModel.getName() + "_edit.png");
//                                try (FileOutputStream fos = new FileOutputStream(file)) {
//                                    resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                                }
//
//                                // Chuyển sang EditImageActivity và gửi path ảnh
//                                Intent intent = new Intent(ImageActivity.this, EditImageActivity.class);
//                                intent.putExtra("image_path", file.getAbsolutePath());
//                                intent.putExtra("image_name", imageModel.getName());
//                                intent.putExtra("image_id", imageModel.getId());
//                                intent.putExtra("is_new", false);
//                                startActivity(intent);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Toast.makeText(ImageActivity.this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {}
//                    });
//        }
//
//        @Override
//        public void onItemLongClick(ImageModel imageModel, int position) {
//            showDeleteDialog(imageModel, position);
//        }
//    };
//
//    private void showDeleteDialog(ImageModel imageModel, int position) {
//        new AlertDialog.Builder(this)
//                .setTitle("Xóa ảnh")
//                .setMessage("Bạn có chắc chắn muốn xóa ảnh này?")
//                .setPositiveButton("Xóa", (dialog, which) -> {
//                    // Xóa ảnh trong Firestore
//                    FirebaseFirestore.getInstance()
//                            .collection("users")
//                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                            .collection("images")
//                            .document(imageModel.getId())
//                            .delete()
//                            .addOnSuccessListener(aVoid -> {
//                                imageList.remove(position);
//                                imageAdapter.notifyItemRemoved(position);
//                                Toast.makeText(ImageActivity.this, "Đã xóa ảnh", Toast.LENGTH_SHORT).show();
//                            })
//                            .addOnFailureListener(e ->
//                                    Toast.makeText(ImageActivity.this, "Lỗi xóa ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//                })
//                .setNegativeButton("Hủy", null)
//                .show();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadImages(); // reload lại sau khi thêm/sửa
//    }
//}

package com.example.hipenjava.Activities.Image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_list);

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

        String currentUserId = user.getUid(); // ID người dùng hiện tại từ Firebase Auth
        // Get reference to the collection 'images'
        CollectionReference imageRef = FirebaseFirestore.getInstance()
                .collection("images");

        // Apply whereEqualTo() to filter by userId (sử dụng đúng tên trường là 'userId')
        imageRef
                .whereEqualTo("userId", currentUserId) // Lọc theo 'userId', không phải 'user_id'
                .orderBy("date", Query.Direction.DESCENDING) // Sắp xếp theo 'date' (timestamp)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    imageList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Không có ảnh để hiển thị", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            // Chuyển đổi dữ liệu Firestore thành đối tượng ImageModel
                            ImageModel image = doc.toObject(ImageModel.class);
                            image.setId(doc.getId()); // Lấy ID của tài liệu Firestore
                            imageList.add(image);
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ImageActivity.this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(ImageActivity.this, EditImageActivity.class);
        intent.putExtra("image_path", imagePath);
        intent.putExtra("image_name", imageModel.getName());
        intent.putExtra("image_id", imageModel.getId());
        intent.putExtra("is_new", false);
        startActivityForResult(intent, 1);
        btnBack.setOnClickListener(v -> finish());
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


