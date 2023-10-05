package zsdev.work.lib.common.widget.swipeback;

/**
 * Created: by 2023-09-20 12:37
 * Description:
 * Author: 张松
 */
public interface SwipeBackActivityBase {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    public abstract SwipeBackLayout getSwipeBackLayout();

    public abstract void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    public abstract void scrollToFinishActivity();
}
