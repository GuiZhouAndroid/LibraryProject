package zsdev.work.lib.common.base.mvp;

/**
 * Created: by 2023-09-20 10:58
 * Description: 全部Activity/Fragment上顶层通用UI业务接口。
 * Author: 张松
 */
public interface IView {
    /**
     * 吐司消息
     *
     * @param msg 消息文本
     */
    default void showToastUI(String msg) {
    }

    /**
     * Model通过网络请求服务器接口响应成功后借助Presenter层传递到View层的用户数据
     *
     * @param successData 响应成功数据
     */
    default void onSuccessNetUI(String successData) {
    }

    /**
     * 显示网络请求错误消息
     *
     * @param errMsg 错误消息
     */
    default void showErrorNetUI(String errMsg) {
    }

    /**
     * 是否显示请求网络等待框
     *
     * @param isShow true：显示 false：隐藏
     */
    default void isShowProgressNetUI(boolean isShow) {
    }

    /**
     * 切换夜间模式
     *
     * @param isOpen true：开启 false：禁用
     */
    default void isNightModeUI(boolean isOpen) {
    }

    /**
     * 显示消息对话框
     *
     * @param msg        提示文本
     * @param cancelable ture：开启框外点击可关闭对话框 false：框外点击不可关闭对话框
     */
    default void showDialogUI(String msg, boolean cancelable) {
    }

    /**
     * 关闭消息对话框
     */
    default void dismissDialogUI() {
    }
}
