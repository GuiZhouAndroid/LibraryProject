package zsdev.work.lib.common.base.mvp;

/**
 * Created: by 2023-09-20 11:01
 * Description: 数据层Model顶层业务接口作为扩展使用
 * Author: 张松
 */
public interface IModel {
    /**
     * 销毁时需要处理业务数据
     */
    void onDestroy();
}
