package zsdev.work.libraryproject;

import java.util.Map;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import retrofit2.Converter;

import zsdev.work.network.INetworkConfig;
import zsdev.work.network.enums.ConverterMode;
import zsdev.work.network.enums.CookieStoreMode;
import zsdev.work.network.interceptor.InterceptorHandler;

/**
 * Created: by 2023-09-01 18:42
 * Description: 网络参数配置接口实现类
 * Author: 张松
 */
public class NetWorkConfig implements INetworkConfig {

    @Override
    public CookieStoreMode setCookieStoreMode() {
        return CookieStoreMode.SP;
    }

    @Override
    public Converter.Factory setCustomConverterFactory() {
        return null;
    }

    @Override
    public Interceptor[] setInterceptors() {
        return new Interceptor[0];
    }

    @Override
    public long setConnectTimeoutMills() {
        return 0;
    }

    @Override
    public long setReadTimeoutMills() {
        return 0;
    }

    @Override
    public long setWriteTimeoutMills() {
        return 0;
    }

    @Override
    public boolean setIsEnableOkpDefaultPrintLog() {
        return true;
    }

    @Override
    public boolean setIsEnableCache() {
        return false;
    }

    @Override
    public int setCacheMaxAgeTimeUnitSeconds() {
        return 0;
    }

    @Override
    public int setCacheMaxStaleTimeUnitSeconds() {
        return 0;
    }

    @Override
    public boolean setIsEnableRetryOnConnection() {
        return false;
    }

    @Override
    public Map<String, String> setUrlParameter() {
        return null;
    }

    @Override
    public Map<String, String> setHeaderParameters() {
        return null;
    }

    @Override
    public CookieJar setCustomCookieStore() {
        return null;
    }

    @Override
    public InterceptorHandler[] setCustomInterceptor() {
        return new InterceptorHandler[0];
    }

    @Override
    public ConverterMode setConverterFactoryMode() {
        return ConverterMode.MOSHI;
    }
}
