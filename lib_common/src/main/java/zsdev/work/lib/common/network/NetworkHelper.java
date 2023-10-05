package zsdev.work.lib.common.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.converter.guava.GuavaOptionalConverterFactory;
import retrofit.converter.java8.Java8OptionalConverterFactory;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.jaxb.JaxbConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.protobuf.ProtoConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.converter.wire.WireConverterFactory;
import zsdev.work.lib.common.network.cookie.CookieJarImpl;
import zsdev.work.lib.common.network.cookie.DBCookieStore;
import zsdev.work.lib.common.network.cookie.MemoryCookieStore;
import zsdev.work.lib.common.network.cookie.SpCookieStore;
import zsdev.work.lib.common.network.interceptor.CachesInterceptor;
import zsdev.work.lib.common.network.interceptor.HeadersInterceptor;
import zsdev.work.lib.common.network.interceptor.InterceptorHandler;
import zsdev.work.lib.common.network.interceptor.InterceptorImpl;
import zsdev.work.lib.common.network.interceptor.UrlParameterInterceptor;


/**
 * Created: by 2023-09-06 00:11
 * Description: 网络配置帮助类（retrofit2 + okhttp3 + rxjava2 + Gson 等封装帮助类）
 * 使用rxjava转换器处理线程调度的帮助类.每一个Presenter中使用到IO子线程与Main主线程切换，每次都需要重新写就比较繁琐，而RxJava中根据APIService返回值Observable或者Flowable类型
 * 自动从匹配相应抽象类中通过compose()完成线程合并自动切换。
 * 返回值类型为Observable<T>时：Observable.compose("使用RxSchedulerHelper类型返回值对应的getObservableScheduler()作为参数")
 * 返回值类型为Flowable<T>时：Flowable.compose("使用RxSchedulerHelper类型返回值对应的getFlowableScheduler()作为参数")
 * Scheduler是RxJava的线程调度器，可以指定代码执行的线程。RxJava内置了几种线程：
 * AndroidSchedulers.mainThread() 主线程
 * Schedulers.immediate() 当前线程，即默认Scheduler
 * Schedulers.newThread() 启用新线程
 * Schedulers.io() IO线程，内部是一个数量无上限的线程池，可以进行文件、数据库和网络操作。
 * Schedulers.computation() CPU计算用的线程，内部是一个数目固定为CPU核数的线程池，适合于CPU密集型计算，不能操作文件、数据库和网络。
 * 一般的网络请求应该使用io,因为io使用了无限的线程池，而newThread没有线程池维护
 * onNext里我们的函数发生异常时，onError会被调用
 * subscribeOn(Schedulers.io()) 指定了 Observable 在 IO 线程中创建和发射数据，而 observeOn(Schedulers.computation()) 指定了订阅者在计算线程中接收数据。
 * subscribeOn(), observeOn() 进行线程调度
 * compose()进行组合操作
 * compose()：用于组合多个操作符，创建一个自定义的操作符。通过使用 compose()，你可以将一系列操作符封装为一个可重复使用的操作符，以便在多个 Observable 上进行复用。
 * observeOn(AndroidSchedulers.mainThread()) 指定了订阅者在主线程中接收数据。compose()将自定义的操作符应用于 Observable
 * subscribeOn() 和 observeOn() 是 RxJava 中的两个关键操作符，用于控制 Observable 的执行线程和订阅者的接收线程。
 * subscribeOn() 操作符用于指定 Observable 的执行线程。它会影响整个链式操作符中的执行线程。
 * 当使用 subscribeOn() 时，Observable 的创建和订阅操作将在指定的线程中执行。这意味着 Observable 的数据流将在指定的线程中发射，并在该线程中执行所有操作符。
 * 如果多次使用 subscribeOn()，只有第一个 subscribeOn() 起作用，后续的将被忽略。
 * observeOn() 操作符用于指定订阅者接收数据的线程。它可以在链式操作符中的任何位置使用，以改变后续操作符和订阅者的执行线程。
 * 当使用 observeOn() 时，它会影响 observeOn() 之后的操作符和订阅者的执行线程。
 * 总结一下，subscribeOn() 用于指定 Observable 的执行线程，observeOn() 用于指定订阅者接收数据的线程。这两个操作符可以一起使用，以实现不同的线程切换需求。
 * 通过使用这些操作符，你可以灵活地控制 Observable 的执行线程和订阅者的接收线程，并实现自定义的操作符组合。
 * Author: 张松
 */
