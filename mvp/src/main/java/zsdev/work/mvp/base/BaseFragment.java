package zsdev.work.mvp.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import zsdev.work.dialog.normal.DialogHelper;
import zsdev.work.dialog.normal.OnDialogCancelListener;
import zsdev.work.mvp.IFragment;
import zsdev.work.mvp.IViewProcess;

/**
 * Created: by 2023-09-20 12:49
 * Description: 最基本的Fragment，视图层V的Fragment基类。
 * Author: 张松
 */
public abstract class BaseFragment extends Fragment implements IViewProcess, IFragment, View.OnClickListener, OnDialogCancelListener {

    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 视图绑定的View实例
     */
    protected View viewFragment;

    /**
     * 当前Fragment所绑定的Activity实例
     */
    protected Activity mNowFragmentOfActivity;

    /**
     * 自定义对话框
     */
    protected DialogHelper mFragmentDialogHelper;

    /**
     * 绑定activity
     * Fragment和Activity相关联时调用。可以通过该方法获取Activity引用，还可以通过getArguments()获取参数
     *
     * @param context 上下文
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");
    }

    /**
     * 运行在onAttach之后，可以接收别人传递过来的参数
     * Fragment被创建时调用
     *
     * @param savedInstanceState Bundle数据
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNowFragmentOfActivity = getActivity();
        Log.d(TAG, "onCreate()");
    }

    /**
     * 运行在onCreate之后，生成View视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewFragment = inflater.inflate(viewByResIdBindLayout(), container, false);//初始化布局
        return viewFragment;
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
     * 当Activity完成onCreate()时调用
     *
     * @param savedInstanceState Bundle数据
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
    }

    /**
     * 在onCreateView（LayoutInflater、ViewGroup、Bundle）返回之后立即调用，
     * 但在将任何保存的状态还原到视图之前调用。这给了子类一个机会，一旦它们知道自己
     * 的视图层次结构已经完全创建，就可以对自己进行初始化。然而，片段的视图层次结构此时并未附加到其父级。
     *
     * @param view               当前View对象
     * @param savedInstanceState Bundle对象
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()");
        //创建自定义对话框
        if (mFragmentDialogHelper == null) {
            mFragmentDialogHelper = new DialogHelper(getFragmentOfActivity(), this);
        }
    }

    /**
     * 当Fragment可见时调用
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    /**
     * 当Fragment可见且可交互时调用
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    /**
     * 当Fragment不可交互但可见时调用
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    /**
     * 当Fragment不可见时调用
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    /**
     * 当Fragment的UI从视图结构中移除时调用
     */
    @Override
    public void onDestroyView() {
        this.viewFragment = null;
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
        //释放对话框
        if (mFragmentDialogHelper != null) {
            mFragmentDialogHelper = null;
        }
    }

    /**
     * 销毁Fragment时调用
     */
    @Override
    public void onDestroy() {
        this.viewFragment = null;
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        //释放对话框
        if (mFragmentDialogHelper != null) {
            mFragmentDialogHelper = null;
        }
    }

    /**
     * 当Fragment和Activity解除关联时调用
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }

    /**
     * 跳转Fragment
     *
     * @param toFragment 跳转到的fragment
     * @param tag        fragment的标签
     */
    public void startFragment(Fragment toFragment, String tag) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.hide(this).add(android.R.id.content, toFragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 跳转Fragment
     *
     * @param toFragment 跳转去的fragment
     */
    public void startFragment(Fragment toFragment) {
        Log.d(TAG, this + "——>已成功跳转——>" + toFragment.getClass());
        startFragment(toFragment, null);
    }

    /**
     * fragment进行回退
     * 类似于activity的OnBackPress
     */
    public void onFragmentBack() {
        getFragmentManager().popBackStack();
    }

    /**
     * 得到Context
     *
     * @return 上下文
     */
    public Context getNowFragmentContext() {
        return getContext();
    }

    /**
     * 得到fragment
     *
     * @return fragment
     */
    public Fragment getNowFragment() {
        return this;
    }

    /**
     * 得到当前Fragment所绑定的Activity实例
     *
     * @return Activity
     */
    public Activity getFragmentOfActivity() {
        return mNowFragmentOfActivity;
    }
}

