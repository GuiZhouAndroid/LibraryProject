package zsdev.work.lib.common.base.mvp;

import androidx.multidex.MultiDexApplication;

/**
 * Created: by 2023-09-20 12:49
 * Description:业务组件在集成模式下是不能有自己的Application的，但在组件开发模式下又必须实现自己的Application
 * 并且要继承自Common组件的BaseApplication并且这个Application不能被业务组件中的代码引用，因为它的功能就是为了使业务组件从BaseApplication中获取的全局Context生效，还有初始化数据之用。
 * Author: 张松
 */
public class BaseApplication extends MultiDexApplication {
    //全局的Application对象
    public static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    public static BaseApplication getInstance() {
        return baseApplication;
    }
}
