package zsdev.work.network.interceptor;

import android.util.Log;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created: by 2023-09-05 23:41
 * Description: 日志打印拦截器
 * Author: 张松
 */
public class PrintLogInterceptor implements InterceptorHandler {

    /**
     * 请求之前的拦截请求数据
     *
     * @param request 请求对象
     * @param chain   chain
     * @return 请求数据
     */
    @Override
    public Request onBeforeRequest(Request request, Interceptor.Chain chain) {
        //请求URL
        Log.i("PrintLogInterceptor", "url     =  : " + request.url());
        //请求方法
        Log.i("PrintLogInterceptor", "method  =  : " + request.method());
        //请求头
        Headers headers = request.headers();
        Log.i("PrintLogInterceptor", "headers =  : " + request.headers());
        if (headers.size() > 0) {
            Log.e("PrintLogInterceptor", "headers : " + headers.toString());
        }
        //请求体
        Log.i("PrintLogInterceptor", "request body    =  : " + request.body());
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            MediaType mediaType = requestBody.contentType();
            //判断ContentType类型
            if (mediaType != null) {
                if (isText(mediaType)) {
                    Log.d("PrintLogInterceptor", "params : " + bodyToString(request));
                } else {
                    Log.d("PrintLogInterceptor", "params : " + " maybe [file part] , too large too print , ignored!");
                }
            }
        }
        //开始请求
        return request;
    }

    /**
     * 请求之后+响应返回之前拦截响应数据
     *
     * @param response 响应对象
     * @param chain    chain
     * @return 响应数据
     */
    @Override
    public Response onAfterRequest(Response response, Interceptor.Chain chain) {
        //响应状态码
        Log.d("PrintLogInterceptor", "code     =  : " + response.code());
        //响应消息
        Log.d("PrintLogInterceptor", "message  =  : " + response.message());
        //响应HTTP协议版本
        Log.d("PrintLogInterceptor", "protocol =  : " + response.protocol());
        //响应体
        ResponseBody body = response.body();
        if (body != null && body.contentType() != null) {
            //有响应体解析MediaType和json数据
            MediaType mediaType = body.contentType();
            if (isText(mediaType)) {
                String string = null;
                try {
                    string = body.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //MediaType
                Log.d("PrintLogInterceptor", "mediaType =  :  " + Objects.requireNonNull(mediaType).toString());
                //响应JSON字符串
                Log.d("PrintLogInterceptor", "response string    =  : " + decode(string));
                //构建响应体对象
                body = ResponseBody.create(mediaType, Objects.requireNonNull(string));
                //装载响应体对象，开始响应
                return response.newBuilder().body(body).build();
            } else {
                Log.d("PrintLogInterceptor", "data : mediaType file content is too large, printing ignored!");
            }
        }
        //无响应体直接返回响应
        return response;
    }

    /**
     * 转换响应数据为字符串
     *
     * @param unicodeStr response.body().string()
     * @return 字符串
     */
    private String decode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuilder retBuf = new StringBuilder();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }

    /**
     * 校验contentType格式是否正确
     *
     * @param mediaType contentType
     * @return 匹配结果
     */
    private boolean isText(MediaType mediaType) {
        if (mediaType == null) return false;
        return ("text".equals(mediaType.subtype())
                || "json".equals(mediaType.subtype())
                || "xml".equals(mediaType.subtype())
                || "html".equals(mediaType.subtype())
                || "webviewhtml".equals(mediaType.subtype())
                || "x-www-form-urlencoded".equals(mediaType.subtype()));
    }

    /**
     * 请求体数据转换成字符串
     *
     * @param request 请求对象
     * @return 字符串
     */
    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            Objects.requireNonNull(copy.body()).writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}

