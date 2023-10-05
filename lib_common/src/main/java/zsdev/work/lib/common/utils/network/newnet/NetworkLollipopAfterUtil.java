package zsdev.work.lib.common.utils.network.newnet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class NetworkLollipopAfterUtil {

    /**
     * >= Android 10（Q版本）推荐
     * 当前使用MOBILE流量上网
     */

    public static boolean isMobileNetwork(Context context) {
        //获取网络连接管理器对象
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取当前活动的网络对象信息，网络切换后会变更为新的 Network 对象
        Network network = cm.getActiveNetwork();
        if (null == network) {
            return false;
        }
        //获取描述当前网络对象属性对象
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return false;
        }
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }


    /**
     * >= Android 10（Q版本）推荐
     * 当前使用WIFI上网
     */

    public static boolean isWifiNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (null == network) {
            return false;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return false;
        }
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }


    /**
     * >= Android 10（Q版本）推荐
     * 当前使用以太网上网
     */
    public static boolean isEthernetNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (null == network) {
            return false;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return false;
        }
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }


    /**
     * >= Android 10（Q版本）推荐
     * NET_CAPABILITY_INTERNET：表示是否连接到互联网，即是否连接上了WIFI或者移动蜂窝网络，这个为TRUE不一定能正常上网
     * NET_CAPABILITY_VALIDATED：表示是否确实能和连接的互联网通信，这个为TRUE，才是真的能上网
     * 判断当前网络可以正常上网
     * 表示此连接此网络并且能成功上网。  例如，对于具有NET_CAPABILITY_INTERNET的网络，这意味着已成功检测到INTERNET连接。
     */
    public static boolean isConnectedAvailableNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (null == network) {
            return false;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return false;
        }
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    /**
     * 检测网络连接状态是否可用
     *
     * @return 连接状态是否可用
     */
    public static boolean isNetAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NetworkInfo mWiFiNetworkInfo = cm.getActiveNetworkInfo();
                    if (mWiFiNetworkInfo != null && mWiFiNetworkInfo.isConnected() && mWiFiNetworkInfo.isAvailable()) {
                        if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {//WIFI
                            return true;
                        } else if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {//移动数据
                            return true;
                        }
                    }
                } else {
                    Network network = cm.getActiveNetwork();
                    if (network != null) {
                        NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                        if (nc != null) {
                            if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {//WIFI
                                return true;
                            } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {//移动数据
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * >= Android 10（Q版本）推荐
     * 获取成功上网的网络类型
     * value = {
     * TRANSPORT_CELLULAR,   0 表示此网络使用蜂窝传输。
     * TRANSPORT_WIFI,       1 表示此网络使用Wi-Fi传输。
     * TRANSPORT_BLUETOOTH,  2 表示此网络使用蓝牙传输。
     * TRANSPORT_ETHERNET,   3 表示此网络使用以太网传输。
     * TRANSPORT_VPN,        4 表示此网络使用VPN传输。
     * TRANSPORT_WIFI_AWARE, 5 表示此网络使用Wi-Fi感知传输。
     * TRANSPORT_LOWPAN,     6 表示此网络使用LoWPAN传输。
     * TRANSPORT_TEST,       7 指示此网络使用仅限测试的虚拟接口作为传输。
     * TRANSPORT_USB,        8 表示此网络使用USB传输
     * }
     */
    public static int getConnectedNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (null == network) {
            return -1;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return -1;
        }
        if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return NetworkCapabilities.TRANSPORT_CELLULAR;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return NetworkCapabilities.TRANSPORT_WIFI;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                return NetworkCapabilities.TRANSPORT_BLUETOOTH;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return NetworkCapabilities.TRANSPORT_ETHERNET;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return NetworkCapabilities.TRANSPORT_VPN;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return NetworkCapabilities.TRANSPORT_WIFI_AWARE;
                }
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    return NetworkCapabilities.TRANSPORT_LOWPAN;
                }
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_USB)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    return NetworkCapabilities.TRANSPORT_USB;
                }
            }
        }
        return -1;
    }
}