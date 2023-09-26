package zsdev.work.mvp.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;

import zsdev.work.dialog.normal.DialogHelper;
import zsdev.work.dialog.normal.OnDialogCancelListener;
import zsdev.work.mvp.IActivity;
import zsdev.work.mvp.IActivityInitUI;
import zsdev.work.mvp.IViewProcess;
import zsdev.work.mvp.R;
import zsdev.work.swipeback.SwipeBackActivity;
import zsdev.work.swipeback.SwipeBackLayout;
import zsdev.work.utils.ActivityManagerUtil;
import zsdev.work.utils.ImmersiveUtil;

/**
 * Created: by 2023-09-20 12:48
 * Description: 最基本的Activity，视图层V的Activity基类
 * Author: 张松
 */
public abstract class BaseActivity extends SwipeBackActivity implements IActivityInitUI, IViewProcess, IActivity, View.OnClickListener, OnDialogCancelListener {
    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 上下文
     **/
    public Context mActivityNowContext;

    /**
     * 上下文
     **/
    @SuppressLint("StaticFieldLeak")
    public static Activity mNowActivity;

    /**
     * Activity管理
     */
    private ActivityManagerUtil activityManagerUtil;

    /**
     * 标题栏
     */
    protected Toolbar mToolBar;

    /**
     * 是否初始化了toolbar
     */
    private boolean isInitToolbar = false;

    /**
     * 自定义对话框
     */
    protected DialogHelper mActivityDialogHelper;

