package zsdev.work.mvp.base;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.RxPreferenceFragment;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import lombok.Getter;
import zsdev.work.mvp.IModel;
import zsdev.work.mvp.IPresenter;
import zsdev.work.mvp.IView;

/**
 * Created: by 2023-09-20 12:50
 * Description: 控制层P基类，处理内存泄漏。控制层P作为中转站和数据处理者 定义V与M的交互行为，调用者是视图层V。P做中转。接口实现类持有V和M的引用。
 * Author: 张松
 */
public class BasePresenter<V extends IView, M extends IModel> implements IPresenter {
    /**
     * P持久V引用
     */
    @Getter
    protected V nowView;

    /**
     * P持久M引用
     */
    @Getter
    protected M nowModel;

    /**
     * 绑定View，提供给V层创建P使用
     *
     * @param nowView 当前View引用
     */
    public BasePresenter(V nowView) {
        this.nowView = nowView;
    }

    /**
     * 绑定View和Model实例，便于DB操作时能获取Model的业务操作数据后，将数据传递到View显示
     *
     * @param nowView  当前View引用
     * @param nowModel 当前Model引用
     */
    public BasePresenter(V nowView, M nowModel) {
        this.nowView = nowView;
        this.nowModel = nowModel;
    }

    /**
     * 因为使用AutoDispose处理类P与V的订阅泄漏问题，而P与M的引用关系并未处理
     * 所以生命周期销毁时将Model引用置为null
     *
     * @param owner 生命周期
     */
    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (nowModel != null) {
            nowModel.onDestroy();
            this.nowModel = null;
        }
    }

    /**
     * compose简化线程,统一处理Observable线程调度和绑定生命周期
     *
     * @param <T> 指定的泛型类型
     * @return Observable转换器
     */
    protected <T> ObservableTransformer<T, T> getBindLifecycleObservableTransformer() {
        return new ObservableTransformer<T, T>() {
            @NonNull
            @Override
            public Observable<T> apply(@NonNull Observable<T> eObservable) {
                // 最终用于订阅的
                LifecycleTransformer<T> bindUntilEvent = null;
                // ---------------------------------进行绑定前判断是相应类型------------
                if (nowView instanceof RxAppCompatActivity) {
                    bindUntilEvent = ((RxAppCompatActivity) nowView).bindUntilEvent(ActivityEvent.DESTROY);
                    Log.i("BasePresenter", "Observable bindUntilEvent: RxAppCompatActivity");
                }
                if (nowView instanceof RxFragmentActivity) {
                    bindUntilEvent = ((RxFragmentActivity) nowView).bindUntilEvent(ActivityEvent.DESTROY);
                    Log.i("BasePresenter", "Observable bindUntilEvent: RxFragmentActivity");
                }
                if (nowView instanceof RxActivity) {
                    bindUntilEvent = ((RxActivity) nowView).bindUntilEvent(ActivityEvent.DESTROY);
                    Log.i("BasePresenter", "Observable bindUntilEvent: RxActivity");
                }
                if (nowView instanceof RxPreferenceFragment) {
                    bindUntilEvent = ((RxPreferenceFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Observable bindUntilEvent: RxPreferenceFragment");
                }
                if (nowView instanceof com.trello.rxlifecycle2.components.RxFragment) {
                    bindUntilEvent = ((com.trello.rxlifecycle2.components.RxFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Observable bindUntilEvent: com.trello.rxlifecycle2.components.RxFragment");
                }
                if (nowView instanceof com.trello.rxlifecycle2.components.support.RxFragment) {
                    bindUntilEvent = ((com.trello.rxlifecycle2.components.support.RxFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Observable bindUntilEvent: com.trello.rxlifecycle2.components.support.RxFragment");
                }
                if (nowView instanceof com.trello.rxlifecycle2.components.RxDialogFragment) {
                    bindUntilEvent = ((com.trello.rxlifecycle2.components.RxDialogFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Observable bindUntilEvent:com.trello.rxlifecycle2.components.RxDiaLogFragment");
                }
                if (nowView instanceof com.trello.rxlifecycle2.components.support.RxDialogFragment) {
                    bindUntilEvent = ((com.trello.rxlifecycle2.components.support.RxDialogFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Observable bindUntilEvent:com.trello.rxlifecycle2.components.support.RxDiaLogFragment");
                }
                // ---------------------------------类型匹配成功调用compose()组装绑定------------

                if (bindUntilEvent != null) {
                    Log.i("BasePresenter", "getBindLifecycleObservableTransformer:开始绑定 ");
                    //绑定
                    return eObservable.compose(bindUntilEvent);
                }
                // ---------------------------------类型匹配失败调用直接返回------------
                return eObservable;
            }
        };
    }

    /**
     * compose简化线程,统一处理Flowable线程调度和绑定生命周期
     *
     * @param <T> 指定的泛型类型
     * @return Flowable转换器
     */
    protected <T> FlowableTransformer<T, T> getBindLifecycleFlowableTransformer() {
        return new FlowableTransformer<T, T>() {
            @NonNull
            @Override
            public Flowable<T> apply(@NonNull Flowable<T> eFlowable) {
                // 最终用于订阅的
                LifecycleTransformer<T> bindUntilEvent = null;
                // ---------------------------------进行绑定前判断是相应类型------------
                if (nowView instanceof RxAppCompatActivity) {
                    bindUntilEvent = ((RxAppCompatActivity) nowView).bindUntilEvent(ActivityEvent.DESTROY);
                    Log.i("BasePresenter", "Flowable bindUntilEvent: RxAppCompatActivity");
                }
                if (nowView instanceof RxFragmentActivity) {
                    bindUntilEvent = ((RxFragmentActivity) nowView).bindUntilEvent(ActivityEvent.DESTROY);
                    Log.i("BasePresenter", "Flowable bindUntilEvent: RxFragmentActivity");
                }
                if (nowView instanceof RxActivity) {
                    bindUntilEvent = ((RxActivity) nowView).bindUntilEvent(ActivityEvent.DESTROY);
                    Log.i("BasePresenter", "Flowable bindUntilEvent: RxActivity");
                }
                if (nowView instanceof RxPreferenceFragment) {
                    bindUntilEvent = ((RxPreferenceFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Flowable bindUntilEvent: RxPreferenceFragment");
                }
                if (nowView instanceof com.trello.rxlifecycle2.components.RxFragment) {
                    bindUntilEvent = ((com.trello.rxlifecycle2.components.RxFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Flowable bindUntilEvent: com.trello.rxlifecycle2.components.RxFragment");
                }
                if (nowView instanceof com.trello.rxlifecycle2.components.support.RxFragment) {
                    bindUntilEvent = ((com.trello.rxlifecycle2.components.support.RxFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Flowable bindUntilEvent: com.trello.rxlifecycle2.components.support.RxFragment");
                }
                if (nowView instanceof com.trello.rxlifecycle2.components.RxDialogFragment) {
                    bindUntilEvent = ((com.trello.rxlifecycle2.components.RxDialogFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Flowable bindUntilEvent:com.trello.rxlifecycle2.components.RxDiaLogFragment");
                }
                if (nowView instanceof com.trello.rxlifecycle2.components.support.RxDialogFragment) {
                    bindUntilEvent = ((com.trello.rxlifecycle2.components.support.RxDialogFragment) nowView).bindUntilEvent(FragmentEvent.DESTROY);
                    Log.i("BasePresenter", "Flowable bindUntilEvent:com.trello.rxlifecycle2.components.support.RxDiaLogFragment");
                }
                // ---------------------------------类型匹配成功调用compose()组装绑定------------
                if (bindUntilEvent != null) {
                    Log.i("BasePresenter", "getBindLifecycleFlowableTransformer:开始绑定 ");
                    //绑定
                    return eFlowable.compose(bindUntilEvent);
                }
                // ---------------------------------类型匹配失败调用直接返回------------
                return eFlowable;
            }
        };
    }
}
