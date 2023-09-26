package zsdev.work.network.rxjava.transformer;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import zsdev.work.network.base.BaseResponse;
import zsdev.work.network.rxjava.function.FlowableErrorFunction;
import zsdev.work.network.rxjava.function.ObservableErrorFunction;
import zsdev.work.network.rxjava.function.ResponseFunction;


/**
 * Created: by 2023-09-11 16:13
 * Description: Observable和Flowable 的处理请求响应的数据 + 异常处理变换 + 线程调度切换的Scheduler
 * 【AutoDispose2绑定订阅生命周期处理内存泄漏：请借助RxJava3的to()传入BasePresenter中的bindLifecycle()】
 * Author: 张松
 */
public class HandlerTransformer {

    /**
     * （1）描述同bindLifecycle()
     * （2）数据处理：接收上游对象数据，依据BaseResponse中的isSuccess()方法来识别请求状态
     * 若isSuccess()返回true 即是本次发起的网络请求的响应code为200，此状态码值需要与后端接口统一返回Json固定格式相匹配，通常情况都是以200表示请求服务器接口响应成功
     * 若是其他错误情况，如客户端异常（网络、权限等...）、服务端异常（服务器访问失败、后端服务系统报错等...）、JSON数据解析异常等错误需要判断异常类型后装载对应错误Message返回给UI显示
     *
     * @param <T> 调用者传递到方法上游的对象数据
     * @return Observable转换器
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> getObservableTransformerScheduler() {
        return new ObservableTransformer<BaseResponse<T>, T>() {
            @NonNull
            @Override
            public Observable<T> apply(@NonNull Observable<BaseResponse<T>> observable) {
                return observable
                        //数据成功与异常处理
                        .map(new ResponseFunction<>()).onErrorResumeNext(new ObservableErrorFunction<>())
                        //Observable线程调度
                        .compose(SchedulerTransformer.getObservableScheduler());
            }
        };
    }

    /**
     * （1）描述同bindLifecycle()
     * （2）数据处理：同上
     *
     * @return Flowable转换器
     */
    public static <T> FlowableTransformer<BaseResponse<T>, T> getFlowableTransformerScheduler() {
        //参数1：BaseResponse<T>为上游值（从model层调用传递过来的Bean），参数2：T为下游值（本次RxJava流程未结束，需要传递下一个流程中处理数据）
        return new FlowableTransformer<BaseResponse<T>, T>() {
            @NonNull
            @Override
            public Flowable<T> apply(@NonNull Flowable<BaseResponse<T>> flowable) {
                //引用Function，若此处网络错误，直接onError(Throwable t)
                return flowable
                        //数据成功与异常处理
                        .map(new ResponseFunction<>()).onErrorResumeNext(new FlowableErrorFunction<>())
                        //Flowable线程调度
                        .compose(SchedulerTransformer.getFlowableScheduler());
            }
        };
    }
}
