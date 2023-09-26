package zsdev.work.dialog.custom;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;


import zsdev.work.dialog.R;
import zsdev.work.dialog.custom.loadingdrawable.LoadingRendererFactory;
import zsdev.work.dialog.custom.loadingdrawable.LoadingView;
import zsdev.work.dialog.custom.loadingview.LVBlazeWood;
import zsdev.work.dialog.custom.loadingview.LVBlock;
import zsdev.work.dialog.custom.loadingview.LVCircular;
import zsdev.work.dialog.custom.loadingview.LVCircularSmile;

/**
 * Created: by 2023-09-13 22:28
 * Description: AlertDialog绑定自定义等待框View
 * Author: 张松
 */
public class DialogCustomView {

    /**
     * 水瓶
     *
     * @param activity          上下文
     * @param loadingRendererId LoadingRenderer的id
     * @return 等待框
     */
    public static Dialog setModeLoadingDialogRendererId(Activity activity, int loadingRendererId) {
        // 创建一个新的RelativeLayout
        RelativeLayout relativeLayout = new RelativeLayout(activity);

        // 定义RelativeLayout布局属性
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                400,
                400);

        //设置Dialog布局属性
        LoadingView loadingView = new LoadingView(activity);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        loadingView.setLayoutParams(layoutParams);
        try {
            loadingView.setLoadingRenderer(LoadingRendererFactory.createLoadingRenderer(activity, loadingRendererId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        relativeLayout.addView(loadingView);
        //Builder的第二个参数是系统的style 或者 自定义的style,可以用来显示自己想要的风格，同时还可以避免黑框问题

        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.DialogNoBg)
                .setView(relativeLayout)
                .create();

        //设置昏暗度为0
        Window window = alertDialog.getWindow();
        window.setDimAmount(0);
        return alertDialog;
    }

    /* ********** todo :仅使用部分，待补充 https://github.com/ldoublem/LoadingView ***************/

    /**
     * 柴火等待框
     *
     * @param activity 上下文
     * @return 等待框
     */
    public static Dialog setFirewoodLoadingDialog(Activity activity) {
        // 获取RelativeLayout对象的引用
        RelativeLayout relativeLayout = new RelativeLayout(activity);

        // 创建一个新的布局参数对象
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(220, 220);
        // 添加规则使子控件居中
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        //设置Dialog自定义View引用
        LVBlazeWood lvBlazeWood = new LVBlazeWood(activity);
        lvBlazeWood.startAnim();
        lvBlazeWood.setLayoutParams(layoutParams);
        relativeLayout.addView(lvBlazeWood);

        //Builder的第二个参数是系统的style 或者 自定义的style,可以用来显示自己想要的风格，同时还可以避免黑框问题
        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.DialogNoBg)
                .setView(relativeLayout)
                .create();
        alertDialog.show();
        //设置昏暗度为0
        Window window = alertDialog.getWindow();

        window.setDimAmount(0);
        return alertDialog;
    }

    /**
     * 太阳等待框
     *
     * @param activity 上下文
     * @return 等待框
     */
    public static AlertDialog setSunLoadingDialog(Activity activity) {
        // 获取RelativeLayout对象的引用
        RelativeLayout relativeLayout = new RelativeLayout(activity);

        // 创建一个新的布局参数对象
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
        // 添加规则使子控件居中
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        //设置Dialog自定义View引用
        LVCircular lvCircular = new LVCircular(activity);
        //lvCircular.setRoundColor();
        //lvCircular.setViewColor();
        lvCircular.startAnim();
        lvCircular.setLayoutParams(layoutParams);
        relativeLayout.addView(lvCircular);

        //Builder的第二个参数是系统的style 或者 自定义的style,可以用来显示自己想要的风格，同时还可以避免黑框问题
        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.DialogNoBg)
                .setView(relativeLayout)
                .create();
        alertDialog.show();
        //设置昏暗度为0
        Window window = alertDialog.getWindow();

        window.setDimAmount(0);
        return alertDialog;
    }

    /**
     * 方块等待框
     *
     * @param activity 上下文
     * @return 等待框
     */
    public static AlertDialog setBlockLoadingDialog(Activity activity) {
        // 获取RelativeLayout对象的引用
        RelativeLayout relativeLayout = new RelativeLayout(activity);

        // 创建一个新的布局参数对象
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
        // 添加规则使子控件居中
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        //设置Dialog自定义View引用
        LVBlock lvBlock = new LVBlock(activity);
        //lvBlock.setViewColor();
        //lvBlock.isShadow();
        //lvBlock.setShadowColor();
        lvBlock.startAnim();
        lvBlock.setLayoutParams(layoutParams);
        relativeLayout.addView(lvBlock);

        //Builder的第二个参数是系统的style 或者 自定义的style,可以用来显示自己想要的风格，同时还可以避免黑框问题
        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.DialogNoBg)
                .setView(relativeLayout)
                .create();
        alertDialog.show();
        //设置昏暗度为0
        Window window = alertDialog.getWindow();

        window.setDimAmount(0);
        return alertDialog;
    }

    /**
     * 圆圈笑脸等待框
     *
     * @param activity 上下文
     * @return 等待框
     */
    public static AlertDialog setCircularSmileLoadingDialog(Activity activity) {
        // 获取RelativeLayout对象的引用
        RelativeLayout relativeLayout = new RelativeLayout(activity);

        // 创建一个新的布局参数对象
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
        // 添加规则使子控件居中
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        //设置Dialog自定义View引用
        LVCircularSmile circularSmile = new LVCircularSmile(activity);
        //lvBlock.setViewColor();
        circularSmile.startAnim();
        circularSmile.setLayoutParams(layoutParams);
        relativeLayout.addView(circularSmile);

        //Builder的第二个参数是系统的style 或者 自定义的style,可以用来显示自己想要的风格，同时还可以避免黑框问题
        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.DialogNoBg)
                .setView(relativeLayout)
                .create();
        alertDialog.show();
        //设置昏暗度为0
        Window window = alertDialog.getWindow();

        window.setDimAmount(0);
        return alertDialog;
    }
}
