package com.example.jiangyue.androidap.chart.paint;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

/**
 * Created by jiangyue on 16/5/30.
 */
public class LongitudeLatitudePaint extends Paint {

    public LongitudeLatitudePaint() {
        setStyle(Style.STROKE);
        setColor(Color.parseColor("#20000000"));
        setStrokeWidth(1);
        setAntiAlias(true);
        setPathEffect(new DashPathEffect(new float[]{12, 6, 12, 6}, 1));
    }

    public void setPathEffect(){
        setPathEffect(new DashPathEffect(new float[]{12, 6, 12, 6}, 1));
    }
}
