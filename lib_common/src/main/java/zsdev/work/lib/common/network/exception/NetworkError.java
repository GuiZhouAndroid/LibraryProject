package zsdev.work.lib.common.network.exception;

/**
 * Created: by 2023-09-09 01:23
 * Description: 约定网络异常
 * Author: 张松
 */
public class NetworkError {

    /**
     * 未知错误
     */
    public static final int UNKNOWN = 1000;

    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = 1009;

    /**
     * 网络错误
     */
    public static final int NETWORK_ERROR = 1002;

    /**
     * 协议出错
     */
    public static final int HTTP_ERROR = 1003;


    /**
     * 证书出错
     */
    public static final int SSL_ERROR = 1005;
    public static final int SSL_QWE = 1001;
}
