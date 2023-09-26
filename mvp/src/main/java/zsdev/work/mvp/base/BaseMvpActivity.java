package zsdev.work.mvp.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import zsdev.work.mvp.IPresenter;

/**
 * Created: by 2023-09-20 12:49
 * Description: MVP活动的基类，封装好了presenter的相关操作
 * 在Activity基类的子类中持有 P引用（BasePresenter）与 V引用（IBaseView）的目的是为了使用rxLifecycle2自动管理维护P和V订阅引用，防止内存泄漏 + 内存溢出
 * Author: 张松
 */
public abstract class BaseMvpActivity<P extends IPresenter, VB extends ViewDataBinding> extends BaseDialogActivity {
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
            //视图层选择绑定的控制层
            mPresenter = createPresenter();
        }
        return mPresenter;
    }

    /**
     * 1.创建视图View
     * 2.界面非正常销毁退出之前，自动将Activity状态现有数据以key-value形式存入onSaveInstanceState(@NonNull Bundle outState)中保存。
     * 3.初始创建调用OnCreate()方法时会读取上次已保存的key-value数据且赋值给变量savedInstanceState，提供给当前视图View使用。
     *
     * @param savedInstanceState Activity状态的key-value数据
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vb = DataBindingUtil.setContentView(this, viewByResIdBindLayout());//将布局id关联DataBinding
        initPrepareData(); //初始化准备数据
        setListener(); //监听事件
        doViewBusiness(); //View业务
    }

    /**
     * Activity活动销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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

