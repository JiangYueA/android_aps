package com.example.jiangyue.androidap.views.ocr;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * Created by jiangyue on 17/2/13.
 */
public class OCR {
    private TessBaseAPI mTess;
    private boolean flag;
    private Context context;
    private AssetManager assetManager;

    public OCR() {
        // TODO Auto-generated constructor stub

        mTess = new TessBaseAPI();
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
        String language = "eng";
        //请将你的语言包放到这里 sd 的 tessseract 下的tessdata 下
        File dir = new File(datapath + "tessdata/");
        if (!dir.exists())
            dir.mkdirs();
        flag = mTess.init(datapath, language);
    }

    /**
     * 识别出来bitmap 上的文字
     *
     * @param bitmap 需要识别的图片
     * @return
     */
    public String getOCRResult(Bitmap bitmap) {
        String result = "dismiss langues";
        if (flag) {
            mTess.setImage(bitmap);
            result = mTess.getUTF8Text();
        }

        return result;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }
}