public class NetworkHelper {

    /* ********************************** 共用 *************************************/

    /**
     * 单例封装帮助类全局变量
     **/
    public static volatile NetworkHelper networkHelperInstance;

    /**
     * retrofit网络配置等参数外部接口
     */
    private static INetworkConfig iNetWorkGlobalConfig = null;

    /**
     * 获取单个网络配置的Map集合
     *
     * @return INetWorkConfig对象集合
     */
    public Map<String, INetworkConfig> getNetWorkSingleConfigMap() {
        return iNetWorkSingleConfigMap;
    }

    /**
     * 清除网络配置缓存的Map数据
     */
    public static void clearConfigCache() {
        getInstance().getRetrofitMap().clear();
        getInstance().getNetWorkSingleConfigMap().clear();
        getInstance().getOkhttpClientMap().clear();
    }

    /* ********************************** Retrofit *************************************/

    /**
     * Retrofit全局变量
     */
    private Retrofit mRetrofit;

    /**
     * 单个网络配置的Map集合，为了兼容多服务器URL
     */
    private final Map<String, INetworkConfig> iNetWorkSingleConfigMap = new HashMap<>();

    /**
     * Retrofit的Map集合，为了兼容多服务器URL
     */
    private final Map<String, Retrofit> retrofitMap = new HashMap<>();

    /* ********************************** Okhttp *************************************/

    /**
     * 声明OkHttpClient全部变量
     */
    private OkHttpClient mClient;

    /**
     * OkHttpClient的Map集合，为了兼容多服务器URL
     */
    private final Map<String, OkHttpClient> okhttpClientMap = new HashMap<>();

    /**
     * 默认请求连接超时时间
     */
    private static final long DEFAULT_CONNECT_TIMEOUT_MILLS = 40 * 1000L;

    /**
     * 默认数据读取超时时间
     */
    private static final long DEFAULT_READ_TIMEOUT_MILLS = 40 * 1000L;

    /**
     * 默认数据写入超时时间
     */
    private static final long DEFAULT_WRITE_TIMEOUT_MILLS = 40 * 1000L;

    /**
     * 缓存有效时间，过期就向服务器重新请求
     */
    private static final int DEFAULT_MAX_AGE = 60 * 60;

    /**
     * 缓存过期后，陈旧秒数不超过max-stale仍然可以使用缓存，如果max-stale后面没有值，无论过期多久都可以使用缓存
     */
    private static final int DEFAULT_MAX_STALE = 60 * 60 * 24;

    /**
     * 无参构造
     */
    private NetworkHelper() {
        mRetrofit = null;
        mClient = null;
    }

    /**
     * 判空创建网络配置帮助类单例
     *
     * @return NetworkHelper单例对象
     */
    public static NetworkHelper getInstance() {
        if (networkHelperInstance == null) {//考虑效率问题
            synchronized (NetworkHelper.class) {
                if (networkHelperInstance == null) {//考虑多个线程问题
                    networkHelperInstance = new NetworkHelper();
                }
            }
        }
        return networkHelperInstance;
    }

    /* ********************************** 注册网络配置与校验注册：全局配置、单次配置 *************************************/

