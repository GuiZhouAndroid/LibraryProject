package zsdev.work.lib.common.utils.network.newnet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;

/**
 * Created: by 2023-09-25 13:04
 * Description: Android 23 6.0以下 观察网络状态变化
 * Author: 张松
 */
public class NetworkLiveDataMBefore extends LiveData<NetworkState> {

    private static Context mContext;
    static NetworkLiveDataMBefore mNetworkLiveData;
    private final NetworkReceiver mNetworkReceiver;
    private final IntentFilter mIntentFilter;

    public NetworkLiveDataMBefore(Context context) {
        mContext = context.getApplicationContext();
        mNetworkReceiver = new NetworkReceiver();
        mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    public static NetworkLiveDataMBefore getInstance(Context context) {
        if (mNetworkLiveData == null) {
            Log.i("NetworkLiveDataMBefore", "getInstance：sdk小于23");
            mNetworkLiveData = new NetworkLiveDataMBefore(context);
        }
        return mNetworkLiveData;
    }

    /**
     * 当活动观察者的数量从0更改为1时调用。
     * 这个回调可以用来知道这个LiveData正在被使用，因此应该保持最新。
     * <p>
     * 注册监听网络变化的广播，即ConnectivityManager.CONNECTIVITY_ACTION
     */
    @Override
    protected void onActive() {
        super.onActive();
        Log.i("NetworkLiveDataMBefore", "onActive：注册监听网络变化的广播");
        mContext.registerReceiver(mNetworkReceiver, mIntentFilter);
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
        Log.i("NetworkLiveDataMBefore", "onInactive：注销网络监听广播");
        mContext.unregisterReceiver(mNetworkReceiver);
    }

    private static class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取网络连接管理器对象
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取正在使用的网络信息
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            //网络连接成功执行
            if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_MOBILE:
                        getInstance(context).postValue(NetworkState.MOBILE);
                        Log.i("NetworkLiveDataMBefore", "onReceive：sdk小于23 蜂窝网络>>>>>>>>");
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        getInstance(context).postValue(NetworkState.WIFI);
                        Log.i("NetworkLiveDataMBefore", "onReceive：sdk小于23 wifi>>>>>>>>");
                        break;
                    default:
                        getInstance(context).postValue(NetworkState.NOT_NETWORK_CHECK);
                        break;
                }
            } else {
                getInstance(context).postValue(NetworkState.NOT_NETWORK_CHECK);
                Log.i("NetworkLiveDataMBefore", "onReceive：sdk小于23 网络断开>>>>>>>>");
            }
        }
    }
}

