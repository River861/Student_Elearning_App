package com.example.elearning.entity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekView;

public class CalendarWeekView extends WeekView {

    private Paint mTextPaint = new Paint();
    private Paint mSchemeBasicPaint = new Paint();
    private static final int futureColor = Color.parseColor("#61b6ff");
    private static final int finishColor = Color.parseColor("#41e669");
    private static final int unfinishColor = Color.parseColor("#ff6767");

    private float mRadio;
    private int mPadding;

    public CalendarWeekView(Context context) {
        super(context);
        initPaint(context);
        mRadio = dipToPx(getContext(), 16);
        mPadding = dipToPx(getContext(), 4);
    }

    protected void initPaint(Context context){
        // 文字
        mTextPaint.setTextSize(dipToPx(context, 8));
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);
        // 标记
        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
    }

    // 绘制选中的日子
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setColor(0x80cfcfcf);
        canvas.drawCircle(x + (float)mItemWidth / 2,  (float)mItemHeight / 2, mRadio + mPadding, mSelectedPaint);
        return true;
    }

    // 绘制标记的事件日子
    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {
        // 标记事件用不同颜色 ⚪
        switch (calendar.getScheme()){
            case "future":
                mSchemeBasicPaint.setColor(futureColor);
                break;
            case "all_finish":
                mSchemeBasicPaint.setColor(finishColor);
                break;
            case "unfinish":
                mSchemeBasicPaint.setColor(unfinishColor);
        }
        canvas.drawCircle(x + (float)mItemWidth / 2, (float)mItemHeight / 2, mRadio, mSchemeBasicPaint);
    }

    //绘制文本
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        if (hasScheme) { // 否则绘制具有标记的
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine,
                    calendar.isCurrentDay() ? mCurDayTextPaint : mSchemeTextPaint);
        } else { // 绘制普通文本
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine,
                    calendar.isCurrentDay() ? mCurDayTextPaint : mCurMonthTextPaint);
        }
    }

    // dp转px
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