    /**
     * 注册Okhttp网络全局配置，接口传递的数据直接使用
     * 全局配置建议使用在Application中注册
     *
     * @param globalConfig INetWorkConfig全局配置
     */
    public static void registerNetWorkGlobalConfig(INetworkConfig globalConfig) {
        NetworkHelper.iNetWorkGlobalConfig = globalConfig;
    }

    /**
     * 注册网络单次配置，采用Map集合存取配置对象，实现多URL情况
     * 当网络单次配置没有注册时，会调用全局配置。
     * 单次配置建议使用在Service中注册（Service中实现INetworkConfig接口完成单次配置）
     *
     * @param singleConfig INetWorkConfig配置
     */
    public static void registerNetWorkSingleConfig(String baseUrl, INetworkConfig singleConfig) {
        NetworkHelper.getInstance().getNetWorkSingleConfigMap().put(baseUrl, singleConfig);
    }

    /**
     * 获取全局网络配置
     *
     * @return INetWorkConfig
     */
    public static INetworkConfig getNetWorkGlobalConfig() {
        return NetworkHelper.iNetWorkGlobalConfig;
    }

    /**
     * 检查是否注册网络配置项
     *
     * @param baseUrl 服务器URL
     * @param config  网络配置
     */
    public void checkNetWorkConfig(String baseUrl, INetworkConfig config) {
        if (config == null) {
            throw new IllegalStateException(baseUrl + "：this url Must implement the INetWorkConfig interface to register the network configuration item for Retrofit!");
        }
    }

    /* ********************************** Retrofit配置 *************************************/

