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
            if (isNewBitmap) {
                canvas.drawColor(Color.WHITE); // Vẽ nền trắng nếu tạo mới
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
        canvas.drawBitmap(bitmap, 0, 0, null);
        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startNewPath(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                finalizePath();
                break;
        }
        return true;
    }

    private void startNewPath(float x, float y) {
        path = new Path();
        path.moveTo(x, y);
        paths.add(path);

        Paint newPaint = new Paint(paint);
        paints.add(newPaint);
        invalidate();
    }

    private void finalizePath() {
        canvas.drawPath(path, paint);
        invalidate();
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




    public void clearCanvas() {
        paths.clear();
        paints.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    public Bitmap getBitmap() {
        Bitmap result = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(result);

        tempCanvas.drawBitmap(bitmap, 0, 0, null);
        for (int i = 0; i < paths.size(); i++) {
            tempCanvas.drawPath(paths.get(i), paints.get(i));
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
