package zsdev.work.lib.common.widget.dialog.custom.loadingdrawable;

import android.content.Context;

/**
 * Created: by 2023-09-12 21:52
 * Description:
 * Author: 张松
 */
public class DensityUtil {

    public static float dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }
}
