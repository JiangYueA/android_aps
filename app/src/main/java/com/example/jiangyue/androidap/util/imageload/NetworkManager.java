/**
 * 
 */
package com.example.jiangyue.androidap.util.imageload;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class NetworkManager {
    private final static String LOG_TAG = NetworkManager.class.getSimpleName();
    public final static String PROXY_HOST = "proxy_host";
    public final static String PROXY_PORT = "proxy_port";

    public final static int MOBILE_TYPE = 1;
    public final static int WIFI_TYPE = 2;
    public final static int NO_NETWORK = 3;

    public static String getNetWorkType(ConnectivityManager con) {
        // showNetworkInfo(con);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        return getNetworkType(networkInfo);

    }

    private static String getNetworkType(NetworkInfo networkInfo) {
        String networkType = "UNKNOWN";
        try {
            if (networkInfo != null) {
                switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI: {
                    networkType = "WIFI";
                    break;
                }
                default: {
                    if (!TextUtils.isEmpty(networkInfo.getExtraInfo())) {
                        networkType = networkInfo.getExtraInfo().toUpperCase();
                    } else {
                        Log.w(LOG_TAG, "networkInfo.getExtraInfo() is empty!");
                        networkType = networkInfo.getTypeName();
                    }
                    break;
                }
                }
            }

            networkType = TextUtils.isEmpty(networkType) ? "UNKNOWN" : networkType;

            Log.w(LOG_TAG, "getNetworkType = " + networkType);

        } catch (Exception e) {
            networkType = "";
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }
        return networkType;

    }

    /**
     * 检测网络是否连接
     * 
     * @return
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Map<String, Object> getProxy() {
        Map<String, Object> res = new HashMap<String, Object>();
        try {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int proxyPort = android.net.Proxy.getDefaultPort();

            Log.w(LOG_TAG, "proxyHost--->" + proxyHost);
            Log.w(LOG_TAG, "proxyPort--->" + proxyPort);

            if (!TextUtils.isEmpty(proxyHost) && (proxyPort > 0)) {
                res.put(PROXY_HOST, proxyHost);
                res.put(PROXY_PORT, proxyPort);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
            return null;

        }
        return res;
    }

    /**
     * 获取网络 /wifi/2g,3g/.
     * 
     * @param context
     * @return
     */
    public static String getCurrentNetwork(Context context) {
        String netType = "UNKNOWN";
        if (isNetworkAvailable(context, ConnectivityManager.TYPE_MOBILE)) {
            netType = getNetwork(context);
        } else if (isNetworkAvailable(context, ConnectivityManager.TYPE_WIFI)) {
            netType = "WIFI";
        }
        return netType;
    }

    public static boolean isNetworkAvailable(Context context, int type) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netWork = connectivity.getNetworkInfo(type);
        return (null != netWork && netWork.isAvailable() && netWork.isConnected());
    }

    /**
     * 中国移动: 2g(GPRS/EDGE),3g(TD-SCDMA/HSPA(包含HSDPA/HSUPA)) 中国电信：
     * 2g(CDMA),3g(CDMA 2000(基于EVDO制式)) 中国联通:
     * 2g(GPRS/EDGE),3g(HSPA，分为HSDPA和HSUPA两种) 获取网络 2g或3g
     * 
     * @param context
     * @return WIFI or MOBILE
     */
    public static String getNetwork(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = manager.getNetworkType();
        String typeString = "UNKNOWN";
        if (type == TelephonyManager.NETWORK_TYPE_CDMA) {
            typeString = "2G";
        } else if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
            typeString = "2G";
        } else if (type == TelephonyManager.NETWORK_TYPE_EVDO_0) {
            typeString = "3G";
        } else if (type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
            typeString = "3G";
        } else if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
            typeString = "2G";
        } else if (type == TelephonyManager.NETWORK_TYPE_HSDPA) {
            typeString = "3G";
        } else if (type == TelephonyManager.NETWORK_TYPE_HSPA) {
            typeString = "3G";
        } else if (type == TelephonyManager.NETWORK_TYPE_HSUPA) {
            typeString = "3G";
        } else if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
            typeString = "3G";
        } else if (type == TelephonyManager.NETWORK_TYPE_LTE) {
            typeString = "4G";
        } else {
            typeString = "UNKNOWN";
        }
        return typeString;
    }

}
