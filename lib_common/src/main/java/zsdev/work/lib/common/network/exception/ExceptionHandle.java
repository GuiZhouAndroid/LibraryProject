package zsdev.work.lib.common.network.exception;

import android.util.Log;

import retrofit2.adapter.rxjava3.HttpException;


/**
 * Created: by 2023-09-08 00:22
 * Description: 异常处理类
 * Author: 张松
 */
public class ExceptionHandle {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex = null;
        Log.i("ExceptionHandle", "handleException：" + e.toString());
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;

            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                    ex = new ResponseThrowable(e, httpException.code(), "网络错误");
                    break;

            }
        }
        return ex;
//        if (e instanceof ServerException) {
//            ServerException resultException = (ServerException) e;
//            ex = new ResponseThrowable(resultException, resultException.code);
//            ex.msg = resultException.msg;
//            return ex;
//        }
//        if (e instanceof UnknownHostException) {
//            ex = new ResponseThrowable(e, NetworkError.NETWORK_ERROR);
//            ex.msg = "访问服务器失败，请检查网络是否连接正常！";
//            return ex;
//        }
//        if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
//            ex = new ResponseThrowable(e, NetworkError.PARSE_ERROR);
//            ex.msg = "解析错误";
//            return ex;
//        }
//        if (e instanceof ConnectException) {
//            ex = new ResponseThrowable(e, NetworkError.NETWORK_ERROR);
//            ex.msg = "连接失败！";
//            return ex;
//        }
//        if (e instanceof SSLHandshakeException) {
//            ex = new ResponseThrowable(e, NetworkError.SSL_ERROR);
//            ex.msg = "证书验证失败";
//            return ex;
//        }
//        if (e instanceof RuntimeException) {
//            ex = new ResponseThrowable(e, NetworkError.SSL_QWE);
//            ex.msg = "系统";
//            return ex;
//        }
//        ex = new ResponseThrowable(e, NetworkError.UNKNOWN);
//        ex.msg = "未知错误";
//        return ex;

    }
}