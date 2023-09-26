package zsdev.work.network.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created: by 2023-09-04 22:38
 * Description: Cookie内存存取类。获取响应体中的Cookie后进行持久化并返回给Okhttp配置。
 * 临时（App应用程序退出即为Cookie数据丢失）：Map集合、Java类(内存)
 * 常用于保存用户登录成功后服务器返回的token凭证，下次访问后端其它接口时作为身份验证鉴权。
 * Author: 张松
 */
public class MemoryCookieStore implements CookieStore {

    /**
     * 存取Cookie数据的Map集合
     */
    private final HashMap<String, List<Cookie>> allCookies = new HashMap<>();

    /**
     * 存储Cookie
     *
     * @param url     Sp的key值url
     * @param cookies Cookie集合
     */
    @Override
    public void addCookieByHttpUrl(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = allCookies.get(url.host());
        if (oldCookies != null) {
            Iterator<Cookie> itNew = cookies.iterator();
            Iterator<Cookie> itOld = oldCookies.iterator();
            while (itNew.hasNext()) {
                String va = itNew.next().name();
                while (va != null && itOld.hasNext()) {
                    String v = itOld.next().name();
                    if (v != null && va.equals(v)) {
                        itOld.remove();
                    }
                }
            }
            oldCookies.addAll(cookies);
        } else {
            allCookies.put(url.host(), cookies);
        }
    }

    /**
     * 通过Sp的key值获取对应Cookie
     *
     * @param url Sp的key值url
     * @return Cookie集合
     */
    @Override
    public List<Cookie> getCookieByHttpUrl(HttpUrl url) {
        List<Cookie> cookies = allCookies.get(url.host());
        if (cookies == null) {
            cookies = new ArrayList<>();
            allCookies.put(url.host(), cookies);
        }
        return cookies;
    }

    /**
     * 获取全部Cookie数据
     *
     * @return cookie集合
     */
    @Override
    public List<Cookie> getAllCookies() {
        List<Cookie> cookies = new ArrayList<>();
        Set<String> httpUrls = allCookies.keySet();
        for (String url : httpUrls) {
            cookies.addAll(Objects.requireNonNull(allCookies.get(url)));
        }
        return cookies;
    }

    /**
     * 删除单个Cookie
     *
     * @param url    Sp的key值url
     * @param cookie Sp的value值
     * @return 删除结果
     */
    @Override
    public boolean removeCookieByHttpUrl(HttpUrl url, Cookie cookie) {
        List<Cookie> cookies = allCookies.get(url.host());
        if (cookie != null) {
            return Objects.requireNonNull(cookies).remove(cookie);
        }
        return false;
    }

    /**
     * 删除全部Cookie数据
     *
     * @return 删除结果
     */
    @Override
    public boolean removeAllCookies() {
        allCookies.clear();
        return true;
    }
}
