package zsdev.work.libraryproject.presenter;

import zsdev.work.libraryproject.App;
import zsdev.work.libraryproject.bean.My;
import zsdev.work.libraryproject.contract.MainContract;
import zsdev.work.libraryproject.model.MainModel;
import zsdev.work.mvp.base.BaseActivity;
import zsdev.work.mvp.base.BasePresenter;
import zsdev.work.network.base.BaseFlowableSubscriber;
import zsdev.work.network.exception.ResponseThrowable;
import zsdev.work.network.rxjava.transformer.HandlerTransformer;

/**
 * Created: by 2023-09-26 18:31
 * Description:
 * Author: 张松
 */
public class MainPresenter extends BasePresenter<MainContract.View, MainContract.Model> implements MainContract.Presenter {

    MainModel mainModel = new MainModel();

    public MainPresenter(MainContract.View nowView) {
        super(nowView);
    }

    public MainPresenter(MainContract.View nowView, MainContract.Model nowModel) {
        super(nowView, nowModel);
    }

    @Override
    public void login(String username, String password) {

    }

    /**
     * 实现MainContract.Presenter 接口中的 login(String username, String password) 方法
     */
    @Override
    public void login2() {
        //循环发送数字 测试内存泄漏
//        mainModel.login2().interval(1, TimeUnit.SECONDS).compose(SchedulerTransformer.getFlowableScheduler()).to(bindLifecycle()).subscribe(new DisposableSubscriber<Long>() {
//            @Override
//            public void onNext(Long aLong) {
//                System.out.println("aLong = " + aLong);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

        mainModel.login2()
                .compose(HandlerTransformer.getFlowableTransformerScheduler())
                .to(bindLifecycle()).subscribe(new BaseFlowableSubscriber<My>(App.getContext(), BaseActivity.getNowActivity(),9) {
                    @Override
                    public void onFail(ResponseThrowable responseThrowable) {
                        nowView.loginFailure(responseThrowable.getMessage());
                    }

                    @Override
                    public void onSuccess(My my) {
                        // nowView.loginSuccess(my);
                        nowView.onSuccessNetUI(my.getName());
                    }
                });

    }
}
