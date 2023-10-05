package zsdev.work.lib.common.base.mvp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;



/**
 * Created: by 2023-09-20 12:50
 * Description: MVP碎片的基类，封装好了presenter的相关操作
 * 在Fragment基类中持有 P引用（BasePresenter）与 V引用（IBaseView）的目的是为了使用AutoDispose2自动管理维护P和V订阅引用，防止内存泄漏 + 内存溢出
 * Author: 张松
 */
public abstract class BaseMvpFragment<P extends IPresenter, VB extends ViewDataBinding> extends BaseDialogFragment {
    /**
     * V持有的P引用
     */
    protected P mPresenter;

    /**
     * 继承DataBinding的子类
     */
    protected VB vb;

    /**
     * 创建Presenter
     *
     * @return 返回Presenter的实例
     */
    protected abstract P createPresenter();

    /**
     * 底层获取P
     *
     * @return P
     */
    protected synchronized P getPresenter() {
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        return mPresenter;
    }

    /**
     * 运行在onCreate之后，生成View视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //使用父类的布局View
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 在onCreateView（LayoutInflater、ViewGroup、Bundle）返回之后立即调用，
     * 但在将任何保存的状态还原到视图之前调用。这给了子类一个机会，一旦它们知道自己
     * 的视图层次结构已经完全创建，就可以对自己进行初始化。然而，片段的视图层次结构此时并未附加到其父级。
     *
     * @param view               当前View对象
     * @param savedInstanceState Bundle对象
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vb = DataBindingUtil.bind(view);
        initLifecycleObserver(getLifecycle());//初始化生命周期
        initPrepareData();//初始化准备数据
        setListener(); //监听事件
        doViewBusiness(); //View业务
    }

    /**
     * 初始化生命周期
     * 订阅绑定：addObserver()
     * 订阅解绑：将当前Lifecycle引用传递到P层，提供给P层绑定使用
     *
     * @param lifecycle 生命周期引用
     */
    @CallSuper //表示任何重写方法也应该调用此方法
    @MainThread //表示只应在主线程上调用带注释的方法。如果带注释的元素是一个类，那么该类中的所有方法都应该在主线程上调用
    protected void initLifecycleObserver(@NonNull Lifecycle lifecycle) {
        getPresenter().setLifecycleOwner(this);
        lifecycle.addObserver(getPresenter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 取消弹窗按钮进行解绑Presenter引用 + + rxjava取消订阅
     *
     * @param dialog 被取消的弹窗
     */
    @Override
    public void onDialogCancelListener(AlertDialog dialog) {
        super.onDialogCancelListener(dialog);
    }
}

