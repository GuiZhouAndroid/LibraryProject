package zsdev.work.network.interceptor;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created: by 2023-09-05 23:13
 * Description: 构建Header拦截器
 * Author: 张松
 */
public class HeadersInterceptor implements InterceptorHandler {

    /**
     * 请求头map集合
     */
    private final Map<String, String> headersMap;

    /**
     * 接收传递来赋值给本类的全局变量map集合
     *
     * @param headersMap 请求头Map集合
     */
    public HeadersInterceptor(Map<String, String> headersMap) {
        //等量赋值
        this.headersMap = headersMap;
    }

    /**
     * 请求之前的拦截请求数据：map集合键值对来构建，将headers加入到Request中
     *
     * @param request 请求对象
     * @param chain   chain
     * @return 请求数据
     */
    @Override
    public Request onBeforeRequest(Request request, Interceptor.Chain chain) {
        Request.Builder builder = chain.request().newBuilder();
        //集合判空
        if (headersMap != null && headersMap.size() > 0) {
            //遍历取全部key
            Set<String> keys = headersMap.keySet();
            //再通过遍历key，对应获取value
            for (String headerKey : keys) {
                //map集合键值将headers加入到Request中构建
                builder.addHeader(headerKey, Objects.requireNonNull(headersMap.get(headerKey))).build();
            }
        }
        //构造request对象发送请求
        return builder.build();
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
        //请求头拦截器只关心添加是否成功，不用关心Response结果，因此不对Response结果进行数据操作，直接返回即可
        return response;
    }
}

