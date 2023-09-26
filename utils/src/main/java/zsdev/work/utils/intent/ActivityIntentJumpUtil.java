package zsdev.work.utils.intent;

import android.app.Activity;
import android.content.Intent;

import androidx.core.util.Pair;

import zsdev.work.utils.R;

/**
 * Created: by 2023-09-26 11:05
 * Description: Activity意图跳转工具类
 * Author: 张松
 */
public class ActivityIntentJumpUtil {
    /**
     * Activity 或 Fragment
     * 浅入深出动画
     *
     * @param intent 跳转意图
     */
    public static void startActivityAnimActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_common_anim_out, R.anim.activity_common_anim_in);
    }

    /**
     * Activity 或 Fragment
     * 深入浅出动画
     *
     * @param intent 跳转意图
     */
    public static void startActivityAnimInAndOut(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.launch_anim_fade_in, R.anim.launch_anim_fade_out);
    }

    /**
     * Activity 或 Fragment 调用
     * 左————>右：启动动画
     *
     * @param intent 跳转意图
     */
    public static void startActivityAnimLeftToRight(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
    }

    /**
     * Activity 或 Fragment 调用
     * 左————>右：启动动画
     * 请求码+返回码 ---> 传值交互
     *
     * @param intent 跳转意图
     * @param code   请求码
     */
    public static void startActivityForResultAnimLeftToRight(Activity activity, Intent intent, int code) {
        activity.startActivityForResult(intent, code);
        activity.overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
    }

    /**
     * Activity 或 Fragment 调用
     * 右————>左：启动动画
     *
     * @param intent 跳转意图
     */
    public static void startActivityAnimRightToLeft(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
    }

    /**
     * Activity 或 Fragment 调用
     * 右————>左：启动动画
     * 请求码+返回码 ---> 传值交互
     *
     * @param intent 跳转意图
     * @param code   请求码
     */
    public static void startActivityForResultAnimRightToLeft(Activity activity, Intent intent, int code) {
        activity.startActivityForResult(intent, code);
        activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
    }

    /**
     * Activity 或 Fragment 调用
     * 下————>上：启动动画 方式一
     *
     * @param intent 跳转意图
     */
    public static void startActivityAnimBottomToTop1(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.layout_down_up, R.anim.layout_center);
    }

    /**
     * Activity 或 Fragment 调用
     * 下————>上：启动动画 方式一
     * 请求码+返回码 ---> 传值交互
     *
     * @param activity 上下文
     * @param intent   跳转意图
     * @param code     请求码
     */
    public static void startActivityForResultAnimBottomToTop1(Activity activity, Intent intent, int code) {
        activity.startActivityForResult(intent, code);
        activity.overridePendingTransition(R.anim.layout_down_up, R.anim.layout_center);
    }

    /**
     * Activity 或 Fragment 调用
     * 下————>上：启动动画 方式二
     *
     * @param intent 跳转意图
     */
    public static void startActivityAnimBottomToTop2(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_bottom_out);
    }


    /**
     * Activity 或 Fragment 调用
     * 下————>上：启动动画 方式二
     * 请求码+返回码 ---> 传值交互
     *
     * @param activity 上下文
     * @param intent   跳转意图
     * @param code     请求码
     */
    public static void startActivityForResultAnimBottomToTop2(Activity activity, Intent intent, int code) {
        activity.startActivityForResult(intent, code);
        activity.overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_bottom_out);
    }

    /**
     * Activity 或 Fragment finish时调用
     * 上————>下：结束动画
     *
     * @param activity 上下文
     */
    public static void finishActivityAnimTopToBottom(Activity activity) {
        activity.overridePendingTransition(R.anim.layout_center, R.anim.layout_up_down);
        activity.finish();
    }

    /**
     * 右边划出
     */
    protected void slideLeftOut(Activity activity) {
        ActivityAnimUtil.out(activity);
    }

    /**
     * 进入
     */
    protected void slideRightIn(Activity activity) {
        ActivityAnimUtil.to(activity);
    }

    /**
     * 打开 Activity
     *
     * @param nowActivity    当前Activity
     * @param targetActivity 目标Activity
     */
    protected void launchActivity(Activity nowActivity, Class<? extends Activity> targetActivity) {
        nowActivity.startActivity(new Intent(nowActivity, targetActivity));
        // 加上动画
        slideRightIn(nowActivity);
    }

    /**
     * 打开 Activity
     *
     * @param nowActivity    当前Activity
     * @param targetActivity 目标Activity
     * @param requestCode    请求码
     */
    protected void launchActivityForResult(Activity nowActivity, Class<? extends Activity> targetActivity, int requestCode) {
        nowActivity.startActivityForResult(new Intent(nowActivity, targetActivity), requestCode);
        // 加上动画
        slideRightIn(nowActivity);
    }

    /**
     * 打开新的 Activity
     *
     * @param nowActivity    当前Activity
     * @param targetActivity 目标Activity
     * @param pairs          键值对
     */
    protected void launchActivity(Activity nowActivity, Class<? extends Activity> targetActivity, Pair<String, Object>... pairs) {
        Intent intent = new Intent(nowActivity, targetActivity);
        // 填充数据
        IntentUtil.fillIntent(intent, pairs);
        nowActivity.startActivity(intent);
        // 加上动画
        slideRightIn(nowActivity);
    }

    /**
     * @param nowActivity    当前Activity
     * @param targetActivity 目标Activity
     * @param requestCode    请求码
     * @param pairs          键值对
     */
    protected void launchActivityForResult(Activity nowActivity, Class<? extends Activity> targetActivity, int requestCode, Pair<String, Object>... pairs) {
        Intent intent = new Intent(nowActivity, targetActivity);
        // 填充数据
        IntentUtil.fillIntent(intent, pairs);
        nowActivity.startActivityForResult(intent, requestCode);
        // 加上动画
        slideRightIn(nowActivity);
    }
// 调用
//    //普通
//    launchActivity(DialogExampleActivity.class);
//
//    //普通携带参数
//    launchActivity(ToastExampleActivityActivity.class,
//                new Pair<String, Object>("key1", "value1"),
//                new Pair<String, Object>("key1", "value1"));
//
//    //返回值
//    launchActivityForResult(LoginActivity.class,200);
//
//    // 返回值携带参数
//    launchActivityForResult(LoginActivity.class,
//			200,
//                    new Pair<String, Object>("key1", "value1"),
//			new Pair<String, Object>("key1", "value1"));

}
