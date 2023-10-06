package zsdev.work.network.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.adapter.rxjava3.HttpException;


/**
 * Created: by 2023-09-08 00:22
 * Description: 异常处理类
 * Author: 张松
 */
public class ExceptionHandle {

    /**
     * 当前请求无法被服务器理解。请求和服务器对接不上。发送请求时出现的问题。
     * 表示报文里存在直接的语法错误，不能处理
     */
    private static final int BAD_REQUEST = 400;

    /**
     * （未授权的、非法的）表示发送的请求需要有通过 HTTP 认证的认证信息
     * 当客户发送请求，服务端返回 401，表明这个资源需要 HTTP认证，需要带上Authorizationcredentials以后再发送。
     * 当第二次发送请求仍然是401，则表示认证失败。返回含有 401 的响应必须包含一个适用于被请求资源的 WWW-Authenticate 首部用以质询（challenge）用户信息。
     * 当浏览器初次接收到 401 响应，会弹出认证用的对话窗口。
     */
    private static final int UNAUTHORIZED = 401;

    /**
     * 服务器理解请求客户端的请求，但是拒绝执行此请求
     * （禁止） 服务器拒绝请求。服务器不接受请求。（可能没有权限）
     * 表明请求资源的访问被拒绝了，服务器不允许该客户端访问这个资源。
     */
    private static final int FORBIDDEN = 403;

    /**
     * 服务器无法根据客户端的请求找到资源
     * 找不到页面，请求访问的URL路径出错。
     * 表明服务器说“无法找到这个资源”
     */
    private static final int NOT_FOUND = 404;

    /**
     * 请求超时
     */
    private static final int REQUEST_TIMEOUT = 408;

    /**
     * （服务器内部错误） 服务器遇到错误，无法完成请求。服务器中的错误即请求成功后服务器运行出现错误。java代码写的有问题。
     * 表明服务器端在执行请求时内部发生了一些错误，可能是 Web应用存在的 bug 或某些临时的故障。
     */
    private static final int INTERNAL_SERVER_ERROR = 500;

    /**
     * （错误网关） 服务器作为网关或代理，从上游服务器收到无效响应。
     * 往往是由于服务器同时响应太多请求，导致访问超时。
     */
    private static final int BAD_GATEWAY = 502;

    /**
     * （服务不可用） 服务器目前无法使用（由于超载或停机维护）。通常，这只是暂时状态。
     * 表明服务器本身正忙（超负载或者维护），忙而无法处理请求。
     */
    private static final int SERVICE_UNAVAILABLE = 503;

    /**
     * （网关超时）服务器作为网关或代理，但是没有及时从上游服务器收到请求。
     */
    private static final int GATEWAY_TIMEOUT = 504;

    /**
     * HTTP版本不支持
     */
    private static final int HTTP_VERSION_NOT_SUPPORTED = 505;

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, NetworkError.HTTP_ERROR);
            switch (httpException.code()) { //HTTP错误
                case BAD_REQUEST:
                    ex.setMsg("请求错误！");
                    break;
                case UNAUTHORIZED:
                    ex.setMsg("未验证！");
                    break;
                case FORBIDDEN:
                    ex.setMsg("服务禁止访问！");
                    break;
                case NOT_FOUND:
                    ex.setMsg("未找到请求资源，请检查访问路径！");
                    break;
                case REQUEST_TIMEOUT:
                    ex.setMsg("请求超时！");
                    break;
                case GATEWAY_TIMEOUT:
                    ex.setMsg("网关超时！");
                    break;
                case INTERNAL_SERVER_ERROR:
                    ex.setMsg("服务器内部错误！");
                    break;
                case BAD_GATEWAY:
                    ex.setMsg("网关错误！");
                    break;
                case SERVICE_UNAVAILABLE:
                    ex.setMsg("服务器正在维护中，请您留意官方通知！");
                    break;
                case HTTP_VERSION_NOT_SUPPORTED:
                    ex.setMsg("HTTP版本不支持！");
                    break;
                default: //其他HTTP错误，都提示网络错误
                    ex.setMsg("请求失败，请稍候重试！");
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {//服务器返回的错误
            ServerException resultException = (ServerException) e;
            ex = new ResponseThrowable(resultException, resultException.getCode());
            ex.setMsg(resultException.getMsg());
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ResponseThrowable(e, NetworkError.PARSE_ERROR);
            ex.setMsg("数据解析错误！");
            return ex;
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof SocketException) {
            ex = new ResponseThrowable(e, NetworkError.NETWORK_ERROR);
            ex.setMsg("网络连接超时，请检查您的网络状态！");
            return ex;
        } else if (e instanceof SSLHandshakeException) {
            ex = new ResponseThrowable(e, NetworkError.SSL_ERROR);
            ex.setMsg("证书验证失败！");
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new ResponseThrowable(e, NetworkError.HTTP_ERROR);
            ex.setMsg("网络不可用，请检查网络！");
            return ex;
        } else if (e instanceof NumberFormatException || e instanceof IllegalArgumentException || e instanceof JsonSyntaxException) {
            //也就是后台返回的数据，与你本地定义的Gson类，不一致，导致解析异常 (ps:当然这不能跟客户这么说)
            ex = new ResponseThrowable(e, NetworkError.HTTP_ERROR);
            ex.setMsg("请求数据失败！攻城狮正在修复...");
            return ex;
        } else {
            ex = new ResponseThrowable(e, NetworkError.UNKNOWN);
            ex.setMsg("sorry 故障啦！，攻城狮正在修复...");
            return ex;
        }
    }
}