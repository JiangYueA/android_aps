package com.example.jiangyue.androidap.util.imageload;

import android.util.Log;

/**
 * 
 * 打印Log
 * <功能详细描述>
 * 
 * @author 姓名 工号
 * @version [版本号, 2014年7月28日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class LogUtils {
    private static boolean printLog = true;
    //默认给false，软件打开的时候，加载变量值；日志功能开启关闭的时候，修改值
    public static boolean isWriteLog = true;
    
    //默认给false，软件打开的时候，加载变量值；日志功能开启关闭的时候，修改值
    public static boolean isWriteLogES = true;
    
//    public static String signer = "zte.com.imclient " + Utility.getDateTimeStamp() + ": ";
   
    public static String signer = "zte.com.imclient " + ": ";
    
    public static void d(String tag, String message) {
    	tag = signer +tag;
        if (printLog) {
            Log.d(tag, message);
        }
        if (isWriteLog) {
            
        }
    }

    public static void v(String tag, String message) {
    	tag = signer +tag;
        if (printLog) {
            Log.v(tag, message);
        }

        if (isWriteLog) {
        }
    }

    public static void i(String tag, String message) {
    	tag = signer +tag;
        if (printLog) {
            Log.i(tag, message);
        }

        if (isWriteLog) {
        }
    }

    public static void w(String tag, String message) {
    	tag = signer +tag;
        if (printLog) {
            Log.w(tag, message);
        }

        if (isWriteLog) {
            
        }
    }

    public static void e(String tag, String message) {
    	tag = signer +tag;
        if (printLog) {
            Log.e(tag, message);
        }
        
        if (isWriteLog) {
        }

        if (isWriteLogES) {
        }
        }

    public static void e(String tag, Exception ex) {
        tag = signer + tag;
        String message = getException(ex);

        Log.e(tag, message);
        
        if (isWriteLog) {
            
        }

        if (isWriteLogES) {
            if (null != message && !message.isEmpty()) {
            }
        }

    }
    
    public static void s(String tag, String message) {
        tag = signer + tag;

        Log.e(tag, message);
        
        
        if (isWriteLog) {
            
        }

        if (isWriteLogES) {
            if (null != message && !message.isEmpty()) {
            }
        }

    }

/*    public static void e(final String tag, Throwable ex) {
        String message = getException(ex);

        Log.e(tag, message);

        Utility.writeLog(IMApplication.getAppContext(), tag, message);
    }*/
    

    public static String getException(Exception ex) {
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("\n" + "uncaughtException getLocalizedMessage--: " + ex.getLocalizedMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString() + "\n");
        }
        return null;
    }

    public static String getException(Throwable ex) {
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("\n" + "uncaughtException getLocalizedMessage--: " + ex.getLocalizedMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString() + "\n");
        }
        return null;
    }
}
