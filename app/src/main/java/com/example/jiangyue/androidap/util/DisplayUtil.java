package com.example.jiangyue.androidap.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by jiangyue on 16/5/31.
 */
public class DisplayUtil {

    public static int SCREEN_WIDTH_PIXELS;
    public static int SCREEN_HEIGHT_PIXELS;
    public static float SCREEN_DENSITY;
    public static float SCREEN_SCALED_DENSITY;
    public static int SCREEN_WIDTH_DP;
    public static int SCREEN_HEIGHT_DP;
    private static boolean sInitialed;

    public DisplayUtil() {
    }

    public static void init(Context context) {
        if (!sInitialed && context != null) {
            sInitialed = true;
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            SCREEN_WIDTH_PIXELS = dm.widthPixels;
            SCREEN_HEIGHT_PIXELS = dm.heightPixels;
            SCREEN_DENSITY = dm.density;
            SCREEN_SCALED_DENSITY = dm.scaledDensity;
            SCREEN_WIDTH_DP = (int) ((float) SCREEN_WIDTH_PIXELS / dm.density);
            SCREEN_HEIGHT_DP = (int) ((float) SCREEN_HEIGHT_PIXELS / dm.density);
        }
    }

    public static int dp2px(float dp) {
        float scale = SCREEN_DENSITY;
        return (int) (dp * scale + 0.5F);
    }

    public static int sp2px(float spValue) {
        final float fontScale = SCREEN_SCALED_DENSITY;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int designedDP2px(float designedDp) {
        if (SCREEN_WIDTH_DP != 320) {
            designedDp = designedDp * (float) SCREEN_WIDTH_DP / 320.0F;
        }

        return dp2px(designedDp);
    }

    public static void setPadding(View view, float left, float top, float right, float bottom) {
        view.setPadding(designedDP2px(left), dp2px(top), designedDP2px(right), dp2px(bottom));
    }

    // 将px值转换为dip或dp值，保证尺寸大小不变
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 将px值转换为sp值，保证文字大小不变
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取屏宽
     */
    public static int getWidthMin() {
        return (SCREEN_WIDTH_PIXELS <= SCREEN_HEIGHT_PIXELS) ? SCREEN_WIDTH_PIXELS : SCREEN_HEIGHT_PIXELS;
    }

    /**
     * 获取屏高
     */
    public static int getHeightMin() {
        return (SCREEN_WIDTH_PIXELS >= SCREEN_HEIGHT_PIXELS) ? SCREEN_WIDTH_PIXELS : SCREEN_HEIGHT_PIXELS;
    }
}
