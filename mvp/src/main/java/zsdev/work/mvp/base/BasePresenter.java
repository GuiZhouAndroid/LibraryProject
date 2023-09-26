package zsdev.work.mvp.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import autodispose2.AutoDisposeConverter;
import lombok.Getter;
import zsdev.work.mvp.IModel;
import zsdev.work.mvp.IPresenter;
import zsdev.work.mvp.IView;
import zsdev.work.mvp.utils.RxLifecycleUtil;

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
     * P持久LifecycleOwner引用
     */
    public LifecycleOwner lifecycleOwner;

    /**
     * P层绑定生命周期后，返回AutoDispose转换器，
     * 使用时我们要借助RxJava的as/to方法进行手动绑定订阅，由转换器搭配RxJava绑定自动完成解绑订阅，
     *
     * @param <T> 泛型
     * @return AutoDispose转换器
     */
    public <T> AutoDisposeConverter<T> bindLifecycle() {
        if (null == lifecycleOwner)
            throw new NullPointerException("lifecycleOwner == null");
        return RxLifecycleUtil.bindLifecycle(lifecycleOwner);
    }
    /* ********************************* IPresenter接口中的方法 ****************************************/

    /**
     * BaseMvpActivity调用此方法将持有的LifecycleOwner引用，传递到P层进行生命周期绑定，同时addObserver()添加订阅
     *
     * @param lifecycleOwner 生命周期
     */
    @Override
    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {

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
}
