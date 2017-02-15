package com.example.jiangyue.androidap.views.ocr;

/**
 * Created by jiangyue on 17/2/15.
 */
public class OpenCVHelper {
    static {
        System.loadLibrary("OpenCV");
    }

    public static native int[] gray(int[] buf, int w, int h);
}
