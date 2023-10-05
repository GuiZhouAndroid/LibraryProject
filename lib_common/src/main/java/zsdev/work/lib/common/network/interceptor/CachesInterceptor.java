package zsdev.work.lib.common.network.interceptor;

import android.content.Context;
import android.util.Log;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import zsdev.work.lib.common.utils.network.NetworkUtil;


/**
 * Created: by 2023-09-01 11:56
 * Description: 缓存策略拦截器
 * Author: 张松
 */
public class CachesInterceptor implements InterceptorHandler {

    private final Context context;

    private final int maxAge;
    private final int maxStale;

    public CachesInterceptor(Context context, int maxAge, int maxStale) {
        this.context = context;
        this.maxAge = maxAge;
        this.maxStale = maxStale;
    }

    /**
     * 请求之前的拦截请求数据
     * 无网络时增加处理缓存功能
     *
     * @param request 请求对象
     * @param chain   chain
     * @return 请求数据
     */
    @Override
    public Request onBeforeRequest(Request request, Interceptor.Chain chain) {
        Request.Builder builder = request.newBuilder();
        // 网络不可用
        if (!NetworkUtil.isAvailable(context)) {
            builder.cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
                    .build();
            Log.i("CacheInterceptor", "网络不可用，从缓存中读取数据。 ");
        }
        if (!NetworkUtil.isAvailable(context)) {
            builder.removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale) //从缓存读取maxAge秒
                    .build();
            Log.i("CacheInterceptor", "网络不可用，缓存过期后 ");
        } else {
            //网络可用
            builder.removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
            Log.i("CacheInterceptor", "网络可用，有效时间内读取缓存 ");
        }
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
        return response;
    }
}
