package zsdev.work.lib.common.utils;

import androidx.lifecycle.LifecycleOwner;

import autodispose2.AutoDispose;
import autodispose2.AutoDisposeConverter;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;


/**
 * Created: by 2023-09-20 12:50
 * Description: 生命周期工具类，用于解决P层因订阅未及时取消V层引用，引起的内存泄漏。此方式优于RxLifecycle
 * Author: 张松
 */
public class RxLifecycleUtil {

    private RxLifecycleUtil() {
        throw new IllegalStateException("Can't instance the RxLifecycleUtils");
    }

    public static <T> AutoDisposeConverter<T> bindLifecycle(LifecycleOwner lifecycleOwner) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)
        );
    }
}
