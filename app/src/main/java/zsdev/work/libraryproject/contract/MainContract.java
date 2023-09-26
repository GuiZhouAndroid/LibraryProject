package zsdev.work.libraryproject.contract;

import android.database.Observable;

import io.reactivex.rxjava3.core.Flowable;
import zsdev.work.libraryproject.bean.LoginBean;
import zsdev.work.libraryproject.bean.My;
import zsdev.work.mvp.IModel;
import zsdev.work.mvp.IPresenter;
import zsdev.work.mvp.IView;
import zsdev.work.network.base.BaseResponse;

/**
 * Created: by 2023-09-26 18:29
 * Description:
 * Author: 张松
 */
public interface MainContract {
    /**
     * 创建对应的联网请求的方法，将Presenter提交的字段放到联网请求中，发送给服务器
     */
    interface Model extends IModel {
        Observable<LoginBean> login(String username, String password);

        Flowable<BaseResponse<My>> login2();
    }

    /**
     * 创建在界面上显示加载中、取消加载以及登陆成功、失败的方法
     */
    interface View extends IView {

        /**
         * 登陆成功
         *
         * @param responseObject 成功回调的信息
         */
        void loginSuccess(My responseObject);

        /**
         * 登陆失败
         *
         * @param message 失败消息
         */
        void loginFailure(String message);
    }

    interface Presenter extends IPresenter {
        /**
         * 登陆
         *
         * @param username
         * @param password
         */
        public abstract void login(String username, String password);

        /**
         * 登陆
         */
        public abstract void login2();
    }
}

