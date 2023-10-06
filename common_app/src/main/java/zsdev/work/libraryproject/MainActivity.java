package zsdev.work.libraryproject;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import zsdev.work.common_app.R;
import zsdev.work.common_app.databinding.ActivityMainBinding;
import zsdev.work.lib.common.base.mvp.BaseMvpActivity;
import zsdev.work.libraryproject.bean.My;
import zsdev.work.libraryproject.contract.MainContract;
import zsdev.work.libraryproject.presenter.MainPresenter;


public class MainActivity extends BaseMvpActivity<MainPresenter, ActivityMainBinding> implements MainContract.View {

    TextInputEditText etUsernameLogin;
    TextInputEditText etPasswordLogin;
    Button btnSigninLogin;


    @Override
    public boolean initNetworkStateListener() {
        return true;
    }

    @Override
    public void onSuccessNetUI(String successData) {
        showSuccessDialog(successData);
    }

    @Override
    public int viewByResIdBindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public boolean initSwipeBackLayout() {
        return true;
    }

    @Override
    public boolean initImmersiveStatusBar() {
        return true;
    }

    @Override
    public void loginSuccess(My responseObject) {
        showSuccessDialog(responseObject.getName());
    }

    @Override
    public void loginFailure(String message) {
        Log.i(TAG, "loginFailure: " + message);
        showErrorDialog(message);
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }


    @Override
    public void doViewBusiness() {
        initView();
    }

    public void initView() {
        etUsernameLogin = (TextInputEditText) findViewById(R.id.et_username_login);
        etPasswordLogin = (TextInputEditText) findViewById(R.id.et_password_login);
        btnSigninLogin = (Button) findViewById(R.id.btn_signin_login);
        btnSigninLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getUsername().isEmpty() || getPassword().isEmpty()) {
                    Toast.makeText(MainActivity.this, "帐号密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                getPresenter().login2();
            }
        });

    }

    /**
     * @return 帐号
     */
    private String getUsername() {
        return etUsernameLogin.getText().toString().trim();
    }

    /**
     * @return 密码
     */
    private String getPassword() {
        return etPasswordLogin.getText().toString().trim();
    }
}