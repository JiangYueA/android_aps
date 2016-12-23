package com.example.jiangyue.androidap.util.imageload;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.jiangyue.androidap.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncImageLoader {
    private static final String TAG = SyncImageLoader.class.getSimpleName();
    Context mContext = null;

    public final static int BITMAP_HEAD_WIDTH = 70;
    
    public final static int BITMAP_MIN_HIGHT = 90;
    public final static int BITMAP_MIN_WIDTH = 90;

    public final static int BITMAP_MID_HIGHT = 150;
    public final static int BITMAP_MID_WIDTH = 150;

    public final static int BITMAP_BIG_HIGHT = 480;
    public final static int BITMAP_BIG_WIDTH = 320;
    

    private static LruCache<String, Bitmap> imageCache = null;

    private static SyncImageLoader mSyncImageLoader = null;

    private ExecutorService mThreadPool = Executors.newFixedThreadPool(1);

//    private static HashMap<String, ArrayList<ReqImgBean>> mReqBeanListMap = null;

    /*
     * private int mStartLoadLimit = 0;
     *
     * private int mStopLoadLimit = 0;
     *
     * private Object lock = new Object();
     * private boolean mAllowLoad = true;
     *
     * public void lock(){
     * mAllowLoad = false;
     * }
     *
     * public void unlock(){
     * mAllowLoad = true;
     * synchronized (lock) {
     * lock.notifyAll();
     * }
     * }
     *
     * public void setLoadLimit(int startLoadLimit,int stopLoadLimit){
     * if(startLoadLimit > stopLoadLimit){
     * return;
     * }
     * mStartLoadLimit = startLoadLimit;
     * mStopLoadLimit = stopLoadLimit;
     * }
     */

    public synchronized static SyncImageLoader getInstance() {
        if (mSyncImageLoader == null) {
            mSyncImageLoader = new SyncImageLoader();
        }
        return mSyncImageLoader;
    }

    public SyncImageLoader() {

        mContext = MyApplication.getAppContext();
        
        if (imageCache == null) {
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxMemory / 8;
            int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
            LogUtils.v(TAG, "init LruCache maxMemory = " + maxMemory);
            LogUtils.v(TAG, "init LruCache cacheSize = " + cacheSize);
            LogUtils.v(TAG, "init LruCache totalMemory = " + totalMemory);
            
            
            imageCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    int biSize = value.getRowBytes();
                    int hei = value.getHeight();
                    int bitSize = biSize * hei;
                    int rSize = bitSize / 1024;
                    LogUtils.v(TAG, "LruCache sizeOf rSize = " + rSize);
                    int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
                    LogUtils.v(TAG, "LruCache sizeOf totalMemory = " + totalMemory);
                    return rSize;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                   
                    Bitmap bitmap= imageCache.remove(key);
                    new PhantomReference<Bitmap>(oldValue, new ReferenceQueue<Bitmap>());
                   /* if (null != bitmap)
                    {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    */
                    System.gc();
                    
                    int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);

                    LogUtils.v(TAG, "LruCache entryRemoved imageCache totalMemory = " + totalMemory);
                    LogUtils.v(TAG, "LruCache entryRemoved key" + key + ", oldValue = " + oldValue);

                }
            };
        }

    }

    /**
     * 
     * 刷新缩略图 <功能详细描述>
     * 
     * @param iconIV
     * @param imageUrl
     * @param imgType
     * @see [类、类#方法、类#成员]
     */

    public void setImageView(final ImageView iconIV, String imageUrl, int imgType,
            final OnImgLoadListener listener) {
        setImageBitmap(iconIV, imageUrl, imgType, listener);
    }

    private void setImageBitmap(final ImageView iconIV, final String imageUrl, final int imgType,
             final OnImgLoadListener listener) {
       
        Bitmap bitmap = SyncImageLoader.getInstance().getCache(imageUrl, imgType);

        if (bitmap != null) {

            LogUtils.d(TAG, "scrollState---getCache successs " + ", loadImageFromUrl has Cache");
            iconIV.setVisibility(View.VISIBLE);
            iconIV.setImageBitmap(bitmap);

            return;
        }

        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    ReqImgBean bean = new ReqImgBean();
                    bean.setListener(listener);
                    bean.setImgUrl(imageUrl);
                    bean.setContext(mContext);
                    bean.setIcon(iconIV);
                    bean.setImgType(imgType);
                    loadImageFromUrl(bean);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    
    public void setImageBitmapFromAssets(final ImageView iconIV, final String filePath, final int imgType,
            final OnImgLoadListener listener) {

        LogUtils.d(TAG, "SyncImageLoader setImageBitmapFromAssets  filePath = " + filePath);

       Bitmap bitmap = SyncImageLoader.getInstance().getCache(filePath, imgType);

       if (bitmap != null) {

           LogUtils.d(TAG, "SyncImageLoader setImageBitmapFromAssets  has Cache filePath = " + filePath);
           iconIV.setVisibility(View.VISIBLE);
           iconIV.setImageBitmap(bitmap);

           return;
       }

       mThreadPool.execute(new Runnable() {
           @Override
           public void run() {

               try {
                   ReqImgBean bean = new ReqImgBean();
                   bean.setListener(listener);
                   bean.setImgUrl(filePath);
                   bean.setContext(mContext);
                   bean.setIcon(iconIV);
                   bean.setImgType(imgType);
                   loadImageFromAssets(bean);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       });
   }
    
    public void loadImageFromAssets(ReqImgBean bean) throws IOException {

            LogUtils.d(TAG, "SyncImageLoader loadImageFromAssets " + ", getChatId = " + bean.getChatId()
                    + ", bean.getImgUrl() = " + bean.getImgUrl());
            if (TextUtils.isEmpty(bean.getImgUrl())) {
                LogUtils.d(TAG, "loadImageFromAssets null == bean.getImgUrl() return");
                return;
            }

            LogUtils.d(TAG, "loadImageFromAssets filepath = " +  bean.getImgUrl());

            Bitmap bitmap = null;
            InputStream is = null;
            try {
                AssetManager am = mContext.getAssets();
                    is = am.open(bean.getImgUrl());
                    bitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != is)
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, e.getMessage());
                    }
            }
            
            
            if (null != bitmap) {
                
                LogUtils.d(TAG, "loadImageFromAssets addCacheUrl bean.getImgUrl() = " +  bean.getImgUrl());

                SyncImageLoader.addCacheUrl(bean.getImgUrl(), bitmap);
            
                bean.setBitmap(bitmap);
                notifySuccessComm(bean);
            }
    }


    public void loadImageFromUrl(ReqImgBean bean) throws IOException {

        try {
            LogUtils.d(TAG, "SyncImageLoader loadImageFromUrl " + ", getChatId = " + bean.getChatId()
                    + ", bean.getImgUrl() = " + bean.getImgUrl());
            if (TextUtils.isEmpty(bean.getImgUrl())) {
                LogUtils.d(TAG, "loadImageFromUrl null == bean.getImgUrl() return");
                return;
            }

            String filepath = FileUtil.findImageFileByPath(bean.getImgUrl());

            LogUtils.d(TAG, "loadImageFromUrl filepath = " + filepath);

            if (null != filepath) {
                LogUtils.v(TAG, "loadImageFromUrl findImageFileByPath " + ", getChatId = " + bean.getChatId()
                        + ", sucess--> filepath = " + filepath);

                Bitmap bitmap = addBitMapToCache(bean.getImgUrl(), bean.getImgType());

                LogUtils.v(TAG, "loadImageFromUrl find local img --> bitmap hight = " + bitmap.getHeight());
                LogUtils.v(TAG, "loadImageFromUrl find local img --> bitmap getWidth = " + bitmap.getWidth());

                if (bitmap != null) {
                    bean.setBitmap(bitmap);
                    notifySuccessComm(bean);
                    return;
                }
            }

            LogUtils.v(TAG, "loadImageFromUrl not find local img --> downloadImg");
            downloadImg(bean);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "loadImageFromUrl Exception = " + e.toString());
        }
    }

    public Bitmap getBitmapFromLocal(String path, int imgType) {
        Bitmap bitmap = null;
        try {
            if (imageCache.get(path) != null) {
                LogUtils.i(TAG, "getBitmapFromLocal from  map cache");
                bitmap = imageCache.get(path);
            }
            if (bitmap == null) {
                LogUtils.i(TAG, "getBitmapFromLocal local file path");
                File file = new File(path);
                if (file.exists()) {
                    LogUtils.i(TAG, "getBitmapFromLocal file.exists()");

                    bitmap = addBitMapToCache(path, imgType);
                } else {
                    LogUtils.i(TAG, "getBitmapFromLocal file is not exist");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString());
        }
        return bitmap;
    }

    String getDrablePath(int resId) {
        return "url:" + resId;
    }

    public Bitmap getBitmapFromLocalDrable(int resId) {
        Bitmap bitmap = null;
        String residpath = getDrablePath(resId);

        LogUtils.d(TAG, "getBitmapFromLocalDrable begin residpath = " + residpath);

        try {
            if (imageCache.get(residpath) != null) {
                bitmap = imageCache.get(residpath);
            }
            
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;

            if (bitmap == null) {

                LogUtils.d(TAG, "getBitmapFromLocalDrable imageCache is null create new bitmap");
                bitmap = BitmapFactory
                        .decodeStream(MyApplication.getAppContext().getResources().openRawResource(resId));

                int width = bitmap.getWidth();

                int height = bitmap.getHeight();

                Matrix matrix = new Matrix();

                matrix.postScale(Utility.getDensity(MyApplication.getAppContext()),
                        Utility.getDensity(MyApplication.getAppContext()));

                // 得到新的图片
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                if (bitmap != null) {
                    addCacheUrl(residpath, bitmap);
                }
            } else {
                LogUtils.d(TAG, "getBitmapFromLocalDrable end from cache  residpath = " + residpath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString());
        }
        return bitmap;
    }

    /**
     * 图片任意形状的放大缩小
     */
    public static Bitmap ZoomToFixShape(Context context, Bitmap pic1) {
        Bitmap tempBitmap = null;
        // int bitH = pic1.getHeight();
        // int bitW = pic1.getWidth();
        /*
         * Matrix mMatrix = new Matrix(); float scoleW = (float) w / (float)
         * bitW; float scoleH = (float) h / (float) bitH;
         */

        int width = pic1.getWidth();
        int height = pic1.getHeight();

        float density = Utility.getDensity(context); // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）

        // 设置想要的大小
        // 计算缩放比例
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(density, density);

        // pic1.recycle();
        return tempBitmap;

    }

    /**
     * 获取压缩后的图片
     * 
     * @param path
     * @param reqWidth
     *            所需图片压缩尺寸最小宽度
     * @param reqHeight
     *            所需图片压缩尺寸最小高度
     * @return
     */
    public Bitmap getBitmapFromLocalImageSize(String path, int reqWidth, int reqHeight) {

        if (null == path) {
            return null;
        }

        // 首先不加载图片,仅获取图片尺寸
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
        options.inJustDecodeBounds = true;
        // 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
        BitmapFactory.decodeFile(path, options);

        // 计算压缩比例,如inSampleSize=4时,图片会压缩成原图的1/4
        int samplesize = calculateInSampleSizess(options, reqWidth, reqHeight);
        options.inSampleSize = samplesize;

        // 当inJustDecodeBounds设为false时,BitmapFactory.decode...就会返回图片对象了
        options.inJustDecodeBounds = false;
        // 利用计算的比例值获取压缩后的图片对象
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSizess(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int bitmapheight = options.outHeight;
        final int bitmapwidth = options.outWidth;
        
        
        int inSampleSize = 1;

        if (bitmapheight > reqHeight || bitmapwidth > reqWidth) {
            final int heightRatio = Math.round((float) bitmapheight / (float) reqHeight);
            final int widthRatio = Math.round((float) bitmapwidth / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        
        LogUtils.i(TAG, "calculateInSampleSizess "
                + "reqHeight = " + reqHeight 
                + "reqWidth = " + reqWidth 
                + "bitmapheight = " + bitmapheight 
                + ", bitmapwidth = " + bitmapwidth
                + ", inSampleSize = " + inSampleSize);

        return inSampleSize;
    }

    /**
     * 计算压缩比例值
     * 
     * @param options
     *            解析图片的配置信息
     * @param reqWidth
     *            所需图片压缩尺寸最小宽度
     * @param reqHeight
     *            所需图片压缩尺寸最小高度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 保存图片原宽高值
        final int height = options.outHeight;
        final int width = options.outWidth;
        // 初始化压缩比例为1
        int inSampleSize = 1;

        // 当图片宽高值任何一个大于所需压缩图片宽高值时,进入循环计算系统
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 压缩比例值每次循环两倍增加,
            // 直到原图宽高值的一半除以压缩值后都~大于所需宽高值为止
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void addCacheUrl(String url, Bitmap bitmap) {
        if (Utility.isNotNull(url) && imageCache != null && bitmap != null) {
            synchronized (imageCache) {
                imageCache.put(url, bitmap);
            }
            LogUtils.v(TAG, "addCacheUrl size--->" + imageCache.size());
            LogUtils.v(TAG, "addCacheUrl maxsize--->" + imageCache.maxSize());
            LogUtils.v(TAG, "addCacheUrl putCount--->" + imageCache.putCount());
            LogUtils.v(TAG, "addCacheUrl evictionCount--->" + imageCache.evictionCount());
        }
    }

    // add by qianch for 引用失效，内存释放 end

    public static void clearCache() {
        /*
         * synchronized (imageCache) { Map<String,Bitmap> caches =
         * imageCache.snapshot(); Set<String> keys = caches.keySet(); for(String
         * k : keys){ Bitmap bitmap = caches.get(k); caches.remove(bitmap);
         * LogUtils.LOGV("imageCache", "clearCache"); } caches = null; }
         */
        System.gc();
    }

    public Bitmap getCache(String imgUrl, int imgType) {
        if (Utility.isNotNull(imgUrl) && imageCache != null) {
            LogUtils.i(TAG, "imageCache.get(url):" + imageCache.get(imgUrl));
            
            String cacheurl = imgUrl;

            if (CommonConstants.TYPE_HEAD_IMG == imgType) {
                cacheurl = imgUrl + CommonConstants.CACHE_HEAD;
            }
            else if (CommonConstants.TYPE_BIG_IMG == imgType) {
                cacheurl = imgUrl + CommonConstants.CACHE_BIG;

            } else if (CommonConstants.TYPE_MID_IMG == imgType) {
                cacheurl = imgUrl + CommonConstants.CACHE_MID;
            }
            
            LogUtils.i(TAG, "getCache cacheurl:" + cacheurl);
            
            return imageCache.get(cacheurl);
        }
        return null;
    }

    public static LruCache<String, Bitmap> getImageCache() {
        return imageCache;
    }

    public static void removeImageCache(String url) {
        if (imageCache != null) {
            synchronized (imageCache) {
                {
                    imageCache.remove(url);
                }

            }
        }

    }

    public static void clearLru() {
        if (imageCache != null) {
            imageCache.evictAll();
        }

    }

    public void freeContext() {
        this.mContext = null;
    }

    public void downloadImg(final ReqImgBean bean) {
        FileOutputStream fOutStream = null;
        try {
            File file = FileUtil.createImageCacheFile(bean, bean.getImgUrl(), bean.getContext());

            if (file != null) {
                LogUtils.i(TAG, "To visit server and create head absolutely path:" + file.getAbsolutePath());
                if (file.length() <= 0) {
                    file.delete();
                } else {

                    Bitmap bitmap = addBitMapToCache(bean.getImgUrl(), bean.getImgType());

                    if (null != bitmap) {

                        if (null != file.getAbsolutePath()) {
                            bean.setLocalPath(file.getAbsolutePath());
                        }
                        bean.setBitmap(bitmap);

                        notifySuccessComm(bean);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString());
        } finally {

            notifyErrorComm(bean);

            if (fOutStream != null) {
                try {
                    fOutStream.flush();
                    fOutStream.close();
                } catch (Exception e) {
                    // TODO: handle exception
                    LogUtils.e(TAG, e.toString());
                }
            }
        }
    }

    private Bitmap addBitMapToCache(String imgUrl, int imgType) {
	        int with = BITMAP_MIN_WIDTH;
	        int hight = BITMAP_MIN_HIGHT;
        String cacheurl = imgUrl;
        LogUtils.d(TAG, "imgType = " + imgType);
        if (CommonConstants.TYPE_HEAD_IMG == imgType) {
            with = BITMAP_HEAD_WIDTH;
            hight = BITMAP_HEAD_WIDTH;
            cacheurl = imgUrl + CommonConstants.CACHE_HEAD;

        }
        else if (CommonConstants.TYPE_BIG_IMG == imgType) {
            with = BITMAP_BIG_WIDTH;
            hight = BITMAP_BIG_HIGHT;
            cacheurl = imgUrl + CommonConstants.CACHE_BIG;

        } else if (CommonConstants.TYPE_MID_IMG == imgType) {
            with = BITMAP_MID_WIDTH;
            hight = BITMAP_MID_HIGHT;
            cacheurl = imgUrl + CommonConstants.CACHE_MID;
        }
        
        Bitmap bitmap = SyncImageLoader.getInstance().getBitmapFromLocalImageSize(
                FileUtil.getImgPath(imgUrl, MyApplication.getAppContext()), with, hight);
       
        LogUtils.i(TAG, "addBitMapToCache imgType = " + imgType);
        
        if (CommonConstants.TYPE_HEAD_IMG == imgType)
        {
            
            int circlelen = Utility.dip2px(MyApplication.getAppContext(), BITMAP_HEAD_WIDTH);
            
            LogUtils.i(TAG, "CommonConstants.TYPE_HEAD_IMG == imgType " + circlelen + "and bitmap width is " + bitmap.getWidth());
            
            
            bitmap = createCircleImage(bitmap, bitmap.getWidth());
        }
        

        if (null != bitmap) {
            SyncImageLoader.addCacheUrl(cacheurl, bitmap);
            return bitmap;
        }

        return null;
    }
    
    
    /**
     * 根据原图和变长绘制圆形图片
     * 
     * @param source
     * @param min
     * @return
     */
    public Bitmap createCircleImage(Bitmap source, int min)
    {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
        
        
   /*     *//**
         * 产生一个同样大小的画布
         *//*
        Canvas canvas = new Canvas(target);
        
        canvas.drawColor(Color.TRANSPARENT);
        *//**
         * 首先绘制圆形
         *//*
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        *//**
         *//*
        *//**
         * 绘制图片
         */
//        canvas.drawBitmap(source, 0, 0, paint);
        
        Canvas c = new Canvas(target);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        Path path = new Path(); 
        path.addCircle(min / 2, min / 2, min / 2, Path.Direction.CW); 
        c.clipPath(path); //裁剪区域 
        c.drawBitmap(source, 0, 0, paint);//把图画上去
        
        if (null != source)
        {
            source.recycle();
            source = null;
        }
        
        
        
        
        return target;
    }

    private void notifySuccessComm(final ReqImgBean bean) {
        if (bean != null) {
            
            final OnImgLoadListener listener = bean.getListener();

            if (listener != null) {
                listener.onSuccess(bean);
            }

            Utility.freeReqFileBean(bean);
        } else {
            notifyErrorComm(bean);
        }
    }

    public void notifyErrorComm(final ReqImgBean bean) {

//        mReqBeanListMap.remove(bean.getImgUrl());

        final OnImgLoadListener listener = bean.getListener();

        if (listener != null) {
            listener.onError(bean);
        }

        Utility.freeReqFileBean(bean);
    }
}
