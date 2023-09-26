package zsdev.work.dialog.normal;

import androidx.appcompat.app.AlertDialog;

/**
 * Created: by 2023-08-12 00:01
 * Description: 取消按钮点击的回调
 * Author: 张松
 */
public interface OnDialogCancelListener {
    /**
     * 取消按钮点击的回调
     *
     * @param dialog 弹窗
     */
    default void onDialogCancelListener(AlertDialog dialog) {
    }
}
