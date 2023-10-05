package zsdev.work.lib.common.utils.network.newnet;

import androidx.annotation.NonNull;

/**
 * Created: by 2023-09-25 12:57
 * Description: 网络连接状态的枚举
 * Author: 张松
 */
public enum NetworkState {
    NOT_NETWORK_CHECK(-1, "网络不可用，请检查网络！"),
    NETWORK_CONNECT_Fail(-2, "网络连接超时，请稍候重试！"),
    WIFI(1, "正在使用无线WIFI，不消耗数据流量哟~"),
    MOBILE(2, "正在使用移动数据流量，请注意流量额度~"),
    ETHERNET(3, "正在使用以太网~");

    private final int statusCode;
    private final String desc;

    NetworkState(int code, String msg) {
        this.statusCode = code;
        this.desc = msg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDesc() {
        return desc;
    }

    @NonNull
    @Override
    public String toString() {
        return "NetworkState{" +
                "statusCode=" + statusCode +
                ", desc='" + desc + '\'' +
                '}';
    }
}
