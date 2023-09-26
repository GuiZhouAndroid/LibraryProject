package zsdev.work.libraryproject.service;

import android.database.Observable;


import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import zsdev.work.libraryproject.bean.LoginBean;
import zsdev.work.libraryproject.bean.My;
import zsdev.work.network.base.BaseApiService;
import zsdev.work.network.base.BaseResponse;

/**
 * Created: by 2023-09-26 18:30
 * Description:
 * Author: 张松
 */
public interface APIService extends BaseApiService {

    /**
     * 登陆
     *
     * @param username 账号
     * @param password 密码
     * @return
     */
    @FormUrlEncoded
    @POST("user/login")
    Observable<LoginBean> login(@Field("username") String username, @Field("password") String password);

    @POST("success")
    Flowable<BaseResponse<My>> login2();

}
