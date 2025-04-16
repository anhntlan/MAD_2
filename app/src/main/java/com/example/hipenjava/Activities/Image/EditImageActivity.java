package com.example.hipenjava.Activities.Image;

import static com.example.hipenjava.R.layout.activity_edit_image;

import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.HomeActivity;
import com.example.hipenjava.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Objects;


public class EditImageActivity extends AppCompatActivity {
    private DrawView drawView;
    private boolean isNew;
    private String imagePath, imageName, imageId;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String userId;
    private ImageView btnBack;
    private Cloudinary cloudinary;
    BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.navigation_draw);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_courses) {
                Intent intent = new Intent(EditImageActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_home) {
                Intent intent = new Intent(EditImageActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_draw) {
                Intent intent = new Intent(EditImageActivity.this, ImageActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_challenge) {
                Intent intent = new Intent(EditImageActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_community) {
                Intent intent = new Intent(EditImageActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        drawView = findViewById(R.id.drawView);

        // Cloudinary cấu hình
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dhhcgee05",
                "api_key", "561863598748432",
                "api_secret", "laGhUW6QCM6nCv4_7sByGdMyYh0"
        ));

        Intent intent = getIntent();
        isNew = intent.getBooleanExtra("is_new", true);
        imagePath = intent.getStringExtra("image_path");

        if (!isNew && imagePath != null) {
//            drawView.loadBitmap(imagePath);
            imageName = intent.getStringExtra("image_name");
            imageId = intent.getStringExtra("image_id");
            drawView.post(() -> drawView.loadBitmap(imagePath));
        }

        findViewById(R.id.btnSave).setOnClickListener(view -> {
            if (isNew) {
                showSaveDialog();
            } else {
                saveDrawing(imageName);
            }
        });

        findViewById(R.id.buttonRed).setOnClickListener(v -> drawView.setBrushColor(Color.RED));
        findViewById(R.id.buttonBlack).setOnClickListener(v -> drawView.setBrushColor(Color.BLACK));
        findViewById(R.id.buttonBlue).setOnClickListener(v -> drawView.setBrushColor(Color.BLUE));
        findViewById(R.id.buttonGreen).setOnClickListener(v -> drawView.setBrushColor(Color.GREEN));
        findViewById(R.id.buttonPurple).setOnClickListener(v -> drawView.setBrushColor(Color.parseColor("#9C27B0"))); // Màu tím
        findViewById(R.id.buttonYellow).setOnClickListener(v -> drawView.setBrushColor(Color.parseColor("#FFEB3B"))); // Màu vàng
        findViewById(R.id.buttonUndo).setOnClickListener(v -> drawView.undo());

        findViewById(R.id.buttonEraser).setOnClickListener(v -> drawView.setEraserMode(true));
        findViewById(R.id.buttonReset).setOnClickListener(v -> drawView.clearCanvas());

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            setResult(RESULT_OK); // Báo về rằng có thể có thay đổi
            finish();
        });


        SeekBar seekBar = findViewById(R.id.seekBarBrushSize);
        seekBar.setProgress(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawView.setBrushSize(progress);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_image, null);
        EditText editText = dialogView.findViewById(R.id.editTextName);

        builder.setView(dialogView)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String input = editText.getText().toString().trim();
                final String name = input.isEmpty() ? "Drawing_" + System.currentTimeMillis() : input;

                checkImageNameExists(name, exists -> {
                    if (exists) {
                        showDuplicateNameAlert(name);
                    } else {
                        dialog.dismiss();
                        saveDrawing(name);
                    }
                });
            });
        });

        dialog.show();
    }

    private void showDuplicateNameAlert(String name) {
        new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage("Tên ảnh '" + name + "' đã tồn tại. Vui lòng chọn tên khác!")
                .setPositiveButton("OK", null)
                .show();
    }


    private Bitmap resizeBitmap(Bitmap original, int targetWidth) {
        float aspectRatio = (float) original.getHeight() / original.getWidth();
        int targetHeight = Math.round(targetWidth * aspectRatio);
        return Bitmap.createScaledBitmap(original, targetWidth, targetHeight, true);
    }

    private void saveDrawing(String name) {
//    Bitmap bitmap = drawView.getBitmap(); // Lấy bitmap từ DrawView
        Bitmap originalBitmap = drawView.getBitmap();
        Bitmap bitmap = resizeBitmap(originalBitmap, 800);

        File file = new File(getCacheDir(), name + ".png"); // Tạo file tạm trong cache

    try (FileOutputStream out = new FileOutputStream(file)) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Lưu bitmap ra file

        new Thread(() -> {
            try {
                // Tạo public_id duy nhất bằng cách thêm timestamp (tránh ghi đè)
                String uniquePublicId = name + "_" + System.currentTimeMillis();

                // Upload lên Cloudinary
                Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.asMap(
                        "public_id", uniquePublicId,
                        "overwrite", false  // Không ghi đè ảnh cũ
                ));

                String imageUrl = (String) uploadResult.get("secure_url");
                Timestamp date = Timestamp.now();
                String uid = auth.getCurrentUser().getUid();
                CollectionReference imageRef = firestore.collection("images");

                if (isNew) {
                    // Tạo document mới nếu là ảnh mới
                    String newId = imageRef.document().getId();
                    ImageModel image = new ImageModel(newId, name, imageUrl, date, uid);

                    imageRef.document(newId).set(image)
                            .addOnSuccessListener(unused -> runOnUiThread(() -> {
                                Toast.makeText(this, "Đã lưu ảnh!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }))
                            .addOnFailureListener(e -> runOnUiThread(() ->
                                    Toast.makeText(this, "Lỗi lưu Firestore!", Toast.LENGTH_SHORT).show()));
                } else {
                    // Cập nhật ảnh hiện có (vẫn giữ name cũ, chỉ đổi URL mới)
                    ImageModel updatedImage = new ImageModel(imageId, name, imageUrl, date, uid);

                    imageRef.document(imageId).set(updatedImage)
                            .addOnSuccessListener(unused -> runOnUiThread(() -> {
                                Toast.makeText(this, "Đã cập nhật ảnh!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }))
                            .addOnFailureListener(e -> runOnUiThread(() ->
                                    Toast.makeText(this, "Lỗi cập nhật Firestore!", Toast.LENGTH_SHORT).show()));
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi upload lên Cloudinary!", Toast.LENGTH_SHORT).show());
            }
        }).start();

    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Lỗi lưu ảnh!", Toast.LENGTH_SHORT).show();
    }
}


    private void checkImageNameExists(String name, OnCheckCallback callback) {
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        firestore.collection("images")
                .whereEqualTo("name", name)
                .whereEqualTo("userId", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResult(!task.getResult().isEmpty());
                    } else {
                        callback.onResult(false);
                    }
                });
    }

}

interface OnCheckCallback {
    void onResult(boolean exists);
}

