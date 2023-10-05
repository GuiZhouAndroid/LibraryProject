package zsdev.work.lib.common.network.enums;

/**
 * Created: by 2023-09-05 12:54
 * Description: Retrofit实体转换器模式，都需要继承Converter.Factory
 * Author: 张松
 */
public enum ConverterMode {

    /**
     * gson转换器
     */
    GSON,

    /**
     * jackson转换器
     */
    JACKSON,

    /**
     * 字符串转换器
     */
    SCALARS,

    /**
     * moshi转换器
     */
    MOSHI,

    /**
     * simplexml转换器
     */
    SIMPLE_XML,

    /**
     * wire转换器
     */
    WIRE,

    /**
     * protobuf转换器
     */
    PROTOCOL_BUFFERS,

    /**
     * jaxb转换器
     */
    JAXB,

    /**
     * java8转换器
     */
    JAVA8,

    /**
     * guava转换器
     */
    GUAVA,

    /**
     * 定制转换器
     */
    CUSTOM;
}
