package zsdev.work.lib.common.utils.ossaliyun;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

/**
 * Created: by 2023-09-26 11:03
 * Description: 阿里云OSS对象存储工具类
 * Author: 张松
 */
public class OssManagerUtil {
    /**
     * 上下文
     */
    private final Context mContext;

    /**
     * 桶名 bucket name
     */
    private final String mBucketName;

    /**
     * access key id
     */
    private final String mAccessKeyId;

    /**
     * access key secret
     */
    private final String mAccessKeySecret;

    /**
     * 阿里云公网域名URL地址 EndPoint
     */
    private final String mEndPoint;

    /**
     * 文件名或文件目录
     */
    private final String mObjectKey;

    /**
     * 本地文件路径
     */
    private final String mLocalFilePath;

    /**
     * 推进文件进度监听器
     */
    private OnPushProgressListener onPushProgressListener;

    /**
     * 推送文件状态
     */
    private OnPushStateListener onPushStateListener;

    /**
     * OSS 异步任务
     */
    private OSSAsyncTask mOSSAsyncTask;

    public OssManagerUtil(Context context, String bucketName, String accessKeyId, String accessKeySecret, String endPoint, String objectKey, String localFilePath) {
        this.mContext = context;
        this.mBucketName = bucketName;
        this.mAccessKeyId = accessKeyId;
        this.mAccessKeySecret = accessKeySecret;
        this.mEndPoint = endPoint;
        this.mObjectKey = objectKey;
        this.mLocalFilePath = localFilePath;
    }

    /**
     * 设置推送文件进程监听器,推动将回调 onProgress(PutObjectRequest request, long currentSize, long totalSize)
     *
     * @param listener 推送文件进度侦听器
     */
    public void setPushProgressListener(OnPushProgressListener listener) {
        this.onPushProgressListener = listener;
    }

    /**
     * 设置推送文件状态侦听器,成功将回调 onSuccess(PutObjectRequest request, PutObjectResult result)
     * 失败将回调 onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException)
     *
     * @param listener 推送文件状态监听器
     */
    public void setPushStateListener(OnPushStateListener listener) {
        this.onPushStateListener = listener;
    }

    /**
     * 将文件推入 oss,此方法为异步任务
     */
    public void push() {
        // 第一个参数：在【RAM访问控制】创建用户时分配的accessKeyId
        // 第二个参数：在【RAM访问控制】创建用户时分配的accessKeySecret
        OSSCredentialProvider ossCredentialProvider = new OSSPlainTextAKSKCredentialProvider(mAccessKeyId, mAccessKeySecret);
        // 第一个参数：上下文
        // 第二个参数：在OSS控制台创建好Bucket后，会有一个EndPoint(地域节点)，比如我这里的节点是：https://www.zsitking.top/
        // 第三个参数：OSSCredentialProvider实力（初始化已携带 accessKeyId + accessKeySecret）
        OSS oss = new OSSClient(mContext.getApplicationContext(), mEndPoint, ossCredentialProvider);
        onPush(oss);
    }

    /**
     * 将文件推送 oss,此方法为异步任务
     */
    public void push(String accessKeyId, String accessKeySecret, String securityToken) {
        if (accessKeyId == null || accessKeySecret == null || securityToken == null) return;
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
        OSS oss = new OSSClient(mContext.getApplicationContext(), mEndPoint, credentialProvider);
        onPush(oss);
    }

    /**
     * 推送
     *
     * @param oss OSS
     */
    private void onPush(OSS oss) {
        //下面3个参数依次为 bucket名，Object名(OSS中的文件名)，上传文件本机路径
        // 第一个参数：OSS云存储创建的bucket桶名
        // 第二个参数：可以是一个文件路径：比如在bucket桶名中创建了一个文件夹为pic_data,那第二个参数传pic_data/OSS重名后的文件.后缀(重名覆盖问题，拼接时间戳)
        // 第三个参数：是文件的本地路径，相册回调选择：如/storage/emulated/0/Android/data/com.lpssfyx.zs.pictrueselectdemo/files/Pictures/IMG_CMP_103573_11682532.jpeg
        PutObjectRequest put = new PutObjectRequest(mBucketName, mObjectKey, mLocalFilePath);
        // 异步上传时可以设置进度回调。
        put.setProgressCallback((request, currentSize, totalSize) -> {
            Log.d("currentSize = " + currentSize, "totalSize = " + totalSize);
            if (onPushProgressListener != null) {
                onPushProgressListener.onProgress(request, currentSize, totalSize);
            }
        });
        // 此处调用异步上传方法
        mOSSAsyncTask = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {//异步上传成功
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());

                //异步上传成功：回调自定义接口，数据传递到接口方法onSuccess()，将成功数据传递到此接口外部实现者处
                if (onPushStateListener != null) {
                    onPushStateListener.onSuccess(request, result);
                }
            }

            /**
             * 异步上传失败
             *
             * @param request          请求参数
             * @param clientException  本机客户端异常
             * @param serviceException OSS服务端异常
             */
            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常。
                if (clientException != null) {
                    // 本地异常，如网络异常等。
                    Log.e("OssManagerUtil", clientException.getMessage());
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
                //异步上传失败：回调自定义接口，数据传递到接口方法onFailure()，将失败数据传递到此接口外部实现者处
                if (onPushStateListener != null) {
                    onPushStateListener.onFailure(request, clientException, serviceException);
                }
            }
        });
    }

    /**
     * 取消文件推送任务
     */
    public void cancelPush() {
        if (mOSSAsyncTask != null && !mOSSAsyncTask.isCanceled() && !mOSSAsyncTask.isCompleted()) {
            mOSSAsyncTask.cancel();
        }
    }
}

