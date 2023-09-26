package zsdev.work.utils.network.newnet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

/**
 * Created: by 2023-09-25 13:04
 * Description: Android 23 6.0及以上 观察网络状态变化
 * Author: 张松
 */

public class NetworkLiveDataMAfter extends LiveData<NetworkState> {
    private static Context mContext;
    private static NetworkLiveDataMAfter myNetworkLiveData;
    private final ConnectivityManager manager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NetworkRequest networkRequest;

    public NetworkLiveDataMAfter(Context context) {
        mContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new NetworkCallbackImpl();
            networkRequest = new NetworkRequest.Builder().build();
        }
        manager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 创建单例
     *
     * @return MyNetworkLiveData 单例
     */
    public static NetworkLiveDataMAfter getInstance(Context context) {
        if (myNetworkLiveData == null) {
            Log.i("NetworkLiveDataMAfter", "getInstance：sdk大于23");
            myNetworkLiveData = new NetworkLiveDataMAfter(context);
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
        manager.registerNetworkCallback(networkRequest, networkCallback);
        Log.i("NetworkLiveDataMAfter", "onInactive：注册监听网络变化的广播");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.unregisterNetworkCallback(networkCallback);
        }
        Log.i("NetworkLiveDataMAfter", "onInactive：注销广播");
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
            Log.i("NetworkLiveDataMAfter: ", "onAvailable：sdk大于23==网络连接成功，通知可以使用的时候调用====onAvailable===");
            getInstance(mContext).postValue(NetworkState.CONNECT);
        }

        /**
         * 当网络连接超时或网络请求达不到可用要求时调用
         */
        @Override
        public void onUnavailable() {
            Log.i("NetworkLiveDataMAfter", "onUnavailable：sdk大于23==当网络连接超时或网络请求达不到可用要求时调用====onUnavailable===");
            super.onUnavailable();
        }

        /**
         * 当访问指定的网络被阻止或解除阻塞时调用
         *
         * @param network 当前活动的网络对象信息，网络切换后会变更为新的 Network 对象
         * @param blocked 是否解除阻塞
         */
        @Override
        public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
            Log.i("NetworkLiveDataMAfter", "onBlockedStatusChanged：sdk大于23==当访问指定的网络被阻止或解除阻塞时调用===onBlockedStatusChanged==");
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
            Log.i("NetworkLiveDataMAfter", "onLosing：sdk大于23==当网络正在断开连接时调用===onLosing===");
            super.onLosing(network, maxMsToLive);
        }

        /**
         * 当网络已断开连接时调用
         *
         * @param network 当前活动的网络对象信息，网络切换后会变更为新的 Network 对象
         */
        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            getInstance(mContext).postValue(NetworkState.NONE);
            Log.i("NetworkLiveDataMAfter：", "onLost：sdk大于23 当网络已断开连接时调用===onLost===");
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
            boolean isCurrentMobileNetwork = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            boolean isCurrentWifiNetwork = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            boolean isCurrentEthernet = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            boolean isValidated = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
            if (isValidated && isCurrentWifiNetwork) {
                Log.i("NetworkLiveDataMAfter", "onCapabilitiesChanged：sdk大于23 wifi>>>>>>>>");
                getInstance(mContext).postValue(NetworkState.WIFI);
                return;
            }
            if (isValidated && isCurrentMobileNetwork) {
                Log.i("NetworkLiveDataMAfter", "onCapabilitiesChanged：sdk大于23 蜂窝网络>>>>>>>>");
                getInstance(mContext).postValue(NetworkState.CELLULAR);
                return;
            }
            if (isValidated && isCurrentEthernet) {
                Log.i("NetworkLiveDataMAfter", "onCapabilitiesChanged：sdk大于23 以太网>>>>>>>>");
                getInstance(mContext).postValue(NetworkState.ETHERNET);
                return;
            }
        }
    }
}

