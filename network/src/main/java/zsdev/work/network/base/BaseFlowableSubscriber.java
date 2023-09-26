package zsdev.work.network.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;


import io.reactivex.subscribers.ResourceSubscriber;
import zsdev.work.dialog.custom.DialogCustomView;
import zsdev.work.network.INetworkHandler;
import zsdev.work.network.exception.NetworkError;
import zsdev.work.network.exception.ResponseThrowable;
import zsdev.work.network.utils.NetworkUtil;

/**
 * Created: by 2023-09-07 23:48
 * Description:统一管理Subscriber订阅.
 * 自定义一个Subscriber来对Exception进行捕获，
 * 也需要对其它Exception进行捕获和包裹，防止发生错误后直接崩溃。
 * Subscriber基类,可以在这里处理client网络连接状况（比如没有wifi，没有4g，没有联网等）
 * Author: 张松
 */
public abstract class BaseFlowableSubscriber<T> extends ResourceSubscriber<T> implements INetworkHandler<T> {

    /**
     * Context上下文
     */
    private final Context context;

    /**
     * 请求等待框
     */
    private Dialog dialog;

    /**
     * 创建BaseSubscriber 用来接收上下文，设置网络请求进度条
     *
     * @param context  Context上下文
     * @param activity Activity上下文
     */
    public BaseFlowableSubscriber(Context context, Activity activity) {
        Log.i("BaseFlowableSubscriber", "BaseFlowableNormalSubscriber():" + context.getClass().getSimpleName());
        this.context = context;
        if (dialog == null) {
            dialog = DialogCustomView.setFirewoodLoadingDialog(activity);
        }
    }

    /**
     * 创建BaseSubscriber 用来接收上下文，设置网络请求进度条
     *
     * @param context           Context上下文
     * @param activity          Activity上下文
     * @param loadingRendererId LoadingRenderer的id
     */
    public BaseFlowableSubscriber(Context context, Activity activity, int loadingRendererId) {
        Log.i("BaseFlowableSubscriber", "BaseFlowableNormalSubscriber():" + context.getClass().getSimpleName());
        this.context = context;
        if (dialog == null) {
            dialog = DialogCustomView.setModeLoadingDialogRendererId(activity, loadingRendererId);
        }
    }

    /**
     * 网络请求开始
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("BaseFlowableSubscriber", "onStart():显示进度条");
        if (!NetworkUtil.isAvailable(context)) {
            Log.i("BaseFlowableSubscriber", "onStart():当前网络不可用，请检查网络情况");
            // 一定好主动调用下面这一句
            onComplete();
        }
        //显示等待框
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 网络请求错误
     *
     * @param throwable 异常对象
     */
    @Override
    public void onError(Throwable throwable) {
        if (throwable instanceof ResponseThrowable) {
            onFail((ResponseThrowable) throwable);
//            onFail(new ResponseThrowable(throwable, NetworkError.UNKNOWN));
            Log.i("BaseFlowableSubscriber", "ResponseThrowable():" + throwable.toString());
        } else {
            onFail(new ResponseThrowable(throwable, NetworkError.UNKNOWN));
            Log.i("BaseFlowableSubscriber", "ResponseThrowable():其他错误");
        }
        //关闭等待框
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 网络请求成功将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型实体
     */
    @Override
    public void onNext(T t) {
        Log.i("BaseFlowableSubscriber", "onNext()：" + t.toString());
        onSuccess(t);
    }

    /**
     * 请求或响应完结不论成功与失败
     * 完成隐藏ProgressDialog
     */
    @Override
    public void onComplete() {
        Log.i("BaseFlowableSubscriber", "onComplete():关闭等待框");
        //关闭等待框
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
