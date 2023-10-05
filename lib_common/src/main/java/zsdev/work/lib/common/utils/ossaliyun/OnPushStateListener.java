package zsdev.work.lib.common.utils.ossaliyun;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

/**
 * Created: by 2023-09-26 10:59
 * Description: 推送文件状态结果监听接口
 * Author: 张松
 */
public interface OnPushStateListener {

    /**
     * OSS推送成功
     *
     * @param request 发送请求对象
     * @param result  请求结果对象
     */
    void onSuccess(PutObjectRequest request, PutObjectResult result);

    /**
     * OSS推送失败
     *
     * @param request          发送请求对象
     * @param clientException  应用客户端异常
     * @param serviceException 阿里服务端异常
     */
    void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException);
}

