package com.annis.customswitch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * @author Lee
 * @date 2019/3/26 12:33
 * @Description
 */
public class CustomSwitch extends View implements View.OnClickListener {
    public CustomSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private boolean isOpen = false;
    private boolean isDarg = false;
    Bitmap background, sliding;
    Paint paint;
    private int slidLeft = 0, slidLeftMax;

    private void initView() {
        paint = new Paint();
        background = BitmapFactory.decodeResource(getResources(), R.drawable.switch_background);
        sliding = BitmapFactory.decodeResource(getResources(), R.drawable.slide_button);
        slidLeftMax = background.getWidth() - sliding.getWidth();
        setOnClickListener(this);
    }
    /**
     * 视图的测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(background.getWidth(), background.getHeight());
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, 0, 0, paint);
        canvas.drawBitmap(sliding, slidLeft, 0, paint);
    }

    float downX, lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = downX = event.getX();
                isDarg = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                float distanceX = currentX - lastX;
                slidLeft += distanceX;
                if (slidLeft < 0) {
                    slidLeft = 0;
                }
                if (slidLeft > slidLeftMax) {
                    slidLeft = slidLeftMax;
                }
                invalidate();
                lastX = currentX;
                if (Math.abs(currentX - downX) > 5) {
                    isDarg = true;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (isDarg) {
                    if (slidLeft < slidLeftMax / 2) {
                        isOpen = false;
                    } else {
                        isOpen = true;
                    }
                    setStatus(isOpen);
                    return true;
                }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (!isDarg)
            changeStatus();
    }

    private void setStatus(boolean open) {
        isOpen = open;
        slidLeft = isOpen ? slidLeftMax : 0;
        invalidate();
    }

    private void changeStatus() {
        setStatus(!isOpen);
    }
}