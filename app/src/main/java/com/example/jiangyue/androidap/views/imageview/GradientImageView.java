package com.example.jiangyue.androidap.views.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.jiangyue.androidap.R;
import com.example.jiangyue.androidap.util.DisplayUtil;

/**
 * Created by jiangyue on 17/1/18.
 * 渐变色块
 */
public class GradientImageView extends View {

    private Paint paint;
    private int gvPos;
    private String[] strs = {"卖出", "利空", "观望", "利好", "买入"};
    private String[] colors = {"#e5dfd5", "#e5d2b9", "#b79364", "#d1b692", "#a9814c"};

    public GradientImageView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public GradientImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public GradientImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradientImageView, defStyleAttr, 0);
        gvPos = a.getInteger(R.styleable.GradientImageView_barPos, 1);
        gvPos = gvPos <= strs.length ? gvPos : strs.length;
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int space = width / 5;
        //设置文字
        for (int j = 0, size = gvPos; j < size; j++) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor(colors[j]));
            canvas.drawRect(space * j, 0, space * (j + 1), getViewHeight(), paint);
            if (j == (gvPos - 1)) {
                setDrawText(strs[j], space * j, space, canvas);
            }
        }
        //设置边框
        for (int k = gvPos, size = 5; k < size; k++) {
            paint.setColor(Color.parseColor(colors[k]));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(DisplayUtil.dp2px(1));
            canvas.drawRect(space * k, DisplayUtil.dp2px(0.75f), space * (k + 1), getViewHeight() - DisplayUtil.dp2px(0.75f), paint);
        }
    }

    private void setDrawText(String str, int start, int space, Canvas canvas) {
        paint.setStrokeWidth(DisplayUtil.dp2px(1));
        paint.setTextSize(DisplayUtil.sp2px(10));
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        canvas.drawText(str, start + space / 2 - bounds.width() / 2, getViewHeight() / 2 + bounds.height() / 2, paint);
    }

    private int getViewHeight() {
        return getMeasuredHeight();
    }
}
