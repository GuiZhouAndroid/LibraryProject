package zsdev.work.lib.common.widget.dialog.normal;

import androidx.appcompat.app.AlertDialog;

/**
 * Created: by 2023-08-12 00:00
 * Description: 确认按钮点击的回调
 * Author: 张松
 */
public interface OnDialogConfirmListener {
    /**
     * 确定按钮点击的回调
     *
     * @param dialog 弹窗
     */
    default void onDialogConfirmListener(AlertDialog dialog) {

    }
}
