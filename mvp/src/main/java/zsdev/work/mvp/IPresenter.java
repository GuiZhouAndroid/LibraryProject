package zsdev.work.mvp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * Created: by 2023-09-20 11:03
 * Description: P层顶层业务接口
 * Author: 张松
 */
public interface IPresenter extends DefaultLifecycleObserver {

    void setLifecycleOwner(LifecycleOwner lifecycleOwner);

    @Override
    default void onCreate(@NonNull LifecycleOwner owner) {
        Log.i("IPresenter", "onCreate: ");
    }

    @Override
    default void onStart(@NonNull LifecycleOwner owner) {
        Log.i("IPresenter", "onStart: ");
    }

    @Override
    default void onResume(@NonNull LifecycleOwner owner) {
        Log.i("IPresenter", "onResume: ");
    }

    @Override
    default void onPause(@NonNull LifecycleOwner owner) {
        Log.i("IPresenter", "onPause: ");
    }

    @Override
    default void onStop(@NonNull LifecycleOwner owner) {
        Log.i("IPresenter", "onStop: ");
    }

    @Override
    default void onDestroy(@NonNull LifecycleOwner owner) {
        Log.i("IPresenter", "onDestroy: ");
    }
}
