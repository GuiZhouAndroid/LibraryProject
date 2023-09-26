package zsdev.work.utils.ossaliyun;

import android.content.Context;

/**
 * Created: by 2023-09-26 11:00
 * Description: 初始化OssManager工具类的构建参数
 * Author: 张松
 */
public class OssBuilder {
    /**
     * 上下文
     */
    private final Context context;

    /**
     * 桶名 bucket name
     */
    private String bucketName;

    /**
     * access key id
     */
    private String accessKeyId;

    /**
     * access key secret
     */
    private String accessKeySecret;

    /**
     * 阿里云公网域名URL地址 EndPoint
     */
    private String endPoint;

    /**
     * 文件名或文件目录
     */
    private String objectKey;

    /**
     * 本地文件路径
     */
    private String localFilePath;

    public OssBuilder(Context context) {
        this.context = context;
    }

    public OssBuilder bucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public OssBuilder accessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public OssBuilder accessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public OssBuilder endPoint(String endPint) {
        this.endPoint = endPint;
        return this;
    }

    public OssBuilder objectKey(String objectKey) {
        this.objectKey = objectKey;
        return this;
    }

    public OssBuilder localFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
        return this;
    }

    /**
     * 构建OSS工具构建对象
     *
     * @return 工具类对象
     */
    public OssManagerUtil build() {
        return new OssManagerUtil(context, bucketName, accessKeyId, accessKeySecret, endPoint, objectKey, localFilePath);
    }
}
