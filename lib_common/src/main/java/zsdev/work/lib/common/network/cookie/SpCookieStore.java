package zsdev.work.lib.common.network.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created: by 2023-08-17 14:29
 * Description: Cookie的SP存取类
 * 获取响应体中的Cookie后进行持久化并返回给Okhttp配置
 * 永久（App卸载即为Cookie数据丢失）：SD卡、SharedPreferences、数据库
 * 常用于保存用户登录成功后服务器返回的token凭证，下次访问后端其它接口时作为身份验证鉴权。
 * Author: 张松
 */
public class SpCookieStore implements CookieStore {
    /**
     * 日志标记
     */
    private static final String Log_TAG = "SpCookieStore";

    /**
     * Sp的XML文件名
     */
    private static final String COOKIE_PREFS = "Cookies_Prefs";

    /**
     * Cookie名称前缀
     */
    private static final String COOKIE_NAME_PREFIX = "cookie_";

    /**
     * Cookie的map集合
     */
    private final Map<String, ConcurrentHashMap<String, Cookie>> cookies;

    /**
     * SP
     */
    private final SharedPreferences cookiePrefs;

    /**
     * 创建实例，初始化参数
     *
     * @param context 上下文
     */
    public SpCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        cookies = new HashMap<>();

        //将持久化的cookies缓存到内存中 即map cookies
        Map<String, ?> prefsMap = cookiePrefs.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
            for (String name : cookieNames) {
                String encodedCookie = cookiePrefs.getString(name, null);
                if (encodedCookie != null) {
                    Cookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        if (!cookies.containsKey(entry.getKey())) {
                            cookies.put(entry.getKey(), new ConcurrentHashMap<>());
                        }
                        Objects.requireNonNull(cookies.get(entry.getKey())).put(name, decodedCookie);
                    }
                }
            }
        }
    }

    /**
     * 获取Cookie数据中的Token
     *
     * @param cookie Cookie
     * @return Token
     */
    protected String getCookieToken(Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    }

    /**
     * 以Sp的key值作为URL
     * 以Sp的Value作为Cookie
     * 使用Sp持久化Cookie，存放在本机APP。
     * 备注：APP卸载那么Sp文件也会删除，因此Cookie数据也会跟着清空，如有需要可使用数据库来存放
     *
     * @param url    Sp的key值url
     * @param cookie 待持久化的Cookie
     */
    public void addCookieByHttpUrl(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);

        //将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (!cookie.persistent()) {
            if (!cookies.containsKey(url.host())) {
                cookies.put(url.host(), new ConcurrentHashMap<>());
            }
            Objects.requireNonNull(cookies.get(url.host())).put(name, cookie);
        } else {
            if (cookies.containsKey(url.host())) {
                Objects.requireNonNull(cookies.get(url.host())).remove(name);
            }
        }

        //将cookies持久化到本地
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        if (cookies.get(url.host()) != null && !Objects.requireNonNull(cookies.get(url.host())).isEmpty())
            prefsWriter.putString(url.host(), TextUtils.join(",", Objects.requireNonNull(cookies.get(url.host())).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SpCookieSerializable(cookie)));
        prefsWriter.apply();
    }

    /**
     * 外部接口：存储Cookie
     *
     * @param uri     Sp的key值url
     * @param cookies Cookie集合
     */
    @Override
    public void addCookieByHttpUrl(HttpUrl uri, List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            addCookieByHttpUrl(uri, cookie);
        }
    }

    /**
     * 返回此cookie的过期时间，格式与System.currentTimeMillis相同。如果cookie是持久的，则为9999年12月31日，在这种情况下，它将在当前会话结束时过期。
     * 这可能会返回一个小于当前时间的值，在这种情况下，cookie已经过期。网络服务器可能会返回过期的cookie，作为删除先前设置的cookie的机制，这些cookie本身可能过期，也可能未过期。
     *
     * @param cookie Cookie
     * @return Cookie是否过期
     */
    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    /**
     * 通过Sp的key值获取对应Cookie
     *
     * @param url Sp的key值url
     * @return Cookie集合
     */
    @Override
    public List<Cookie> getCookieByHttpUrl(HttpUrl url) {
        ArrayList<Cookie> ret = new ArrayList<>();
        if (cookies.containsKey(url.host())) {
            Collection<Cookie> cookies = Objects.requireNonNull(this.cookies.get(url.host())).values();
            //遍历Cookie集合
            for (Cookie cookie : cookies) {
                //判断每个Cookie数据是否过期
                if (isCookieExpired(cookie)) {
                    //已过期执行移除
                    boolean removeResult = removeCookieByHttpUrl(url, cookie);
                    Log.i("SpStorageCookie", "getCookieByHttpUrl: " + (removeResult
                            ? ("Cookie：" + cookie + "已过期，系统自动删除")
                            : ("Cookie：" + cookie + "未过期"))
                    );
                } else {
                    //未过期执行添加
                    ret.add(cookie);
                }
            }
        }
        return ret;
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
        String name = getCookieToken(cookie);
        if (cookies.containsKey(url.host()) && Objects.requireNonNull(cookies.get(url.host())).containsKey(name)) {
            Objects.requireNonNull(cookies.get(url.host())).remove(name);
            SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name);
            }
            prefsWriter.putString(url.host(), TextUtils.join(",", Objects.requireNonNull(cookies.get(url.host())).keySet()));
            prefsWriter.apply();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除全部Cookie数据
     *
     * @return 删除结果
     */
    @Override
    public boolean removeAllCookies() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        cookies.clear();
        return true;
    }

    /**
     * 获取全部Cookie数据
     *
     * @return cookie集合
     */
    @Override
    public List<Cookie> getAllCookies() {
        ArrayList<Cookie> ret = new ArrayList<>();
        for (String key : cookies.keySet()) {
            ret.addAll(Objects.requireNonNull(cookies.get(key)).values());
        }
        return ret;
    }

    /**
     * cookies 序列化成 string
     *
     * @param cookie 要序列化的cookie
     * @return 序列化之后的string
     */
    protected String encodeCookie(SpCookieSerializable cookie) {
        if (cookie == null)
            return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.d(Log_TAG, "IOException in encodeCookie", e);
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    /**
     * 将字符串反序列化成cookies
     *
     * @param cookieString cookies string
     * @return cookie object
     */
    protected Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SpCookieSerializable) objectInputStream.readObject()).getCookies();
        } catch (IOException e) {
            Log.d(Log_TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(Log_TAG, "ClassNotFoundException in decodeCookie", e);
        }
        return cookie;
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
