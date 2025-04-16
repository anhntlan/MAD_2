package com.example.hipenjava.Activities.Image;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
public class DrawingView extends View{
    private Paint paint;

    public DrawingView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        return true;
    }
}
