package zsdev.work.utils.network.newnet;

import androidx.annotation.NonNull;

/**
 * Created: by 2023-09-25 12:57
 * Description: 网络连接状态的枚举
 * Author: 张松
 */
public enum NetworkState {
    NONE(0, "当前无网络连接"),
    CONNECT(1, "当前网络连接正常"),
    WIFI(2, "当前正在使用无线WIFI"),
    CELLULAR(3, "当前正在使用移动数据流量"),
    ETHERNET(4, "当前正在使用以太网");

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
