package zsdev.work.network.rxjava.function;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.functions.Function;
import zsdev.work.network.base.BaseResponse;
import zsdev.work.network.exception.ServerException;

/**
 * Created: by 2023-09-12 00:12
 * Description: Observable + Flowable 的Function处理数据解析
 * Author: 张松
 */
public class ResponseFunction<T> implements Function<BaseResponse<T>, T> {
    /**
     * Code==200表示请求服务器且响应成功
     * Code!=200表示请求数据异常，如服务器返回非200的错误Code，但依然可以判定已经通过接口访问了服务器
     *
     * @param tBaseResponse 上游值BaseResponse
     * @return T对象数据实体
     * @throws Exception Code非200时的异常
     */
    @Override
    public T apply(@NonNull BaseResponse<T> tBaseResponse) {
        //匿名实现Function装载上游值，调用isSuccess()判断响应结果，T为下游值
        //状态码!=200就将json数据的msg值装载，手动抛异常，既然是异常，在RxJava中发送异常一定会调用onError()方法，下游onErrorResumeNext能够处理
        if (!tBaseResponse.isSuccess()) {
            // TODO: 2023/10/6 待修复
            throw new RuntimeException(tBaseResponse.getMsg());
        }
        //状态码code==200时请求成功，直接返回T对象数据实体，也是就是onNext(T tData)，然后Model层通过View引用调用UI显示
        return tBaseResponse.getData();
    }
}
