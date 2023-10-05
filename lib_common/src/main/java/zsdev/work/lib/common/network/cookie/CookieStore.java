package zsdev.work.lib.common.network.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created: by 2023-09-04 21:49
 * Description: Cookie存取接口
 * Author: 张松
 */
public interface CookieStore {
    /**
     * 存储Cookie
     *
     * @param url     Sp的key值url
     * @param cookies Cookie集合
     */
    void addCookieByHttpUrl(HttpUrl url, List<Cookie> cookies);

    /**
     * 通过Sp的key值获取对应Cookie
     *
     * @param url Sp的key值url
     * @return Cookie集合
     */
    List<Cookie> getCookieByHttpUrl(HttpUrl url);

    /**
     * 获取全部Cookie数据
     *
     * @return cookie集合
     */
    List<Cookie> getAllCookies();

    /**
     * 删除单个Cookie
     *
     * @param url    Sp的key值url
     * @param cookie Sp的value值
     * @return 删除结果
     */
    boolean removeCookieByHttpUrl(HttpUrl url, Cookie cookie);

    /**
     * 删除全部Cookie数据
     *
     * @return 删除结果
     */
    boolean removeAllCookies();
}
