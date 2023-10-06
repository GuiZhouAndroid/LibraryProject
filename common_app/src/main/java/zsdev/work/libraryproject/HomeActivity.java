package zsdev.work.libraryproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import zsdev.work.common_app.R;
import zsdev.work.lib.common.base.mvp.BaseActivity;


/**
 * Created: by 2023-09-26 17:30
 * Description:
 * Author: 张松
 */
public class HomeActivity extends BaseActivity {


    @Override
    public int viewByResIdBindLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btn_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
            }
        });
    }
}
