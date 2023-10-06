package zsdev.work.libraryproject.model;

import android.database.Observable;

import io.reactivex.rxjava3.core.Flowable;
import zsdev.work.lib.common.network.base.BaseResponse;
import zsdev.work.libraryproject.ServiceBuildHelper;
import zsdev.work.libraryproject.bean.LoginBean;
import zsdev.work.libraryproject.bean.My;
import zsdev.work.libraryproject.contract.MainContract;


/**
 * Created: by 2023-09-26 18:30
 * Description:
 * Author: 张松
 */
public class MainModel implements MainContract.Model {

    @Override
    public Observable<LoginBean> login(String username, String password) {
        return ServiceBuildHelper.getApiService().login(username, password);
    }

    @Override
    public Flowable<BaseResponse<My>> login2() {
        return ServiceBuildHelper.getApiService().login2();
    }

    @Override
    public void onDestroy() {

    }
}
