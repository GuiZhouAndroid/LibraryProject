package zsdev.work.network.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created: by 2023-09-05 22:48
 * Description: 自定义拦截器实现类
 * Author: 张松
 */
public class InterceptorImpl implements Interceptor {

    /**
     * 自定义拦截接口
     */
    InterceptorHandler interceptorHandler;

    /**
     * 接收实现类
     *
     * @param interceptorHandler 接口实现类
     */
    public InterceptorImpl(InterceptorHandler interceptorHandler) {
        this.interceptorHandler = interceptorHandler;
    }

    /**
     * 在请求与响应中间将数据使用接口装载传递，向外部提供Request、Response可调用方式，进行统一管理
     *
     * @param chain chain
     * @return 响应结果
     * @throws IOException 异常
     */
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        //获取请求对象
        Request request = chain.request();
        if (interceptorHandler != null) {
            //将本次Request对象数据装载
            request = interceptorHandler.onBeforeRequest(request, chain);
        }
        //Request对象传入获取Response对象
        Response response = chain.proceed(request);
        if (interceptorHandler != null) {
            Response tempResponse = interceptorHandler.onAfterRequest(response, chain);
            if (tempResponse != null) {
                return tempResponse;
            }
        }
        return response;
    }
}
