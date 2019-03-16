package com.internal.demo.android.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.internal.demo.android.utility.SharedPerfUtils;
import com.internal.mediasdk.MediaEngineSDK;
import com.internal.mediasdk.ParametersNames;
import com.internal.mediasdk.SysEventType;


public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    protected final int LOGIN_ACCOUNT_SUCCESS = 0;
    protected final int LOGIN_ACCOUNT_FAILED = 1;
    protected final int LOGIN_ACCOUNT_PASS_LEN_ERROR = 2;
    protected final int LOGIN_APP_ACCOUNT_LEN_ERROR = 3;
    protected final int LOGIN_SERVER_LEN_ERROR = 4;
    protected final int QUERY_APP_ACCOUNT_SUCCESS = 5;
    protected final int QUERY_APP_ACCOUNT_FAILED = 6;

    protected ProgressDialog progressDialog;

    Intent intent = null;

    protected void AppLoginAppAccount(final String hostServer, final String hostPort, final String appAccountId, final Handler handler)
    {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String AccountId = "";

                MediaEngineSDK.getInstance().setParameter(ParametersNames.KEY_ROOT_CS,hostServer);
                MediaEngineSDK.getInstance().setParameter(ParametersNames.KEY_ROOT_CS_PORT,hostPort);

                AccountId = MediaEngineSDK.getInstance().bindAppAccount(appAccountId);

                if(AccountId.length() == 8)
                {
                    Log.e(TAG, "用户登陆成功,存储至本地APP数据");
                    SharedPerfUtils.setServerHost(BaseActivity.this, hostServer);
                    SharedPerfUtils.setServerPort(BaseActivity.this, hostPort);
                    SharedPerfUtils.setAccountId(BaseActivity.this, AccountId);
                    SharedPerfUtils.setAppAccountId(BaseActivity.this, appAccountId);
                    handler.sendEmptyMessage(LOGIN_ACCOUNT_SUCCESS);
                }
                else if(appAccountId.length() <= 0 || appAccountId.length() > 40)
                {
                    Log.e(TAG, "用户账户长度1-40位");
                    handler.sendEmptyMessage(LOGIN_APP_ACCOUNT_LEN_ERROR);
                }
                else if(hostServer.length() <= 0 || hostPort.length() <= 0)
                {
                    Log.e(TAG, "服务器地址或端口号输入不能为空");
                    handler.sendEmptyMessage(LOGIN_SERVER_LEN_ERROR);
                }
                else
                {
                    Log.e(TAG, "用户登陆失败...");
                    handler.sendEmptyMessage(LOGIN_ACCOUNT_FAILED);
                }
            }
        }).start();
    }

    protected void queryIdByAppAccount(final String appAccountId, final Handler handler) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String id = MediaEngineSDK.getInstance().queryIdByAppAccount(appAccountId);

                if (id != null && id.length() > 0)
                {
                    Message msg = handler.obtainMessage();
                    msg.what = QUERY_APP_ACCOUNT_SUCCESS;
                    msg.obj = id;
                    handler.sendMessage(msg);
                }
                else
                {
                    handler.sendEmptyMessage(QUERY_APP_ACCOUNT_FAILED);
                }
            }
        }).start();
    }

    protected void showToastMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        if (message != null) {
            progressDialog.setMessage(message);
        }else{
            progressDialog.setMessage("正在处理，请稍后...");
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    protected void enterMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    protected void enterLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    private AppSimpleListener appSimpleListener = new AppSimpleListener()
    {
        @Override
        public void onSystemEvent(int eventType) {
            switch (eventType)
            {
                case SysEventType.SYS_EVENT_KICKOUT:
                {
                    finish();
                    Intent intent  = new Intent(getApplicationContext(),LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                    Message msg = new Message();
                    msg.what = SysEventType.SYS_EVENT_KICKOUT;
                    mHandler.sendMessage(msg);
                    SharedPerfUtils.clear(getApplicationContext());
                }
                break;
                case SysEventType.SYS_EVENT_DISCONNECT:
                {
                    Message msg = new Message();
                    msg.what = SysEventType.SYS_EVENT_DISCONNECT;
                    mHandler.sendMessage(msg);
                }
                break;
                case SysEventType.SYS_EVENT_RECONNECT:
                {
                    Message msg = new Message();
                    msg.what = SysEventType.SYS_EVENT_RECONNECT;
                    mHandler.sendMessage(msg);
                }
                break;
                default:
                    break;
            }
        }
    };

    private Handler mHandler = new Handler(){

        public void handleMessage(Message msg) {
            switch(msg.what)
            {
                case SysEventType.SYS_EVENT_KICKOUT:
                    showToastMessage("此用户已在其他设备登陆,自动退出");
                    break;
                case SysEventType.SYS_EVENT_DISCONNECT:
                    showToastMessage("网络不可用,请检查网络链接");
                    break;
                case SysEventType.SYS_EVENT_RECONNECT:
                    showToastMessage("网络链接已重新链接");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).registerEventListener(appSimpleListener);
    }
}
