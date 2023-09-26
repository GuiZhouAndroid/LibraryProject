package zsdev.work.network.base;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;


import io.reactivex.observers.ResourceObserver;
import zsdev.work.dialog.normal.DialogHelper;
import zsdev.work.dialog.normal.OnDialogCancelListener;
import zsdev.work.network.INetworkHandler;
import zsdev.work.network.exception.NetworkError;
import zsdev.work.network.exception.ResponseThrowable;
import zsdev.work.network.utils.NetworkUtil;

/**
 * Created: by 2023-09-12 00:40
 * Description: 描述同BaseFlowableSubscriber
 * Author: 张松
 */
public abstract class BaseObserverSubscriber<T> extends ResourceObserver<T> implements OnDialogCancelListener, INetworkHandler<T> {

    /**
     * Context上下文
     */
    private final Context context;

    /**
     * Activity上下文
     */
    protected Activity mActivity;

    /**
     * 自定义对话框
     */
    protected DialogHelper mDialogHelper;

    /**
     * 消息对话框文本
     */
    private final String showDialogMessage;

    /**
     * 创建BaseSubscriber 用来接收上下文，设置网络请求进度条
     *
     * @param context          Context上下文
     * @param activity         Activity上下文
     * @param strShowDialogMsg 消息对话框文本
     */
    public BaseObserverSubscriber(Context context, Activity activity, String strShowDialogMsg) {
        Log.i("BaseObserverSubscriber", "BaseObserverNormalSubscriber():" + context.getClass().getSimpleName());
        this.context = context;
        this.mActivity = activity;
        this.showDialogMessage = strShowDialogMsg;
    }

    /**
     * 网络请求开始
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("BaseObserverSubscriber", "onStart():显示进度条");
        if (!NetworkUtil.isAvailable(context)) {
            Log.i("BaseObserverSubscriber", "onStart():当前网络不可用，请检查网络情况");
            // 一定好主动调用下面这一句
            onComplete();
        }
        //创建自定义对话框
        if (mDialogHelper == null) {
            mDialogHelper = new DialogHelper(mActivity, this);
        }
        // 显示进度条
        mDialogHelper.showLoadingDialog(showDialogMessage);
    }

    /**
     * 网络请求错误
     *
     * @param throwable 异常对象
     */
    @Override
    public void onError(Throwable throwable) {
        Log.i("BaseObserverSubscriber", "onError():" + throwable.toString());
        if (throwable instanceof ResponseThrowable) {
            onFail((ResponseThrowable) throwable);
        } else {
            onFail(new ResponseThrowable(throwable, NetworkError.UNKNOWN));
        }
        onComplete();
    }

    /**
     * 网络请求成功
     *
     * @param t 响应结果的对象数据实体
     */
    @Override
    public void onNext(T t) {
        Log.i("BaseObserverSubscriber", "onNext()：" + t.toString());
        onSuccess(t);
    }

    /**
     * 请求或响应完结不论成功与失败
     */
    @Override
    public void onComplete() {
        Log.i("BaseObserverSubscriber", "onComplete():关闭等待进度条");
        //关闭等待进度条
        if (mDialogHelper != null) {
            mDialogHelper.dismissDialog();
        }
    }

    /**
     * 监听等待进度条取消对话框
     *
     * @param dialog 等待进度条
     */
    @Override
    public void onDialogCancelListener(AlertDialog dialog) {
        //关闭等待进度条
        if (mDialogHelper != null) {
            mDialogHelper.dismissDialog();
        }
    }
}
