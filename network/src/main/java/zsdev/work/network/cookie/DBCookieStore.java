package zsdev.work.network.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created: by 2023-09-04 23:28
 * Description: Cookie的DB存取类
 * Author: 张松
 */
// TODO: 2023/9/4 待实现DB
public class DBCookieStore implements CookieStore {
    @Override
    public void addCookieByHttpUrl(HttpUrl url, List<Cookie> cookies) {

    }

    @Override
    public List<Cookie> getCookieByHttpUrl(HttpUrl url) {
        return null;
    }

    @Override
    public List<Cookie> getAllCookies() {
        return null;
    }

    @Override
    public boolean removeCookieByHttpUrl(HttpUrl url, Cookie cookie) {
        return false;
    }

    @Override
    public boolean removeAllCookies() {
        return false;
    }
}
