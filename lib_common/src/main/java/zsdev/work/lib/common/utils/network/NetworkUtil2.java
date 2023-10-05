package zsdev.work.lib.common.utils.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created: by 2023-08-17 16:37
 * Description: 网络工具类
 * Author: 张松
 */
public class NetworkUtil2 {

    /**
     * 网络可用状态码
     */
    public static int NET_CONNECTION_BAIDU_OK = 1;

    /**
     * 网络不可用状态码
     */
    public static int NET_CONNECTION_BAIDU_TIMEOUT = 2;

    /**
     * 网络未就绪状态码
     */
    public static int NET_NOT_PREPARE = 3;

    /**
     * 网络错误状态码
     */
    public static int NET_ERROR = 4;


    /**
     * 检查当前设备网络是否可用
     *
     * @param context 上下文
     * @return 连接状态 true 网络可用 false 网络不可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        return null != info && info.isAvailable();
    }

    /**
     * 获取返回本地IP地址
     *
     * @return IP
     */
    public static String getLocalIpAddress() {
        String ret = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ret = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * 返回设备当前网络状态
     *
     * @param context 上下文
     * @return 设备网络状态
     */
    public static int getNetState(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
                if (networkinfo != null) {
                    if (networkinfo.isAvailable() && networkinfo.isConnected()) {
                        if (!connectionNetwork())
                            return NET_CONNECTION_BAIDU_TIMEOUT;
                        else
                            return NET_CONNECTION_BAIDU_OK;
                    } else {
                        return NET_NOT_PREPARE;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NET_ERROR;
    }

    /**
     * ping "http://www.baidu.com"
     *
     * @return x
     */
    static private boolean connectionNetwork() {
        boolean result = false;
        HttpURLConnection httpUrl = null;
        try {
            httpUrl = (HttpURLConnection) new URL("http://www.baidu.com").openConnection();
            // TIMEOUT
            int TIMEOUT = 3000;
            httpUrl.setConnectTimeout(TIMEOUT);
            httpUrl.connect();
            result = true;
        } catch (IOException ignored) {
        } finally {
            if (null != httpUrl) {
                httpUrl.disconnect();
            }
            httpUrl = null;
        }
        return result;
    }

    /**
     * 是否3G网络
     *
     * @param context 上下文
     * @return 连接状态 true 已连接3G网络 false未连接3G网络
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 是否WIFI连接
     *
     * @param context 上下文
     * @return 连接状态 true 已连接WIFI false未连接WIFI
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 是否2G网络
     *
     * @param context 上下文
     * @return 连接状态 true 已连接2G网络 false未连接2G网络
     */
    public static boolean is2G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && (activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
                || activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || activeNetInfo
                .getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA);
    }

    /**
     * 当前设备网络状态是否已连接WIFI
     *
     * @param context 上下文
     */
    @SuppressLint("MissingPermission")
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

}
