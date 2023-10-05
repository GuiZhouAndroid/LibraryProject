package zsdev.work.lib.common.network.enums;

/**
 * Created: by 2023-08-31 19:42
 * Description: Cookie存取模式
 * Author: 张松
 */
public enum CookieStoreMode {

    /**
     * Mao集合存取
     */
    MEMORY,

    /**
     * SharedPreferences存取
     */
    SP,

    /**
     * 数据库存取，如LitePal、Sqlite等DB
     */
    DB,

    /**
     * 定制存储模式
     * 使用定制模式，必须先创建类去实现Okhttp的CookieJar接口之后完成业务需求的定制
     */
    CUSTOM;
}
