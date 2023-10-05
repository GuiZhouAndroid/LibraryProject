package zsdev.work.lib.common.utils.network.newnet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.Objects;

/**
 * Created: by 2023-09-25 13:04
 * Description: Android 23 6.0及以上 观察网络状态变化
 * Author: 张松
 */

@SuppressLint("NewApi")
public class NetworkLiveData extends LiveData<NetworkState> {
    private static Context mContext;
    private static NetworkLiveData myNetworkLiveData;
    private final ConnectivityManager manager;
    private final ConnectivityManager.NetworkCallback networkCallback;

    public NetworkLiveData(Context context) {
        mContext = context.getApplicationContext();
        networkCallback = new NetworkCallbackImpl();
        manager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 创建单例
     *
     * @return MyNetworkLiveData 单例
     */
    public static NetworkLiveData getInstance(Context context) {
        if (myNetworkLiveData == null) {
            Log.i("NetworkLiveData", "getInstance：sdk大于23");
            myNetworkLiveData = new NetworkLiveData(context);
        }
        return myNetworkLiveData;
    }

    /**
     * 当活动观察者的数量从0更改为1时调用。
     * 这个回调可以用来知道这个LiveData正在被使用，因此应该保持最新。
     * 注册监听网络变化的广播，即ConnectivityManager.CONNECTIVITY_ACTION
     */
    @Override
    protected void onActive() {
        super.onActive();
        manager.registerDefaultNetworkCallback(networkCallback);
        Log.i("NetworkLiveData", "onInactive：注册监听网络变化的广播");
        //在首次注册网络监听完成之后，判断有无网络，切换网络监听在NetworkCallback中的onCapabilitiesChanged()中判断
        if (!NetworkLollipopAfterUtil.isNetAvailable(mContext)) {
            getInstance(mContext).postValue(NetworkState.NOT_NETWORK_CHECK);
            Log.i("NetworkLiveData", "onInactive：网络不可用，请检查网络！");
        }
    }

    /**
     * 当活动观察者的数量从1更改为0时调用。
     * 这并不意味着没有观察者，可能仍然有观察者，但他们的生命周期状态没有启动或恢复（就像后堆栈中的“活动”）。
     * 您可以通过hasObserver（）检查是否有观察者。
     * 注销广播
     */
    @Override
    protected void onInactive() {
        super.onInactive();
        manager.unregisterNetworkCallback(networkCallback);
        Log.i("NetworkLiveData", "onInactive：注销网络监听广播");
    }

    /**
     * getActiveNetwork需要api > 23,所以在使用时最好加入对于api版本的判断，在 23 以下的仍使用 NetworkInfo
     * 在启动VPN时(在Android10上会这样，在Android7.1.1不会有任何的方法回调)，启动成功后会直接再回调onAvailable方法(不回调onLost)，网络变成WIFI + VPN或进CELLULAR + VPN
     * 注：不要使用VpnManager.isOnline()来判断，这个并不准确。从wifi vpn切换到APN网时，VPN图标还在，而且VpnManager.isOnline()返回true，实际上此时的VPN已经失效
     * 切换网络时，如果VPN没有断开，则不需要做任何处理，因为ip会使用VPN的ip，数据请求权限什么都不会变的。
     * 注：在android10系统中，切换网络后如果VPN没有断开，则不会执行onAvailable方法回调
     * 在Android7.1.1 VPN连着时，切换网络VPN就会断开，在Android10不会断开，但是在Android10不回调onAvailable（注：Android10启动VPN成功后会再回调此方法，这种情况也是不需要执行后面代码的）
     * 从移动数据网络切换到wifi网络，此时系统不会回调onLost方法，而是直接回调onAvailable。此时再断开Wifi会回调onLost方法，接着回调onAvailable变成移动数据网络
     * 在Android10（在Android7.1.1中试验是会调用onAvailable的,因为切换时VPN会断开），开着VPN，移动数据网络切换到wifi，只回调onCapabilitiesChanged方法。如果没开VPN，则会回调onAvailable方法。WIFI开着VPN切换到移动网也只回调onCapabilitiesChanged
     * 开着VPN切换网络只回调onCapabilitiesChanged的前提应该是切换后VPN并没有断开，如果断开了应该会回调onAvailable方法
     * 从一个wifi切换到另一个wifi，先回调onLost，然后onAvailable是移动网络，wifi连上后又回调一次onAvailable是wifi网络
     */

    private static class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

        /**
         * 网络连接成功 但不能判断该网络是否可以正常上网
         * 在有网络的情况下注册监听器后这个函数就会立马被调用
         * 网络切换时，拿connectivityManager.activeNetwork来判断是不准确的，
         * 比如从一个wifi切换到另一个wifi时会先断开当前wifi，然后立马回调onAvailable，网络变成了移动数据网络。
         * 之后连上另一个wifi时又回调onAvailable，网络变成了wifi网络。
         * 在变成移动数据网络的时候，拿connectivityManager.activeNetwork来判断是否是移动网络显示为false，
         * 而使用onAvailable中的参数network来判断则为true。
         *
         * @param network 当前活动的网络对象信息，网络切换后会变更为新的 Network 对象
         */
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            Log.i("NetworkLiveData: ", "onAvailable：sdk大于23==网络连接成功，通知可以使用的时候调用====onAvailable===");
        }

