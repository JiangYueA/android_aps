package com.example.jiangyue.androidap.util.imageload;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;

import com.example.jiangyue.androidap.MyApplication;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    public static final String IM_PATH = "imclient" + File.separator;
    public static final String DOWN_LOAD_PATH = IM_PATH + "download" + File.separator;
    public static final String APK_FILE_PATH = IM_PATH + "apk" + File.separator;
    public static final String IMG_FILE_DIR = IM_PATH + "img" + File.separator;
    public static final String APK_FILE_NAME = "new.apk";
    public static final String SAMPLE_DEFAULT_DIR = IM_PATH + "imrecorder" + File.separator;
    /** 头像存储的目录 **/
    public static final String PORTRAIT_PATH = IM_PATH +"videoportrait" + File.separator;
    public static final String PREFIX_LOCAL_VIDEO = "local_";			  //自己头像的前缀

    
    public static final String ASSETS_PATH = "assets:";
    
    public static File testCreateCacheFile(String fileName, String downloadUrl, Context context, String paramPath) {
        File cacheFile = null;
        DataInputStream in = null;
        OutputStream out = null;
        try {
            String filepath = getFileDir(context, paramPath);
            cacheFile = new File(filepath + fileName);
            if (cacheFile.exists()) {
                return cacheFile;
            } else {
                if (!new File(filepath).exists()) {
                    Log.w("loadImageFromUrl", "mkdirs");
                    new File(filepath).mkdirs();
                }

                in = new DataInputStream(context.getResources().getAssets().open("html/test.xml"));
                out = new FileOutputStream(cacheFile);
                byte[] buffer = new byte[1024];
                int byteread = 0;
                while ((byteread = in.read(buffer)) != -1) {
                    out.write(buffer, 0, byteread);
                }
            }
        } catch (Exception e) {
            cacheFile = null;
            Log.e(TAG, " FileUtil createCacheFile Exception " + e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        }
        return cacheFile;
    }

    public static String saveImageToDir(Context context, Bitmap bitmap, String picName) {
        if (null == bitmap || TextUtils.isEmpty(picName)) {
            return null;
        }
        FileOutputStream out = null;

        try {
            String filePathdir = null;

            if (Utility.getStorageResult(context)) {
                filePathdir = Environment.getExternalStorageDirectory() + File.separator + IM_PATH;
            } else {
                filePathdir = context.getExternalCacheDir().getAbsolutePath() + File.separator + IM_PATH;
            }

            File sampleDir = new File(filePathdir);

            if (!sampleDir.exists()) {
                sampleDir.mkdirs();
            }

            String filePath = sampleDir.getAbsolutePath() + File.separator + picName;


            out = new FileOutputStream(new File(filePath));

            if (null != out) {
                bitmap.compress(CompressFormat.JPEG, 100, out);
                out.flush();
                return filePath;
            }

        } catch (Exception e) {
            Log.e(TAG, "saveImageToDir exception-->" + e.getMessage());
        } finally {
            if (null != out) {
                try {
                    out.close();
                    bitmap.recycle();
                } catch (IOException e) {
                }
            }

        }
        return null;
    }

    public static String findImageFileByPath(String imgFilePath) {
        try {

            File file = new File(imgFilePath);
            if (file.exists()) {
                return imgFilePath;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "findImageFileByPath imgFilePath Exception " + e.getMessage());
            e.printStackTrace();
        }
        
        
        return null;
    }

    public static boolean isSDCardAvaliable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getFileDir(Context context, String paramPathdir) {
        // modify by wangtao on 2013-1-10 modify filepath start

        String pahtdir = null;
        if (Utility.isSDCardAvailable() && Utility.isStorageAvailable()) {
            pahtdir = Environment.getExternalStorageDirectory() + File.separator + paramPathdir;
            Log.d(TAG, "getFileDir getExternalStorageDirectory sdcard " + pahtdir);
        } else {
            pahtdir = context.getCacheDir().getPath() + File.separator + paramPathdir;
            Log.d(TAG, "getFileDir filepath " + pahtdir);
        }
        return pahtdir;
    }

    /**
     * 获取调用的缓存路径
     * 
     * @since 2013-02-21
     * @author jiangym
     * @return
     */
    public static String getCacheDir(Context context) {
        String packageName = "/." + context.getPackageName();
        String filepath = context.getCacheDir().getPath() + packageName;
        if (Build.VERSION.SDK_INT < 8) {
            if (Utility.getStorageResult(context)) {
                filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + packageName;
            }
        } else {
            if (Utility.getStorageResult(context)) {
                filepath = context.getExternalCacheDir().getAbsolutePath() + packageName;
            }
        }
        return filepath + "/" + "surfnews/news/";
    }

    public static String resetWidth(String val) {
        if (Utility.isNum(val)) {
            return val;
        } else {
            val = "320";
        }
        return val;
    }

    public static String resetScale(String val) {
        if (Utility.isNum(val)) {
            return val;
        } else {
            val = "1.0";
        }
        return val;
    }

    private static String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String month = sdf.format(new Date().getTime());
        return month;
    }

    private static String getLastMonth() {
        String lastMonth = "";

        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        long time = new Date().getTime();
        int currentMonth = Integer.parseInt(sdf.format(time));
        sdf = new SimpleDateFormat("yyyy");
        int currentYear = Integer.parseInt(sdf.format(time));
        if (currentMonth > 1) {
            currentMonth--;
        } else {
            currentMonth = 12;
            currentYear--;
        }

        lastMonth += currentYear + currentMonth;
        return lastMonth;
    }

    public static void deleteApkFile(Context context) {
        if (Utility.getStorageResult(context)) {
            String filepath = Environment.getExternalStorageDirectory() + APK_FILE_PATH;
            File file = new File(filepath + APK_FILE_NAME);
            if (file.exists()) {
                file.delete();
            }
        } else {
            context.deleteFile(APK_FILE_NAME);
        }
    }

    public static String findFileByPath(String paramPath) {
        try {
            File file = new File(paramPath);
            if (file.exists()) {
                return paramPath;
            }
        } catch (Exception e) {
            Log.e(TAG, "findFileByPath paramPath Exception " + e.getMessage());
        }
        return null;
    }

    public static String getPathRel(String srcPath, String pnPath) {

        if (TextUtils.isEmpty(srcPath)) {
            return null;
        }
        String pathRel = null;
        if (null == pnPath || TextUtils.isEmpty(pnPath)) {
            pathRel = getLastName(srcPath);
        } else {
            int len = srcPath.indexOf(pnPath);
            if (len >= 0) {
                // modify by qianch for 截取index start
                pathRel = srcPath.substring(len + pnPath.length() + 1);
                // modify by qianch for 截取index end
            } else {
                pathRel = getLastName(srcPath);
            }
        }

        return pathRel;
    }

    private static String getLastName(String srcPath) {

        int len = srcPath.lastIndexOf("/") + 1;
        if (len > 0) {
            srcPath = srcPath.substring(len, srcPath.length());
        }

        return srcPath;
    }

    /**
     * zhucl-ca@cplatform.com 2013.8.5
     * 删除目录（文件夹）以及目录下的文件
     * 
     * @param file
     *            被删除目录的文件路径
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
                file.delete();
            }
        }
    }

    /**
     * 获取指定文件的长度
     * 
     * @param path
     */
    public static long getFileLength(String path) {
        long len = 0;
        try {
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                len = file.length();
            }
        } catch (Exception e) {
            Log.e(TAG, "getFileLength Exception -- " + e.toString());
            e.printStackTrace();
        }
        return len;
    }

    /**
     * 使用decodeStream获取file
     * 
     * @param file
     * @return
     */
    public static Bitmap getBitmapFromFile(File file) {
        try {
            if (null != file) {
                FileInputStream fis = new FileInputStream(file);
                return getBitmapFromFileInputStream(fis);
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.toString());
            return null;
        }
    }

    public static Bitmap getBitmapFromFileInputStream(FileInputStream is) {
        if (is == null)
            return null;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Modified by Li Yong
            // Compare image quality under RGB_565 with ARGB_8888
            options.inPreferredConfig = Config.ARGB_8888;
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
                    Log.e(TAG, e.toString());
                }
            }
        }
        return bitmap;
    }

    /**
     * 根据视频在手机中的地址路径取得指定的视频缩略图
     * 
     * @param cr
     *            本地视频地址
     * @return 返回bitmap类型数据
     */

    public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Config.ARGB_8888;
        Cursor cursor = cr.query(uri, new String[] { MediaStore.Video.Media._ID }, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID)); // image id in
                                                                                              // image table.s

        if (videoId == null) {
            return null;
        }
        cursor.close();
        long videoIdLong = Long.parseLong(videoId);
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong, Images.Thumbnails.MICRO_KIND, options);

        return bitmap;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * 
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param imgType 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, final int imgType) {
        Bitmap bitmap = SyncImageLoader.getInstance().getBitmapFromLocal(videoPath, imgType);
        if (bitmap == null) {
            // 获取视频的缩略图
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, imgType);
            Log.i(TAG, "videoPath:" + videoPath + "    bitmap:" + bitmap);
            if (null != bitmap) {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                if (bitmap != null) {
                    SyncImageLoader.addCacheUrl(videoPath, bitmap);
                }
            }
        } else {

        }
        return bitmap;
    }

    /**
     * 将long类型的秒数转变为00:00的字符串类型
     */
    public static String getChatroomFormatTime(long time)
    {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = (int) time;
        if(second >= 60){
            minute = second / 60;
            second = second % 60;
        }
        if(minute >= 60){
            hour = minute / 60;
            minute = minute % 60;
        }
        if(hour > 0){
            return (getTwoLength(hour) + ":" + getTwoLength(minute)  + ":"  + getTwoLength(second));
        } else {
            return (getTwoLength(minute)  + ":"  + getTwoLength(second));
        }
    }

    /**
     * 十位数补零操作
     * @param data
     * @return
     */
    private static String getTwoLength(int data) {
        if(data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }
    
    /**
     * 将文件file.length获得的long类型转化为B、KB、MB、GB格式
     */
    public static String formateFileSize(long length) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (length < 1024) {
            fileSizeString = df.format((double) length) + "B";
        } else if (length < 1024 * 1024) {
            fileSizeString = df.format((double) length / 1024) + "K";
        } else if (length < 1024 * 1024 * 1024) {
            fileSizeString = df.format((double) length / (1024 * 1024)) + "M";
        } else {
            fileSizeString = df.format((double) length / (1024 * 1024 * 1024)) + "G";
        }

        return fileSizeString;
    }

    public static final String CAMERA_DIR = "/dcim/";
    public static final String ALBUM_NAME = "CameraSample";

    public static File getAlbumStorageDir(String albumName) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName);
        } else {
            return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName);
        }
    }
    
    /**
	 * 获取Portrait存放路径
	 * @param fileName
	 * @return
	 */
	public static String getPortraitFilePath(String fileName) {
		
		String dirPath = FileUtil.getFileDir(MyApplication.getAppContext(), FileUtil.PORTRAIT_PATH);
		
		File localFile = new File(dirPath , fileName);
		
		
		LogUtils.d(TAG, "localFile getAbsolutePath is " + localFile.getAbsolutePath());
		
		return localFile.getAbsolutePath();
	}
	
	/**
	 * 根据路径加载头像
	 * @param fileName
	 * @return
	 */
	public static File loadVideoPorprtaitPath(String fileName) {
		
		File file = new File(FileUtil.getFileDir(MyApplication.getAppContext(), FileUtil.PORTRAIT_PATH));
		LogUtils.d(TAG, "loadVideoPorprtaitPath and file path is " + file.getAbsolutePath());
		if(file.isDirectory() && file.exists()) {
			File[] listFiles = file.listFiles(new CCPFilenameFilter(fileName));
			LogUtils.d(TAG, "listFiles size is " +  listFiles.length);
			if(listFiles != null && listFiles.length > 0) {
				return listFiles[0];
			}
		}
		return null;
	}
	
	/**
	 * 创建头像存储路径
	 * @param fileName
	 * @param ext
	 * @return
	 */
	public static File CreatePortraitFilePath(String fileName , String ext) {

		LogUtils.d(TAG, "fileName = " + fileName + "and ext =" + ext);
		
		String filepath = FileUtil.getFileDir(MyApplication.getAppContext(), FileUtil.PORTRAIT_PATH);
        File sampleDir = new File(filepath);
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        
        File file = new File (filepath + fileName + "." + ext);
		return file;
	}
	
	private static class CCPFilenameFilter implements FilenameFilter{

		String fileName = null;
		 @SuppressWarnings("unused")
		public CCPFilenameFilter(String fileNoExtensionNoDot)
		 {
			 fileName = fileNoExtensionNoDot;
		 }
		 
		@Override
		public boolean accept(File dir, String filename) {
			return filename.startsWith(fileName);
		}
	}

    /**
     * 判断该图片url命名的图片是否已经存在，存在则返回改图片文件 否则：不存在则调用httpclientUtil的downloadImg方法下载图片
     */
    public static File createImageCacheFile(ReqImgBean bean, String url, Context context) {
        String fileName = MD5.digest2Str(url);
        File cacheFile = null;
        try {
            String pathdir = getFileDir(context, IMG_FILE_DIR);
            cacheFile = new File(pathdir + fileName);

            LogUtils.i(TAG, " createImageCacheFile pathdir: " + pathdir + " cacheFile: " + cacheFile);

            if (cacheFile.exists()) {
                return cacheFile;
            } else {
                if (!new File(pathdir).exists()) {
                    LogUtils.w("loadImageFromUrl", "mkdirs");
                    new File(pathdir).mkdirs();
                }
                // cacheFile.createNewFile();
                cacheFile = HttpClientUtil.downloadImageFile(context, url, cacheFile);
            }
        } catch (Exception e) {
            cacheFile = null;
            Log.e(TAG, " FileUtil createImageCacheFile Exception " + e.getMessage());
            e.printStackTrace();
        }
        return cacheFile;
    }

    public static String getImgPath(String url, Context context) {
        try {
            if (null != url && !url.startsWith("http")) {
                return url;
            }

            String fileName = MD5.digest2Str(url);
            String filedir = getFileDir(context, FileUtil.IMG_FILE_DIR);
            String imgUrl = filedir + fileName;
            return imgUrl;
        } catch (Exception e) {
            Log.e(TAG, "findImageFileByPath paramPath Exception " + e.getMessage());
            e.printStackTrace();
        }

        return null;

    }
}