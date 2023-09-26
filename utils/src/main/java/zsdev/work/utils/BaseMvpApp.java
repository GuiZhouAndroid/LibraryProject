package zsdev.work.utils;

import android.app.Application;

/**
 * Created: by 2023-08-10 09:33
 * Description:
 * Author: 张松
 */
public class BaseMvpApp extends Application {
    //全局的Application对象
    public static BaseMvpApp baseMvpApp;

    @Override
    public void onCreate() {
        super.onCreate();
        baseMvpApp = this;
    }

    public static BaseMvpApp getInstance() {
        return baseMvpApp;
    }
}
