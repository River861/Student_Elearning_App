package com.example.elearning.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.elearning.CourseActivity;

public class MyButton extends AppCompatImageButton {

    private Context mContext;
    // 画笔: 背景、水波纹、文字
    private Paint bottomPaint, colorPaint, txtPaint;
    // 实现涟漪效果
    private static final int INVALIDATE_DURATION = 10;  //每次刷新的时间间隔
    private static int DIFFUSE_GAP = 50;                //扩散半径增量
    private int maxRadio;                               //扩散的最大半径
    private int shaderRadio;                            //扩散的半径
    private boolean isPushButton;                       //记录是否按钮被按下
    private long downTime = 0;                          //按下的时间

    private int viewWidth, viewHeight;                  //控件宽高
    private int eventX, eventY;                         //触摸位置的X,Y坐标
    private int waterColor = Color.parseColor("#ffffff");
    private int bottomColor = Color.parseColor("#2880e0");
    // 文字
    private String text = "Loading...";
    private String subText = "loading...";
    private int txtSize;        // 文字大小
    private boolean txtBold = true;  // 是否加粗
    private int txtX, txtY;
    private int txtColor = Color.parseColor("#ffffff");
    // 课程id
    private String course_id;

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setTxtSize(19);
        initPaint();
    }

    // 初始化画笔
    private void initPaint() {
        colorPaint = new Paint();
        bottomPaint = new Paint();
        txtPaint = new Paint();
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setColor(txtColor);
        txtPaint.setTextSize(txtSize);
        txtPaint.setFakeBoldText(txtBold);

        colorPaint.setColor(waterColor);
        bottomPaint.setColor(bottomColor);
    }

    public void setCourse_id(String course_id){
        this.course_id = course_id;
    }

    // 设置主标题
    public void setText(String text){
        this.text = text;
    }

    // 设置副标题
    public void setSubText(String subText){
        this.subText = subText;
    }

    // 设置文字颜色
    public void setTxtColor(int color){
        this.txtColor = color;
    }

    // 设置文字大小
    public void setTxtSize(int size){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        size = (int) (size * scale + 0.5f);

        this.txtSize = size;
    }

    // 设置粗细
    public void setTxtBold(boolean bold){
        this.txtBold = bold;
    }

    // 设置底板颜色
    public void setBottomColor(int color){
        this.bottomColor = color;
        bottomPaint.setColor(bottomColor);
    }

    @Override
    public boolean performClick() {
        DIFFUSE_GAP = 50;
        postInvalidate();
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (downTime == 0) downTime = SystemClock.elapsedRealtime();
                eventX = (int) event.getX();
                eventY = (int) event.getY();
                //计算最大半径
                countMaxRadio();
                isPushButton = true;
                postInvalidateDelayed(INVALIDATE_DURATION);
                break;
            case MotionEvent.ACTION_UP:
                performClick();
                break;
            case MotionEvent.ACTION_CANCEL:
                clearData();
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制整个背景
        int pointX = 0, pointY = 0; //控件原点坐标（左上角）
        canvas.drawRect(pointX, pointY, pointX + viewWidth, pointY + viewHeight, bottomPaint);
        if(text.length() < 7) canvas.drawText(text, txtX, txtY, txtPaint);
        else canvas.drawText(text.substring(0, 6) + "...", txtX, txtY, txtPaint);
        canvas.save();
        if(!isPushButton) return; //如果按钮没有被按下则返回

        //绘制扩散圆形背景
        canvas.clipRect(pointX, pointY, pointX + viewWidth, pointY + viewHeight);
        canvas.drawCircle(eventX, eventY, shaderRadio, colorPaint);
        //最后画字
        if(text.length() < 7) canvas.drawText(text, txtX, txtY, txtPaint);
        else canvas.drawText(text.substring(0, 6) + "...", txtX, txtY, txtPaint);
        canvas.restore();
        //直到半径等于最大半径
        if(shaderRadio < maxRadio){
            postInvalidateDelayed(INVALIDATE_DURATION,
                    pointX, pointY, pointX + viewWidth, pointY + viewHeight);
            shaderRadio += DIFFUSE_GAP;
        }else{
            // 跳转到 单课程界面
            Intent intent = new Intent(mContext, CourseActivity.class);
            Bundle bd = new Bundle();
            bd.putString("course_id", course_id);
            bd.putString("title", text);
            bd.putString("subTitle", subText);
            bd.putInt("color", bottomColor);
            intent.putExtras(bd);
            mContext.startActivity(intent);
            clearData();
        }
    }


    // 计算最大半径
    private void countMaxRadio() {
        int a, b;
        if (eventX < viewWidth / 2) a = viewWidth - eventX;
        else a = eventX;
        if (eventY < viewHeight / 2) b = viewHeight - eventY;
        else b = eventY;
        maxRadio = (int)(Math.sqrt(a * a + b * b) + 0.5) + 200;
    }


    // 重置数据的方法
    private void clearData(){
        downTime = 0;
        DIFFUSE_GAP = 50;
        isPushButton = false;
        shaderRadio = 0;
        postInvalidate(); // 刷新
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
        txtX = w / 2;
        txtY = h / 2;
    }

}