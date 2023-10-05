package zsdev.work.lib.common.network;


import zsdev.work.lib.common.network.exception.ResponseThrowable;

/**
 * Created: by 2023-09-12 13:36
 * Description:
 * Author: 张松
 */
public interface INetworkHandler<T> {

    /**
     * 请求失败
     *
     * @param responseThrowable 响应异常
     */
    void onFail(ResponseThrowable responseThrowable);

    /**
     * 请求成功
     *
     * @param t 应结果的对象数据实体
     */
    void onSuccess(T t);
}
