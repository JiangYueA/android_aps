package com.example.jiangyue.androidap.util.imageload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.widget.EditText;

import org.apache.http.client.methods.HttpGet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 
 * 功能类 <功能详细描述>
 * 
 * @author 姓名 工号
 * @version [版本号, 2014年7月28日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class Utility {
    private final static String TAG = Utility.class.getSimpleName();

    /* 应用变量 */
    public static boolean isHomeResume = false;

    private static int mScreenWidth = 0; // 屏幕宽
    private static int mScreenHight = 0; // 屏幕高
    private static float mDensity = -1; // 屏幕密度

    public static String DEVICE_ID = null; // 设备ID
    private static int mIsOPhoneChecked = 0;
    public static Random random = new Random();
    /* end */

    // 表示从哪里进入聊天界面
    public static int mChattingFrom;

    public static String createKeyId() {

        StringBuffer strbuffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            strbuffer.append(rndChar());// 从随机数直接转换成字母
        }

        return strbuffer.toString();
    }

    private static char rndChar() {
        int rnd = (int) (Math.random() * 52); // or use Random or whatever
        char base = (rnd < 26) ? 'A' : 'a';

        return (char) (base + rnd % 26);

    }

    public static void freeReqFileBean(ReqFileBean bean) {
        if (bean != null) {
            bean.setListener(null);
            bean = null;
        }
    }

    public static void freeReqFileBean(ReqImgBean bean) {
        if (bean != null) {
            bean.setContext(null);
            bean.setListener(null);
            bean = null;
        }
    }

    public static void freeReqBean(ReqBean bean) {
        if (bean != null) {
            bean.setContext(null);
            bean.setLister(null);
            bean = null;
        }
    }

    // 过滤特殊字符
    public static String StringFilter(String str) throws PatternSyntaxException {
        // 清除掉所有特殊字符
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            Pattern p;
            Matcher m;
            p = Pattern.compile("[\u4e00-\u9fa5]");// 取中文
            m = p.matcher(str);
            if (m.find()) {
                sb.append(c);
            }
            p = Pattern.compile("[a-zA-Z]");// 取英文字符
            m = p.matcher(str);
            if (m.find()) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static int getSDKVserion() {
        try {
            return Build.VERSION.SDK_INT;
        } catch (Exception e) {
            return 0;

        }
    }


    public static boolean isNotNull(String str) {
        if (str != null && !TextUtils.isEmpty(str) && !"".equals(str.trim()) && !"null".equals(str)) {
            return true;
        }
        return false;
    }

    public static boolean isOPhone() {
        switch (mIsOPhoneChecked) {
        case 0:
            mIsOPhoneChecked = 2;
            try {
                Method method1 = NetworkInfo.class.getMethod("getApType");
                Method method2 = NetworkInfo.class.getMethod("getInterfaceName");
                // Added by Li Yong on 2011-10-10
                Method method3 = Socket.class.getMethod("setInterface", String.class);
                Method method4 = WebSettings.class.getMethod("setProxy", Context.class, String.class, int.class);
                // if (method1 != null && method2 != null)
                if (method1 != null && method2 != null && method3 != null && method4 != null) {
                    mIsOPhoneChecked = 1;
                    return true;
                }
            } catch (Exception e) {
            }
            return false;
        case 1:
            return true;
        case 2:
            return false;
        }
        return false;
    }

    public static final String getDeviceImei(Context context) {
        TelephonyManager tmManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tmManager.getSubscriberId();

    }

    public static String getDeviceId() {
        return DEVICE_ID;
    }

    public static Bitmap getBitmapFromnFile(InputStream is) {
        if (is == null)
            return null;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Modified by Li Yong
            // Compare image quality under RGB_565 with ARGB_8888
            // options.inPreferredConfig = Config.RGB_565;
            options.inPreferredConfig = Config.ARGB_8888;
            // End
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            bitmap = null;
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromnFileInputStream(FileInputStream is) {
        if (is == null)
            return null;
        Bitmap bitmap = null;
        try {
            Log.i(TAG, "------->");
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Modified by Li Yong
            // Compare image quality under RGB_565 with ARGB_8888
            // options.inPreferredConfig = Config.RGB_565;
            options.inPreferredConfig = Config.ARGB_8888;
            // End
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, options);
        } catch (Exception e) {
            bitmap = null;
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 使用decodeStream获取file <功能详细描述>
     *
     * @param file
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Bitmap getBitmapFromnFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return getBitmapFromnFileInputStream(fis);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Bitmap getBitmapFromnFile(String filePath) {
        return getBitmapFromnFile(new File(filePath));
    }

    public static Bitmap getBitmapFromnFile(Context ctx, int resId) {
        InputStream in = ctx.getResources().openRawResource(resId);
        return getBitmapFromnFile(in);
    }

    public static BitmapDrawable getBitmapDrawableFromnFile(Context ctx, int resId) {
        return new BitmapDrawable(ctx.getResources(), getBitmapFromnFile(ctx, resId));
    }

    public static String getFileEndWith(String url) {
        String fileEnd = "";
        int lsIndex = url.lastIndexOf(".");
        if (lsIndex >= 0 && lsIndex < url.length()) {
            fileEnd = url.substring(lsIndex);
        }
        return fileEnd;
    }

    /**
     *
     * 圆角 <功能详细描述>
     *
     * @param bitmap
     * @param pixels
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = null;
        try {

            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (Exception e) {

            output = null;
        }

        return output;
    }

    /**
     *
     * 图片增加阴影 <功能详细描述>
     *
     * @param map
     * @param radius
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Bitmap drawShadow(Bitmap map, int radius) {
        Bitmap shadowImage = null;
        try {
            if (map == null)
                return null;
            BlurMaskFilter blurFilter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER);
            Paint shadowPaint = new Paint();
            shadowPaint.setMaskFilter(blurFilter);

            int[] offsetXY = new int[2];
            shadowImage = map.extractAlpha(shadowPaint, offsetXY);
            shadowImage = shadowImage.copy(Config.ARGB_4444, true);
            Canvas c = new Canvas(shadowImage);
            c.drawBitmap(map, -offsetXY[0], -offsetXY[1], null);

        } catch (Exception e) {
            // TODO: handle exception
            shadowImage = null;
        }
        return shadowImage;
    }

    public static void freeHttpGet(HttpGet get) {
        if (get != null) {
            get.abort();
        }
        get = null;
    }

    public static boolean isNum(String str) {
        return str.matches("[\\d.]+");
    }

    /**
     *
     * 获取横竖屏中最小值 <功能详细描述>
     *
     * @param context
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int getWidthMin(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return (dm.widthPixels <= dm.heightPixels) ? dm.widthPixels : dm.heightPixels;
    }

    /**
     *
     * 获取屏幕宽度 <功能详细描述>
     *
     * @param context
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int getScreenWidth(Context context) {

        if (mScreenWidth <= 0) {
            WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;
        }
        return mScreenWidth;
    }

    /**
     *
     * 获取屏幕高度 <功能详细描述>
     *
     * @param context
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int getScreenHeight(Context context) {
        if (mScreenHight <= 0) {
            WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            mScreenHight = dm.heightPixels;
        }
        return mScreenHight;
    }

    /**
     *
     * px 转化为 sp <功能详细描述>
     *
     * @param context
     * @param pxValue
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int px2sp(Context context, float pxValue) {

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

        return (int) (pxValue / fontScale + 0.5f);

    }

    /**
     *
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) <功能详细描述>
     *
     * @param context
     * @param dpValue
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp <功能详细描述>
     *
     * @param context
     * @param pxValue
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale);
    }

    /**
     *
     * 复制文件by wangjiachao
     * <功能详细描述>
     *
     * @param sourceFile
     * @param targetFile
     * @throws java.io.IOException
     * @see [类、类#方法、类#成员]
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        // 关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    /**
     *
     * 强制显示或者关闭系统键盘 <功能详细描述>
     *
     * @param context
     * @param txtSearchKey
     * @param isOpen
     * @see [类、类#方法、类#成员]
     */
    public static void KeyBoard(Context context, final EditText txtSearchKey, boolean isOpen) {
        InputMethodManager m = (InputMethodManager) txtSearchKey.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (isOpen) {
            m.showSoftInput(txtSearchKey, InputMethodManager.SHOW_FORCED);
        } else {
            m.hideSoftInputFromWindow(txtSearchKey.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     *
     * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到 <功能详细描述>
     *
     * @param context
     * @see [类、类#方法、类#成员]
     */
    public static void setVibrator(Context context) {

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // 停止 开启 停止 开启
        long[] pattern = { 100, 400, 100, 400 };

        // 重复两次上面的
        vibrator.vibrate(pattern, -1);
    }

    /**
     *
     * 获得UniqId <功能详细描述>
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getChatIdValue() {
        StringBuffer sb = new StringBuffer();
        long t = System.currentTimeMillis();
        sb.append(t);

        return sb.toString();
    }

    public static int getChatMessageId() {
        return random.nextInt(65535);
    }

    /**
     *
     * 屏幕密度 <功能详细描述>
     *
     * @param context
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static float getDensity(Context context) {

        if (mDensity > 0) {
            return mDensity;
        }

        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        mDensity = dm.density;

        return mDensity;
    }

    /**
     *
     * <一句话功能简述> <功能详细描述>
     *
     * @param context
     * @param permission
     * @return
     * @see [类、类#方法、类#成员]
     */

    private static String getAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return null;
        }
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs == null) {
            return null;
        }
        for (PackageInfo pack : packs) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (permission.equals(provider.readPermission) || permission.equals(provider.writePermission)) {
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * 获取应用的名称 <功能详细描述>
     *
     * @param context
     * @return
     * @throws android.content.pm.PackageManager.NameNotFoundException
     * @see [类、类#方法、类#成员]
     */
    private static String obtatinAppName(Context context) throws NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)).toString();
    }

    public static void writeLog(Context context, String tag, String content) {

        String filepath = FileUtil.getFileDir(context, "imclientlog");

        File sampleDir = new File(filepath);

        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        String logfile = sampleDir.getAbsolutePath() + File.separator + "alarmReceiver.log";

        Log.v("write", "writeLog logfile = " + logfile);


        try {
            OutputStream mOut = new FileOutputStream(logfile, true);
            String str = "\n" + getDateTimeStamp() + tag + " : " + content;
            byte[] strBuffer = str.getBytes();
            mOut.write(strBuffer);
            mOut.close();
            mOut.flush();

        } catch (IOException e) {

            e.printStackTrace();
        }finally
        {
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateTimeStamp() { // 返回时间戳
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sim.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTimeStamp() { // 返回时间戳

        SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sim.format(new Date());
    }

    public static int getPackageVerCode(Context context) {
        PackageInfo info = null;
        ;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info.versionCode;
    }

    public static String getPackageVerName(Context context) {
        PackageInfo info = null;
        ;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info.versionName;
    }

    // weibo start
    private static int displayWidth = 0;
    private static int displayHeight = 0;
    private static int statusBarHeight = 0;

    // 获取屏幕宽度
    public static int getDisplayWidth(Context context) {
        if (displayWidth <= 0) {
            WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            displayWidth = dm.widthPixels;
        }
        return displayWidth;
    }

    // 获取屏幕高度
    public static int getDisplayHeight(Context context) {
        if (displayHeight <= 0) {
            WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            displayHeight = dm.heightPixels;
        }
        return displayHeight;
    }

    public static int getStatusBarHeight(Context context) {

        if (statusBarHeight == 0) {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
            }
        }
        return statusBarHeight;
    }

    public static int getTodayMonth() {
        Calendar.getInstance().setTime(Calendar.getInstance().getTime());
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 判断sd卡是否可用
     * <一句话功能简述>
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isSDCardAvailable() {
        boolean isAvailable = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * 判断sd卡是否有可用空间
     */

    public static boolean isStorageAvailable() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        long freeStorage = (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
        if (freeStorage > 1) {
            return true;
        }
        return false;

    }

    /**
     * 
     * <一句话功能简述>
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean getStorageResult(Context context) {
        if (isSDCardAvailable()) {
            if (isStorageAvailable()) {
                // Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                return true;

            }
            // Toast.makeText(context, "存储卡空间不足", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // Toast.makeText(context, "请插入存储卡", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static Intent openFile(String filePath) {

        File file = new File(filePath);
        if (!file.exists())
            return null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length())
                .toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
                || end.equals("wav")) {
            return getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getVideoFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg")
                || end.equals("bmp")) {
            return getImageFileIntent(filePath);
        } else if (end.equals("apk")) {
            return getApkFileIntent(filePath);
        } else if (end.equals("ppt")) {
            return getPptFileIntent(filePath);
        } else if (end.equals("xls")) {
            return getExcelFileIntent(filePath);
        } else if (end.equals("doc")) {
            return getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else if (end.equals("chm")) {
            return getChmFileIntent(filePath);
        } else if (end.equals("txt")) {
            return getTextFileIntent(filePath, false);
        } else {
            return getAllIntent(filePath);
        }
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content")
                .encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

}
