package com.example.hipenjava.Activities.Image;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class DrawView extends View{
    private Paint paint;
    private Path path;
    private List<Path> paths = new ArrayList<>();
    private List<Paint> paints = new ArrayList<>();
    private Bitmap bitmap;
    private Canvas canvas;
    private boolean isEraser = false;
    private boolean isNewBitmap = true;
    private String pendingImagePath = null;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        path = new Path();
    }
    private void loadBitmapInternal(String imagePath) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap loadedBitmap = BitmapFactory.decodeFile(imagePath);
            if (loadedBitmap != null) {
                bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);

                // 🔥 Vẽ nền trắng trước
                canvas.drawColor(Color.WHITE);

                // 🔥 Scale ảnh để khớp View, tránh mờ
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedBitmap, getWidth(), getHeight(), true);
                canvas.drawBitmap(scaledBitmap, 0, 0, null);

                isNewBitmap = false;
                paths.clear();
                paints.clear();
                invalidate();
            }
        }
    }

    @Override


    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);

            if (pendingImagePath == null && isNewBitmap) {

                canvas.drawColor(Color.WHITE);
            }
        }

        if (pendingImagePath != null) {
            loadBitmapInternal(pendingImagePath);
            pendingImagePath = null;
        }
    }


    @Override


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // ✅ Luôn vẽ nền trắng trước
        canvas.drawColor(Color.WHITE);

        // ✅ Nếu có ảnh thì vẽ ảnh nền
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        // ✅ Vẽ các nét đã vẽ
        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }

        // ✅ Vẽ nét hiện tại đang vẽ
        canvas.drawPath(path, paint);
    }





    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                path.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                // ✅ Chỉ khi nhấc tay mới lưu path vào danh sách
                paths.add(path);
                paints.add(new Paint(paint));  // Lưu style hiện tại
                path = new Path(); // Reset để không vẽ lại path cũ
                invalidate();
                break;
        }
        return true;
    }



    public void setBrushColor(int color){
        paint.setColor(color);
    }
    public void setBrushSize(float size){
        paint.setStrokeWidth(size);
    }

    public void setEraserMode(boolean isEraser){
        paint.setColor(isEraser ? Color.WHITE : Color.BLACK);
    }

    public void undo() {
        if (!paths.isEmpty() && !paints.isEmpty()) {
            paths.remove(paths.size() - 1);
            paints.remove(paints.size() - 1);
            invalidate();
        }
    }



public void clearCanvas() {
    paths.clear();
    paints.clear();
    path.reset();
    invalidate();
}




public Bitmap getBitmap() {
    Bitmap result = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    Canvas tempCanvas = new Canvas(result);

    tempCanvas.drawColor(Color.WHITE); // vẽ nền trắng

    if (bitmap != null) {
        tempCanvas.drawBitmap(bitmap, 0, 0, null); // vẽ ảnh gốc nếu có
    }

    for (int i = 0; i < paths.size(); i++) {
        tempCanvas.drawPath(paths.get(i), paints.get(i)); // vẽ nét vẽ
    }

    return result;
}



    public void loadBitmap(String imagePath) {
        if (getWidth() == 0 || getHeight() == 0) {
            // View chưa sẵn sàng -> lưu lại đường dẫn, chờ onSizeChanged xử lý
            pendingImagePath = imagePath;
            return;
        }

        loadBitmapInternal(imagePath); // Nếu sẵn sàng thì load ngay
    }
}
