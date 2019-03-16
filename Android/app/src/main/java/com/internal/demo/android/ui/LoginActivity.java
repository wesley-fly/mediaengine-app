package com.internal.demo.android.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.internal.demo.R;

public class LoginActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText editTextHost = findViewById(R.id.et_server);
        final EditText editTextPort = findViewById(R.id.et_server_port);
        final EditText editTextAccount = findViewById(R.id.et_account);

        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg){
                hideProgressDialog();
                if (msg.what == LOGIN_ACCOUNT_SUCCESS)
                {
                    enterMainActivity();
                }
                else if(msg.what == LOGIN_ACCOUNT_PASS_LEN_ERROR)
                {
                    showToastMessage("密码长度为6-20位，请重新输入！");
                }
                else if(msg.what == LOGIN_APP_ACCOUNT_LEN_ERROR)
                {
                    showToastMessage("用户账户长度1-40位，请重新输入！");
                }
                else if(msg.what == LOGIN_SERVER_LEN_ERROR)
                {
                    showToastMessage("服务器地址或端口号输入不能为空，请重新输入！");
                }
                else
                {
                    showToastMessage("登录失败（该账号对应的身份ID为空）");
                }
            }
        };

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hostServer = editTextHost.getText().toString().trim();
                String hostPort = editTextPort.getText().toString().trim();
                String appAccountId = editTextAccount.getText().toString().trim();

                if (!TextUtils.isEmpty(appAccountId)) {
                    showProgressDialog("登录中...");
                    AppLoginAppAccount(hostServer, hostPort, appAccountId, handler);
                }

            }
        });
    }
}
