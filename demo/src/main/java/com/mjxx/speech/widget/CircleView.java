package com.mjxx.speech.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author e
 * @datetime 2021/2/6 12:47 AM
 */
public class CircleView extends View {

    private Paint mPaint;

    public CircleView(Context context) {
        super(context);
        this.paint();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.paint();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.paint();
    }

    private void paint() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float size = Math.min(getWidth(), getHeight());
        float radius = size / 2;

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#D5E1FC"));
        canvas.drawCircle(cx, cy, radius, mPaint);

        mPaint.setColor(Color.parseColor("#A9C1F8"));
        canvas.drawCircle(cx, cy, radius / 3 * 2, mPaint);

        mPaint.setStrokeWidth(15);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#ffffff"));
        canvas.drawCircle(cx, cy, radius / 4, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(100, widthMeasureSpec);
        int height = measureDimension(100, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defSize, int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY)
            result = specSize;
        else {
            result = defSize;
            if (specMode == MeasureSpec.AT_MOST)
                result = Math.min(defSize, specSize);
        }
        return result;
    }
}
