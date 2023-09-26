package zsdev.work.mvp;

/**
 * Created: by 2023-09-20 11:05
 * Description: 网络回调请求结果
 * Author: 张松
 */
public interface ICallBack<T> {
    /**
     * 请求成功
     *
     * @param data 返回的数据
     */
    void reqSuccess(T data);

    /**
     * 请求失败
     *
     * @param failMsg 失败信息
     */
    void reqFail(String failMsg);

    /**
     * 请求错误
     *
     * @param errorMsg 错误信息
     */
    void reqError(String errorMsg);
}