    /**
     * 获取Retrofit实例：配置BaseUrl、配置client、配置转换器
     * 网络配置引用优先级：netWorkConfig（直接注册） > iNetWorkSingleConfigMap（单次注册） > iNetWorkGlobalConfig（全局注册）
     * 若使用registerNetWorkGlobalConfig()注册，则调用getNetWorkGlobalConfig()获取全局网络配置
     *
     * @param context                  上下文
     * @param baseUrl                  服务器URL
     * @param isEnableConverterFactory 是否启用实体转换器
     * @param isEnableRxJava           是否启用RxJava线程调度适配器
     * @param isEnableCookieStore      是否启用Cookie存取模式
     * @param netWorkConfig            网络配置
     * @return Retrofit对象
     */
    public Retrofit getRetrofit(Context context, String baseUrl, boolean isEnableConverterFactory, boolean isEnableRxJava, boolean isEnableCookieStore, INetworkConfig netWorkConfig) {
        //服务器URL为空时程序关闭打印异常
        if (TextUtils.isEmpty(baseUrl)) throw new IllegalStateException("baseUrl can not be null!");

        //判断本次请求的服务器URL在Map集合中是否已注册有网络配置，如果已注册就将此含有配置属性的Retrofit对象返回使用
        if (getRetrofitMap().get(baseUrl) != null) {
            Log.i("NetworkHelper", "getRetrofit: " + baseUrl + "——>Retrofit已存在创建，正在引用中..");
            return getRetrofitMap().get(baseUrl);
        } else {
            Log.i("NetworkHelper", "getRetrofit: " + baseUrl + "——>Retrofit未创建");
        }

        //此处是使用全局网络配置、单个网络配置的关键之处
        if (netWorkConfig == null) {   //netWorkConfig == null 意味着调用getRetrofit()时未使用参数传递注册

            Log.i("NetworkHelper", "getRetrofit: " + baseUrl + "当前未使用参数传递注册网络配置");

            //先根据请求的服务器URL在Map集合中查是否已存在注册网络配置，如果已注册网络配置直接赋值给netWorkConfig变量提供后续使用
            netWorkConfig = getNetWorkSingleConfigMap().get(baseUrl);

            //如果未注册网络配置情况去赋值给netWorkConfig变量就为null
            if (netWorkConfig == null) {

                Log.i("NetworkHelper", "getRetrofit: " + baseUrl + "当前使用全局网络配置");
                //为null就引用全局网络配置
                netWorkConfig = getNetWorkGlobalConfig();

            } else {
                Log.i("NetworkHelper", "getRetrofit: " + baseUrl + "当前使用单次网络配置");
            }
        } else {
            Log.i("NetworkHelper", "getRetrofit: " + baseUrl + "当前已使用参数传递注册网络配置");
        }

        //对引用网络配置判空校验，未注册配置程序关闭打印异常，反知继续下一步
        checkNetWorkConfig(baseUrl, netWorkConfig);

        Log.i("NetworkHelper", "getRetrofit: " + baseUrl + "成功引用网络配置");

        //将接口传递来的网络配置参数装载Retrofit，生成Service
        if (mRetrofit == null) {
            //开始构建Retrofit
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(baseUrl) //服务器URL
                    .client(getHttpClient(context, baseUrl, netWorkConfig, isEnableCookieStore));//设置使用okhttp网络请求，加载Okhttp已配置的网络参数
            //设置实体转换器模式
            if (!isEnableConverterFactory) {
                Log.i("NetworkHelper", "getRetrofit: Current not using converter factory state!" +
                        " will could not locate ResponseBody converter for bean!" +
                        "If you do not use converter factory, please set the Retrofit service interface " +
                        "Change the return value type to ResponseBody, and then you can only manually complete data parsing and usage!");
            } else {
                Log.i("NetworkHelper", "getRetrofit: Current yes using converter factory state!");
                //开启实体转换器必须设置模式
                if (netWorkConfig.setConverterFactoryMode() == null) {
                    throw new IllegalStateException("converter factory mode can not be null!");
                }
                //匹配模式设置属性
                switch (netWorkConfig.setConverterFactoryMode()) {
                    case GSON:
                        builder.addConverterFactory(GsonConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用Gson转换器");
                        break;
                    case JACKSON:
                        builder.addConverterFactory(JacksonConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用jackson转换器");
                        break;
                    case SCALARS:
                        builder.addConverterFactory(ScalarsConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用scalars转换器");
                        break;
                    case MOSHI:
                        builder.addConverterFactory(MoshiConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用moshi转换器");
                        break;
                    case SIMPLE_XML:
                        builder.addConverterFactory(SimpleXmlConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用simplexml转换器");
                        break;
                    case WIRE:
                        builder.addConverterFactory(WireConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用wire转换器");
                        break;
                    case PROTOCOL_BUFFERS:
                        builder.addConverterFactory(ProtoConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用protobuf转换器");
                        break;
                    case JAXB:
                        builder.addConverterFactory(JaxbConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用jaxb转换器");
                        break;
                    case JAVA8:
                        builder.addConverterFactory(Java8OptionalConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用java8转换器");
                        break;
                    case GUAVA:
                        builder.addConverterFactory(GuavaOptionalConverterFactory.create());
                        Log.i("NetworkHelper", "getRetrofit: 使用guava转换器");
                        break;
                    case CUSTOM:
                        Converter.Factory factory = netWorkConfig.setCustomConverterFactory();
                        if (factory != null) {
                            builder.addConverterFactory(factory);
                            Log.i("NetworkHelper", "getRetrofit: 使用定制转换器");
                        }
                        break;
                }
            }

            //判断是否启用RxJava适配器
            if (!isEnableRxJava) {
                Log.i("NetworkHelper", "getRetrofit: Current not using rxjava state!");
            } else {
                //添加回调库
                builder.addCallAdapterFactory(RxJava3CallAdapterFactory.create());
                Log.i("NetworkHelper", "getRetrofit: Current yes using rxjava state!");
            }

            //结束并构建Retrofit对象
            mRetrofit = builder.build();

            //重用网络配置：将本次已引用网络配置的Retrofit对象存入Map集合，下次根据key服务器URL获取对应的Retrofit对象值
            getRetrofitMap().put(baseUrl, mRetrofit);

            //重用网络配置：将本次已引用网络配置的单次注册的网络配置存入Map集合
            getNetWorkSingleConfigMap().put(baseUrl, netWorkConfig);
        }
        return mRetrofit;
    }

    /**
     * 创建Retrofit的Class请求接口，默认使用全局网络配置
     *
     * @param context                  上下文
     * @param baseUrl                  服务器URL
     * @param isEnableConverterFactory 是否启用实体转换器
     * @param isEnableRxJava           是否启用RxJava线程调度适配器
     * @param isEnableCookieStore      是否启用Cookie存取模式
     * @param service                  服务class
     * @param <C>                      泛型
     * @return Retrofit对象
     */
    public static <C> C getApiServiceClass(Context context, String baseUrl, boolean isEnableConverterFactory, boolean isEnableRxJava, boolean isEnableCookieStore, Class<C> service) {
        return getInstance().getRetrofit(context, baseUrl, isEnableConverterFactory, isEnableRxJava, isEnableCookieStore, null).create(service);
    }

    /**
     * 创建Retrofit的Class请求接口，指定使用单次网络配置
     *
     * @param context                  上下文
     * @param baseUrl                  服务器URL
     * @param isEnableConverterFactory 是否启用实体转换器
     * @param isEnableRxJava           是否启用RxJava线程调度适配器
     * @param isEnableCookieStore      是否启用Cookie存取模式
     * @param service                  服务class
     * @param singleConfig             单次网络配置
     * @param <C>                      泛型
     * @return Retrofit对象
     */
    public static <C> C getApiServiceClass(Context context, String baseUrl, boolean isEnableConverterFactory, boolean isEnableRxJava, boolean isEnableCookieStore, Class<C> service, INetworkConfig singleConfig) {
        return getInstance().getRetrofit(context, baseUrl, isEnableConverterFactory, isEnableRxJava, isEnableCookieStore, singleConfig).create(service);
    }

    /**
     * 获取Retrofit的Map集合
     *
     * @return Retrofit对象集合
     */
    public Map<String, Retrofit> getRetrofitMap() {
        return retrofitMap;
    }

    /* ********************************** Okhttp配置 *************************************/

    /**
     * 设置日志拦截器打印日志：
     * NONE 无
     * BASIC  响应行
     * HEADERS 响应行+头、
     * BODY 响应行+头+体
     *
     * @return 日志拦截器实例
     */
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        //创建okhttp3日志拦截器实例
        return new HttpLoggingInterceptor(strLogMsg -> {
            //设置打印retrofit日志前缀
            Log.i("okpLog", strLogMsg);
        }).setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    /**
     * 获取和配置OkHttpClient：连接超时时间、读取和写入超时时间、自定义拦截器、请求日志拦截器
     *
     * @param context             上下文
     * @param baseUrl             服务器URL
     * @param netWorkConfig       网络配置
     * @param isEnableCookieStore 是否启用Cookie存取模式
     * @return OkHttpClient对象
     */
    public OkHttpClient getHttpClient(Context context, String baseUrl, INetworkConfig netWorkConfig, boolean isEnableCookieStore) {
        //服务器URL为空时程序关闭打印异常
        if (TextUtils.isEmpty(baseUrl)) throw new IllegalStateException("baseUrl can not be null!");

        //判断本次请求的服务器URL在Map集合中是否已注册有Okhttp对象，如果已创建对象返回给Retrofit使用
        if (getOkhttpClientMap().get(baseUrl) != null) {
            Log.i("NetworkHelper", "getHttpClient: " + baseUrl + "——>OkHttpClient已存在创建，正在引用中..");
            return getOkhttpClientMap().get(baseUrl);
        } else {
            Log.i("NetworkHelper", "getHttpClient: " + baseUrl + "——>OkHttpClient未创建");
        }

        //此处需对赋值引用全局配置进行判空校验，无全局网络配置程序关闭打印异常，反知即可进行下一步配置
        checkNetWorkConfig(baseUrl, netWorkConfig);

        if (mClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //请求错误后重试
            builder.retryOnConnectionFailure(netWorkConfig.setIsEnableRetryOnConnection());
            // 请求连接超时时间，时间数值为0就使用默认值，时间数值不为0就使用Application注册传递过来的时间数值
            builder.connectTimeout(netWorkConfig.setConnectTimeoutMills() != 0
                    ? netWorkConfig.setConnectTimeoutMills()
                    : DEFAULT_CONNECT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);
            // 数据读取超时时间，时间数值为0就使用默认值，时间数值不为0就使用Application注册传递过来的时间数值
            builder.readTimeout(netWorkConfig.setReadTimeoutMills() != 0
                    ? netWorkConfig.setReadTimeoutMills()
                    : DEFAULT_READ_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);
            // 数据写入超时时间，时间数值为0就使用默认值，时间数值不为0就使用Application注册传递过来的时间数值
            builder.writeTimeout(netWorkConfig.setWriteTimeoutMills() != 0
                    ? netWorkConfig.setWriteTimeoutMills()
                    : DEFAULT_WRITE_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);

            //设置Cookie存取模式
            if (!isEnableCookieStore) {
                Log.i("NetworkHelper", "getRetrofit: Current not using cookie store state!");
            } else {
                Log.i("NetworkHelper", "getRetrofit: Current yes using cookie store state!");
                //开启Cookie存取必须设置模式
                if (netWorkConfig.setCookieStoreMode() == null) {
                    throw new IllegalStateException("cookie store mode can not be null!");
                }
                //匹配模式设置属性
                switch (netWorkConfig.setCookieStoreMode()) {
                    case SP:
                        builder.cookieJar(new CookieJarImpl(new SpCookieStore(context)));
                        Log.i("NetworkHelper", "getHttpClient: 当前Cookie存取模式是SharedPreferences");
                        break;
                    case MEMORY:
                        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
                        Log.i("NetworkHelper", "getHttpClient: 当前Cookie存取模式是Memory");
                        break;
                    case DB:
                        // TODO: 2023/8/31 待完善DB模式存取Cookie
                        builder.cookieJar(new CookieJarImpl(new DBCookieStore()));
                        Log.i("NetworkHelper", "getHttpClient: 当前Cookie存取模式是DB");
                        break;
                    case CUSTOM:
                        //自定义
                        CookieJar cookieJar = netWorkConfig.setCustomCookieStore();
                        if (cookieJar != null) {
                            builder.cookieJar(cookieJar);
                            Log.i("NetworkHelper", "getHttpClient: 当前Cookie存取模式是Custom");
                        } else {
                            throw new IllegalStateException("You must first create a class to implement the CookieJar interface of Okhttp before completing the customization of business requirements!");
                        }
                        break;
                }
            }
            //设置请求头
            builder.addInterceptor(new InterceptorImpl(new HeadersInterceptor(netWorkConfig.setHeaderParameters())));
            //设置URL公共参数
            builder.addInterceptor(new InterceptorImpl(new UrlParameterInterceptor(netWorkConfig.setUrlParameter())));

            //接口传递：遍历设置添加定制拦截器，拦截交互数据
            InterceptorHandler[] interceptorHandlers = netWorkConfig.setCustomInterceptor();
            if (interceptorHandlers != null && interceptorHandlers.length > 0) {
                for (InterceptorHandler interceptorHandler : interceptorHandlers) {
                    //依次配置拦截器到Okhttp中
                    builder.addInterceptor(new InterceptorImpl(interceptorHandler));
                    Log.i("NetworkHelper", "添加定制拦截器（接口传递方式）：" + interceptorHandler.getClass().getSimpleName());
                }
            }

            //非接口传递：遍历设置添加创建拦截器数组，如通过拦截器添加统一请求头等。
            Interceptor[] interceptors = netWorkConfig.setInterceptors();
            if (interceptors != null && interceptors.length > 0) {
                for (Interceptor interceptor : interceptors) {
                    //依次配置拦截器到Okhttp中
                    builder.addInterceptor(interceptor);
                    Log.i("NetworkHelper", "添加定制拦截器（非接口传递方式）：" + interceptor.getClass().getSimpleName());
                }
            }

            //判断是否开启打印默认日志
            //根据APK打包类型(开发版或发布版)判定当前应用程序是否启用日志拦截器打印请求日志。PS：开发版启用打印，发布版禁用打印。
            if (netWorkConfig.setIsEnableOkpDefaultPrintLog()) {
                builder.addNetworkInterceptor(getHttpLoggingInterceptor());
                Log.i("NetworkHelper", "已启用Okhttp默认日志打印");
            } else {
                Log.i("NetworkHelper", "未启用Okhttp默认日志打印");
            }

            //判断是否开启缓存 + 设置缓存时间
            if (netWorkConfig.setIsEnableCache()) {
                builder.addInterceptor(new InterceptorImpl(
                        new CachesInterceptor(context, netWorkConfig.setCacheMaxAgeTimeUnitSeconds() != 0
                                ? netWorkConfig.setCacheMaxAgeTimeUnitSeconds()
                                : DEFAULT_MAX_AGE, netWorkConfig.setCacheMaxStaleTimeUnitSeconds() != 0
                                ? netWorkConfig.setCacheMaxStaleTimeUnitSeconds()
                                : DEFAULT_MAX_STALE))
                );

                //构建设置设置缓存目录和缓存大小为10MB
                builder.cache(new Cache(new File(
                        context.getCacheDir().getAbsolutePath(), "MyNetworkCache"), 10 * 1024 * 1024)
                );
                Log.i("NetworkHelper", "已启用Okhttp缓存：缓存时间==" + 10 * 1024 * 1024);
            } else {
                Log.i("NetworkHelper", "未启用Okhttp缓存");
            }

            //开始构建OkhttpClient对象
            mClient = builder.build();

            //重用网络配置：将本次已引用网络配置的OkhttpClient对象存入Map集合，下次根据key服务器URL获取对应的OkhttpClient对象值
            getOkhttpClientMap().put(baseUrl, mClient);

            //重用网络配置：将本次已引用网络配置的单次注册的网络配置存入Map集合
            getNetWorkSingleConfigMap().put(baseUrl, netWorkConfig);
        }
        return mClient;
    }

    /**
     * 获取OkHttpClient的Map集合
     *
     * @return OkHttpClient对象集合
     */
    public Map<String, OkHttpClient> getOkhttpClientMap() {
        return okhttpClientMap;
    }

    /* ********************************** RxJava配置：线程调用 + 异常处理变换（详见TransformerNormalHelper、TransformerExtendsResponseHelper）*************************************/

//    /**
//     * 异常处理变换
//     *
//     * @return
//     */
//    public static <T extends IModel> FlowableTransformer<T, T> getApiTransformer() {
//
//        return new FlowableTransformer<T, T>() {
//            @NonNull
//            @Override
//            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
//                return upstream.flatMap(new Function<T, Publisher<T>>() {
//                    @Override
//                    public Publisher<T> apply(@NonNull T model) throws Exception {
//                        if (model.isNull()) {
//                            return Flowable.error(new NetError(model.getErrorMsg(), NetError.NoDataError));
//                        } else if (model.isAuthError()) {
//                            return Flowable.error(new NetError(model.getErrorMsg(), NetError.AuthError));
//                        } else if (model.isBizError()) {
//                            return Flowable.error(new NetError(model.getErrorMsg(), NetError.BusinessError));
//                        } else {
//                            return Flowable.just(model).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());
//                        }
//                    }
//                });
//            }
//        };
//    }

}
