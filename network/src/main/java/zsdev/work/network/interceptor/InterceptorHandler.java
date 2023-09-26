package zsdev.work.network.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created: by 2023-09-05 22:42
 * Description: 拦截器请求与响应的业务接口
 * Author: 张松
 */
public interface InterceptorHandler {

    /**
     * 请求之前的拦截请求数据
     *
     * @param request 请求对象
     * @param chain   chain
     * @return 请求数据
     */
    Request onBeforeRequest(Request request, Interceptor.Chain chain);

    /**
     * 请求之后+响应返回之前拦截响应数据
     *
     * @param response 响应对象
     * @param chain    chain
     * @return 响应数据
     */
    Response onAfterRequest(Response response, Interceptor.Chain chain);
}
