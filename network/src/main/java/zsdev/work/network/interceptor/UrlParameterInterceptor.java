package zsdev.work.network.interceptor;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created: by 2023-09-05 23:52
 * Description: URL公共参数拦截器。通过在URL网址后追加的公共请求参数。另外一种方式是通过请求头方式添加
 * Author: 张松
 */
public class UrlParameterInterceptor implements InterceptorHandler {

    /**
     * URL公共参数map集合
     */
    private final Map<String, String> UrlParameterMap;

    /**
     * 接收传递来赋值给本类的全局变量map集合
     *
     * @param urlParameterMap 公共参数map集合
     */
    public UrlParameterInterceptor(Map<String, String> urlParameterMap) {
        this.UrlParameterMap = urlParameterMap;
    }

    /**
     * 请求之前的拦截请求数据：map集合键值对来构建，将URL加入到HttpUrl中
     *
     * @param request 请求对象
     * @param chain   chain
     * @return 请求数据
     */
    @Override
    public Request onBeforeRequest(Request request, Interceptor.Chain chain) {
        HttpUrl.Builder builder = request.url().newBuilder();
        //集合判空
        if (UrlParameterMap != null && UrlParameterMap.size() > 0) {
            //遍历取全部key
            Set<String> keys = UrlParameterMap.keySet();
            //再通过遍历key，对应获取value
            for (String headerKey : keys) {
                //map集合键值将URL参数加入到HttpUrl构建
                builder.addQueryParameter(headerKey, Objects.requireNonNull(UrlParameterMap.get(headerKey))).build();
            }
        }
        //含有新增URL请求头request对象装载重组成新的request对象后开始请求
        return request.newBuilder().url(builder.build()).build();
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

