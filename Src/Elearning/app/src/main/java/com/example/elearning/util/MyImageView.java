package com.example.elearning.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/* 圆形ImageView */
public class MyImageView extends AppCompatImageView {

    private Paint paint;
    private Paint edgePaint;
    private Matrix matrix;
    private float height;
    private float width;
    private float adius;
    private int border_width = 5;

    public MyImageView(Context context) {
        this(context,null);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(); // 初始化画笔
        paint.setAntiAlias(true); // 扛锯齿
        edgePaint = new Paint();
        edgePaint.setAntiAlias(true);
        edgePaint.setColor(Color.parseColor("#E5E5E5"));
        edgePaint.setStyle(Paint.Style.STROKE);
        edgePaint.setStrokeWidth(border_width);
        matrix = new Matrix(); // 矩形
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        adius = (Math.min(width, height)-2*border_width) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onDraw(canvas);
            return;
        }
        if (drawable instanceof BitmapDrawable) {
            paint.setShader(initBitmapShader((BitmapDrawable) drawable));
            canvas.drawCircle(width / 2, height / 2, adius, paint);
            canvas.drawCircle(width / 2, height / 2, adius, edgePaint);
            return;
        }
        super.onDraw(canvas);
    }

    private Shader initBitmapShader(BitmapDrawable drawable) {
        Bitmap bitmap = drawable.getBitmap();
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = Math.min(width / bitmap.getWidth(), height / bitmap.getHeight());
        if (bitmap.getWidth() > adius * 2) {
            matrix.setScale(scale, scale, -(bitmap.getWidth() - bitmap.getWidth() * scale) / 2, 0);
        }
        else {
            matrix.setScale(scale, scale, - (adius*2 - bitmap.getWidth() * scale) / 2, 0); // 缩放处理
        }
        bitmapShader.setLocalMatrix(matrix);
        return bitmapShader;
    }
}

