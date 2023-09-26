package zsdev.work.swipeback;

import android.os.Bundle;
import android.view.View;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Created: by 2023-09-20 12:36
 * Description: RxLifecycle2 是一个用于管理 RxJava 订阅的生命周期库，它帮助你在 Android 应用中处理订阅的生命周期。
 * 而 AndroidX 是 Android 官方推出的支持库，提供了一组用于向后兼容 Android 平台的库。所以，RxLifecycle2 和 AndroidX 可以一起使用，来处理 RxJava 订阅的生命周期管理
 * Author: 张松
 */
public class SwipeBackActivity extends RxAppCompatActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        T v = super.findViewById(id);
        if (v == null && mHelper != null)
            return (T) mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        SwipeBackActivityUtil.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
