package zsdev.work.lib.common.utils.ossaliyun;

import com.alibaba.sdk.android.oss.model.PutObjectRequest;

/**
 * Created: by 2023-09-26 10:54
 * Description: 推送文件进度条监听接口
 * Author: 张松
 */
public interface OnPushProgressListener {
    /**
     * OSS推送文件进度回调接口
     *
     * @param request     发送请求对象
     * @param currentSize 当前推送文件进度值
     * @param totalSize   总推送文件进度值
     */
    void onProgress(PutObjectRequest request, long currentSize, long totalSize);
}
