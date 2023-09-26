package zsdev.work.network;

import java.util.Map;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import retrofit2.Converter;
import zsdev.work.network.enums.ConverterMode;
import zsdev.work.network.enums.CookieStoreMode;
import zsdev.work.network.interceptor.InterceptorHandler;

/**
 * Created: by 2023-08-11 14:06
 * Description: retrofit网络配置等参数外部接口
 * Author: 张松
 */
public interface INetworkConfig {

    /**
     * 创建OkhttpClient拦截器数组，Retrofit依据配置调用addInterceptor()全部添加
     *
     * @return 拦截器数组
     */
    Interceptor[] setInterceptors();

    /**
     * 请求连接超时时间
     *
     * @return 时间参数/s
     */
    long setConnectTimeoutMills();

    /**
     * 数据读取超时时间
     *
     * @return 时间参数/s
     */
    long setReadTimeoutMills();

    /**
     * 写入数据超时时间
     *
     * @return 时间参数/s
     */
    long setWriteTimeoutMills();

    /**
     * 调试模式：是否默认开启Okhttp网络请求打印日志拦截器，默认禁用
     *
     * @return ture启用打印 false禁用打印
     */
    boolean setIsEnableOkpDefaultPrintLog();

    /**
     * 是否开启缓存，默认禁用
     *
     * @return ture启用缓存 false禁用
     */
    boolean setIsEnableCache();

    /**
     * 缓存有效时间，过期就向服务器重新请求
     *
     * @return 时间参数/s
     */
    int setCacheMaxAgeTimeUnitSeconds();

    /**
     * 缓存过期后，陈旧秒数不超过max-stale仍然可以使用缓存，如果max-stale后面没有值，无论过期多久都可以使用缓存
     *
     * @return 时间参数/s
     */
    int setCacheMaxStaleTimeUnitSeconds();

    /**
     * 设置是否启用请求错误后重试，默认禁用
     *
     * @return ture启用重试 false禁用重试
     */
    boolean setIsEnableRetryOnConnection();

    /**
     * 设置公共参数拦截器
     *
     * @return 参数Map集合
     */
    Map<String, String> setUrlParameter();

    /**
     * 设置请求体参数拦截器
     *
     * @return 参数Map集合
     */
    Map<String, String> setHeaderParameters();

    /**
     * 设置定制Cookie持久化配置，如SQL存取等特殊需求，需要实现CookieStore接口完成定制
     *
     * @return cookieJar
     */
    CookieJar setCustomCookieStore();

    /**
     * 设置定制拦截器
     *
     * @return InterceptorHandler实现类数组
     */
    InterceptorHandler[] setCustomInterceptor();

    /**
     * 设置转换器模式
     *
     * @return 转换器模式
     */
    ConverterMode setConverterFactoryMode();

    /**
     * 设置Cookie存取模式
     *
     * @return Cookie存取模式
     */
    CookieStoreMode setCookieStoreMode();

    /**
     * 设置定制实体转换器
     *
     * @return Converter.Factory
     */
    Converter.Factory setCustomConverterFactory();
}
