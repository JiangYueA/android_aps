package com.example.jiangyue.androidap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.jiangyue.androidap.views.ocr.OpenCVHelper;

/**
 * Created by jiangyue on 17/2/13.
 */
public class IOTessCardActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_io2);

        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(
                R.drawable.card)).getBitmap();
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int[] resultPixes = OpenCVHelper.gray(pix, w, h);
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        result.setPixels(resultPixes, 0, w, 0, 0, w, h);
        ((ImageView) findViewById(R.id.img)).setImageBitmap(result);
    }

}