    /**
     * 设置滑动返回
     *
     * @param isOpen true：开启左滑动 false：开启右滑动
     */
    private void setSwipeBackLayout(boolean isOpen) {
        if (isOpen) {
            getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
            Log.i(TAG, "setSwipeBackLayout: 左滑动返回");
        } else {
            getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_RIGHT);
            Log.i(TAG, "setSwipeBackLayout: 右滑动返回");
        }
    }

    /**
     * 设置沉浸式状态栏
     *
     * @param isOpen true：开启沉浸 false：禁用沉浸
     */
    private void setImmersiveStatusBar(boolean isOpen) {
        //判断是否设置了沉浸式效果
        if (isOpen) {
            ImmersiveUtil.setStatusTransparent(this);
            Log.i(TAG, "setImmersiveStatusBar: 顶部状态栏沉浸");
        } else {
            //ImmersiveUtil.setStatusTranslucent(this);
            Log.i(TAG, "setImmersiveStatusBar: 顶部状态栏非沉浸");
        }
    }

    /**
     * 设置顶部隐藏状态栏
     *
     * @param isOpen：开启隐藏 false：禁用隐藏
     */
    private void setTopHideStatus(boolean isOpen) {
        if (isOpen) {
            ImmersiveUtil.setHideStatus(this);
            Log.i(TAG, "setTopHideStatus: 顶部状态栏隐藏");
        } else {
            Log.i(TAG, "setTopHideStatus: 顶部状态栏显示");
        }
    }

    /**
     * 设置底部隐藏导航栏，上滑激活显示
     *
     * @param isOpen true：开启隐藏 false：禁用隐藏
     */
    private void setBottomNaviCation(boolean isOpen) {
        if (isOpen) {
            ImmersiveUtil.setAutoFixHideNaviCation(this);
            Log.i(TAG, "setBottomNaviCation: 底部导航栏隐藏");
        } else {
            Log.i(TAG, "setBottomNaviCation: 底部导航栏显示");
        }
    }

    /**
     * 设置全屏显示：覆盖之前 状态栏+导航栏 的沉浸设置
     *
     * @param isOpen true：状态栏+导航栏全透明 false：状态栏+导航栏非透明
     */
    private void setFullScreen(boolean isOpen) {
        if (isOpen) {
            //效果等同 ：setBottomNaviCation(true) +  setImmersiveStatusBar(true)
            ImmersiveUtil.setStatusBottomAndNavigationTransparent(this);
            Log.i(TAG, "setFullScreen: 全屏透明(状态栏+导航栏)");
        } else {
            //ImmersiveUtil.setStatusBottomAndNavigationTranslucent(this);
            Log.i(TAG, "setFullScreen: 非全屏透明");
        }
    }

    /**
     * 1.创建视图View
     * 2.界面非正常销毁退出之前，自动将Activity状态现有数据以key-value形式存入onSaveInstanceState(@NonNull Bundle outState)中保存。
     * 3.初始创建调用OnCreate()方法时会读取上次已保存的key-value数据且赋值给变量savedInstanceState，提供给当前视图View使用。
     *
     * @param savedInstanceState Activity状态的key-value数据
     */
    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        mActivityNowContext = getApplicationContext();//上下文
        mNowActivity = this;//上下文
        //创建自定义对话框
        if (mActivityDialogHelper == null) {
            mActivityDialogHelper = new DialogHelper(getNowActivity(), this);
        }
        //Activity的管理，将Activity压入栈
        activityManagerUtil = ActivityManagerUtil.getScreenManager();
        activityManagerUtil.pushActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //强制竖屏
        setSwipeBackLayout(initSwipeBackLayout());//设置滑动返回
        setImmersiveStatusBar(initImmersiveStatusBar());//设置沉浸式状态栏
        setTopHideStatus(initTopHideStatus());//设置顶部状态栏隐藏
        setBottomNaviCation(initBottomNaviCation());//设置底部导航栏隐藏
        setFullScreen(initFullScreen());//设置全屏显示
        setContentView(viewByResIdBindLayout()); //设置View布局
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isInitToolbar) {
            initToolbar();
        }
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    /**
     * 存储持久数据：DB、SP
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    /**
     * Activity活动销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        activityManagerUtil.popActivity(this);
        //释放对话框
        if (mActivityDialogHelper != null) {
            mActivityDialogHelper = null;
        }
        if (mNowActivity != null) {
            mNowActivity = null;
        }
    }

    /**
     * View单击的监听事件
     *
     * @param v 当前View对象
     */
    @Override
    public void onClick(View v) {
        viewClick(v);
    }

    /**
     * 初始化toolbar：渐变标题栏与沉浸适配
     * 如果子页面不需要初始化ToolBar，请直接覆写本方法做空操作即可
     */
    protected void initToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.base_toolbar);
        if (null != mToolBar) {
            // 设置为透明色
            mToolBar.setBackgroundColor(0x00000000);
            // 设置全透明
            mToolBar.getBackground().setAlpha(0);
            // 清除标题
            mToolBar.setTitle("");
            setSupportActionBar(mToolBar);
            // 子类中没有设置过返回按钮的情况下
            if (mToolBar.getNavigationIcon() == null) {
                //设置返回按钮
                mToolBar.setNavigationIcon(R.drawable.ic_chevron_left_write_24dp);
            }
            mToolBar.setNavigationOnClickListener(v -> finish());
            isInitToolbar = true;
            //返回文字按钮
            View navText = findViewById(R.id.toolbar_nav_text);
            if (null != navText) {
                navText.setOnClickListener(v -> finish());
            }
        }
        // appbar
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.base_appbar);
        // 状态栏高度 getStatusBarHeight只是一个获取高度的方法
        int statusBarHeight = ImmersiveUtil.getStatusBarHeight(getNowActivityContext());
        //大于 19  设置沉浸和padding
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mAppBarLayout != null) {
                ViewGroup.MarginLayoutParams appbarLayoutParam = (ViewGroup.MarginLayoutParams) mAppBarLayout.getLayoutParams();
                // 更改高度 toolbar_height 的高度是可配置的
                appbarLayoutParam.height = (int) (getResources().getDimension(R.dimen.toolbar_height) + statusBarHeight);
                // 设置padding
                mAppBarLayout.setPadding(mAppBarLayout.getPaddingLeft(), statusBarHeight, mAppBarLayout.getPaddingRight(), mAppBarLayout.getPaddingBottom());
                //重新设置回去
                mAppBarLayout.setLayoutParams(appbarLayoutParam);
            }
        }
    }

    /**
     * 启动Fragment
     *
     * @param id       id
     * @param fragment 碎片
     */
    protected void startFragment(int id, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(id, fragment);
        fragmentTransaction.commit();
    }

    /**
     * 得到Context
     *
     * @return 上下文
     */
    public Context getNowActivityContext() {
        return mActivityNowContext;
    }

    /**
     * 得到Activity
     *
     * @return 上下文
     */
    public static Activity getNowActivity() {
        return mNowActivity;
    }

    @Override
    public void onDialogCancelListener(AlertDialog dialog) {
        if (mActivityDialogHelper != null) {
            mActivityDialogHelper.dismissDialog();
        }
    }
}

