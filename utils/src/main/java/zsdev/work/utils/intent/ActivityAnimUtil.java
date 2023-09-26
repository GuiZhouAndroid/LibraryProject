package zsdev.work.utils.intent;

import android.app.Activity;

import zsdev.work.utils.R;

/**
 * Created: by 2023-09-26 11:05
 * Description: Activity的跳转动画
 * Author: 张松
 */
public class ActivityAnimUtil {

    /**
     * 跳转到
     *
     * @param activity activity
     */
    public static void to(Activity activity) {
        activity.overridePendingTransition(R.anim.setup_next_in, R.anim.setup_next_out);

    }

    /**
     * 退出动画
     *
     * @param activity activity
     */
    public static void out(Activity activity) {
        activity.overridePendingTransition(R.anim.setup_pre_in, R.anim.setup_pre_out);
    }
}
