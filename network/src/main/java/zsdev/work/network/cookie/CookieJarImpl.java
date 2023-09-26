package zsdev.work.network.cookie;

import androidx.annotation.NonNull;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created: by 2023-09-04 22:48
 * Description: Cookie持久化实现类。如果有特殊需求，提供Cookie自定义实现，
 * 新建类实现CookieStore并重写全部接口方法，对应每个业务方法将特殊需求定制，如SQL的CRUD操作等。传入此对象到CookieJarImpl(定制对象)即可
 * 。获取响应体中的Cookie后进行持久化并返回给Okhttp配置
 * 永久（App卸载即为Cookie数据丢失）：SD卡、SharedPreferences、数据库
 * 常用于保存用户登录成功后服务器返回的token凭证，下次访问后端其它接口时作为身份验证鉴权。
 * Author: 张松
 */
public class CookieJarImpl implements CookieJar {

    private final CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalStateException("cookieStore can not be null.");
        }
        this.cookieStore = cookieStore;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * 写入Cookie：以url为key，将Cookie数据集合存入value中
     *
     * @param httpUrl    url
     * @param cookieList cookie集合
     */
    @Override
    public synchronized void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> cookieList) {
        cookieStore.addCookieByHttpUrl(httpUrl, cookieList);
    }

    /**
     * 读取Cookie
     *
     * @param httpUrl url
     * @return cookie集合
     */
    @NonNull
    @Override
    public synchronized List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
        return cookieStore.getCookieByHttpUrl(httpUrl);
    }
}
