package zsdev.work.lib.common.network.rxjava.transformer;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * Created: by 2023-09-11 16:27
 * Description: Observable和Flowable 的线程调度切换的Scheduler
 * Author: 张松
 */
public class SchedulerTransformer {

    /**
     * Presenter中使用到IO子线程与Main主线程切换，每次都需要重新写就比较繁琐
     * Observable处理线程切换调度的转换器
     * subscribeOn()：设置Observable被观察者在什么线程运行.得先创建Observable，这里对应Retrofit中Service的返回值，
     * subscribeOn()只被执行一次。若出现多次，以第1次出现使用线程为准，只有第1个subscribeOn()起作用，Observable发送数据的时候是从上游往下游的，
     * 在这个过程中，发射数据的线程已经被subscribeOn指定过了，这个过程本身不会主动去切换线程，所以数据发射和传递的所有工作线程都是同一个
     * observeOn()：设置Observer观察者在什么线程运行，还将影响后面的onNext,map….的运行线程
     * unsubscribeOn()：解绑上次线程
     *
     * @param <T> 泛型
     * @return Observable转换器
     */
    public static <T> ObservableTransformer<T, T> getObservableScheduler() {
        return new ObservableTransformer<T, T>() {
            @NonNull
            @Override
            public Observable<T> apply(@NonNull Observable<T> observable) {
                return observable
                        //这仅影响Observable订阅时使用的线程，并且它将保留在下游,如果流中有多个实例subscribeOn，则只有第一个具有实际效果
                        .subscribeOn(Schedulers.io())
                        //指定下游运算所在的线程，使用observeOn所指定的线程来操作的后续切换和数据流推送
                        .observeOn(AndroidSchedulers.mainThread())
                        //解绑上次的线程
                        .unsubscribeOn(Schedulers.io());
            }
        };
    }

    /**
     * Presenter中使用到IO子线程与Main主线程切换，每次都需要重新写就比较繁琐
     * Flowable处理线程切换调度的转换器
     * 执行网络/ IO /计算任务时，使用后台调度程序至关重要。如果没有subscribeOn()，您的代码将使用调用程序线程来执行操作，从而导致Observable阻塞
     *
     * @param <T> 泛型
     * @return Flowable转换器
     */
    public static <T> FlowableTransformer<T, T> getFlowableScheduler() {
        return new FlowableTransformer<T, T>() {
            @NonNull
            @Override
            public Flowable<T> apply(@NonNull Flowable<T> flowable) {
                return flowable
                        //这仅影响Observable订阅时使用的线程，并且它将保留在下游,如果流中有多个实例subscribeOn，则只有第一个具有实际效果
                        .subscribeOn(Schedulers.io())
                        //指定下游运算所在的线程，使用observeOn所指定的线程来操作的后续切换和数据流推送
                        .observeOn(AndroidSchedulers.mainThread())
                        //解绑上次的线程
                        .unsubscribeOn(Schedulers.io());
            }
        };
    }
}