        /**
         * 当网络连接超时或网络请求达不到可用要求时调用
         */
        @Override
        public void onUnavailable() {
            Log.i("NetworkLiveData", "onUnavailable：sdk大于23==当网络连接超时或网络请求达不到可用要求时调用====onUnavailable===");
            super.onUnavailable();
            getInstance(mContext).postValue(NetworkState.NETWORK_CONNECT_Fail);
        }

        /**
         * 当访问指定的网络被阻止或解除阻塞时调用
         *
         * @param network 当前活动的网络对象信息，网络切换后会变更为新的 Network 对象
         * @param blocked 是否解除阻塞
         */
        @Override
        public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
            Log.i("NetworkLiveData", "onBlockedStatusChanged：sdk大于23==当访问指定的网络被阻止或解除阻塞时调用===onBlockedStatusChanged==");
            super.onBlockedStatusChanged(network, blocked);
        }

        /**
         * 当网络正在断开连接时调用
         *
         * @param network     当前活动的网络对象信息，网络切换后会变更为新的 Network 对象
         * @param maxMsToLive 断开时
         */
        @Override
        public void onLosing(@NonNull Network network, int maxMsToLive) {
            Log.i("NetworkLiveData", "onLosing：sdk大于23==当网络正在断开连接时调用===onLosing===");
            super.onLosing(network, maxMsToLive);
        }

        /**
         * 当网络已断开连接时调用
         *
         * @param network 当前活动的网络对象信息，网络切换后会变更为新的 Network 对象
         */
        @Override
        public void onLost(@NonNull Network network) {
            Log.i("NetworkLiveData：", "onLost：sdk大于23 当网络已断开连接时调用===onLost===");
            super.onLost(network);
            getInstance(mContext).postValue(NetworkState.NOT_NETWORK_CHECK);

        }

        /**
         * 当网络连接的属性被修改时调用
         *
         * @param network        当前活动的网络对象信息，网络切换后会变更为新的 Network 对象
         * @param linkProperties 网络详细信息 如 DNS、IP 、interface name 、proxy
         */
        @Override
        public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
            Log.i("NetworkLiveDataMAfter", "onLinkPropertiesChanged：sdk大于23==当网络连接的属性被修改时调用===onLinkPropertiesChanged===");
            super.onLinkPropertiesChanged(network, linkProperties);
        }

        /**
         * 检测网络类型
         * 当网络状态改变时回调，比如信号强度切换，或者连接上网络—>断开网络，或者 mobile网络切换到Wifi网络，都会回调
         *
         * @param network             当前活动的网络对象信息，网络切换后会变更为新的 Network 对象
         * @param networkCapabilities 当前网络对象属性对象
         */
        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);

            //表明此网络连接验证成功
            if (NetworkLollipopAfterUtil.isNetAvailable(mContext)) {
                Log.i("NetworkLiveData", "onCapabilitiesChanged ---> ====网络可正常上网===网络类型为： " + NetworkLollipopAfterUtil.getConnectedNetworkType(mContext));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    //通过网络连接管理器对象获取正在使用的网络信息
                    NetworkInfo networkInfo = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                    switch (Objects.requireNonNull(networkInfo).getType()) {
                        case ConnectivityManager.TYPE_MOBILE:
                            getInstance(mContext).postValue(NetworkState.MOBILE);
                            Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk小于23 蜂窝网络>>>>>>>>");
                            break;
                        case ConnectivityManager.TYPE_WIFI:
                            getInstance(mContext).postValue(NetworkState.WIFI);
                            Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk小于23 wifi>>>>>>>>");
                            break;
                        default:
                            getInstance(mContext).postValue(NetworkState.NOT_NETWORK_CHECK);
                            break;
                    }
                } else {
                    boolean isCurrentMobileNetwork = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                    boolean isCurrentWifiNetwork = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    boolean isCurrentBluetooth = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH);
                    boolean isCurrentEthernet = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                    boolean isCurrentVPN = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
                    boolean isCurrentWifiAware = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE);
                    boolean isCurrentLowPan = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN);
                    boolean isCurrentUSB = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_USB);

                    if (isCurrentMobileNetwork) {
                        getInstance(mContext).postValue(NetworkState.MOBILE);
                        Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk大于23===当前在使用Mobile流量上网===");
                    } else if (isCurrentWifiNetwork) {
                        getInstance(mContext).postValue(NetworkState.WIFI);
                        Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk大于23====当前在使用WiFi上网===");
                    } else if (isCurrentBluetooth) {
                        Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk大于23=====当前使用蓝牙上网=====");
                    } else if (isCurrentEthernet) {
                        getInstance(mContext).postValue(NetworkState.ETHERNET);
                        Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk大于23=====当前使用以太网上网=====");
                    } else if (isCurrentVPN) {
                        Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk大于23===当前使用VPN上网====");
                    } else if (isCurrentWifiAware) {
                        Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk大于23===表示此网络使用Wi-Fi感知传输====");
                    } else if (isCurrentLowPan) {
                        Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk大于23=====表示此网络使用LowPan传输=====");
                    } else if (isCurrentUSB) {
                        Log.i("NetworkLiveData", "onCapabilitiesChanged ---> sdk大于23=====表示此网络使用USB传输=====");
                    }
                }
            } else {
                getInstance(mContext).postValue(NetworkState.NOT_NETWORK_CHECK);
                Log.i("NetworkLiveData", "onCapabilitiesChanged ---> 网络不可用，请检查网络！");
            }
        }
    }
}

