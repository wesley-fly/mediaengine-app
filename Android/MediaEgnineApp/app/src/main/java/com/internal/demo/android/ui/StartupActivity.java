package com.internal.demo.android.ui;

import android.os.Bundle;
import android.view.WindowManager;

import com.internal.demo.R;
import com.internal.demo.android.utility.SharedPerfUtils;

public class StartupActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_startup);

        final String accountId = SharedPerfUtils.getAccountId(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);

                    if(accountId != null)
                    {
                        enterMainActivity();
                    }
                    else
                    {
                        enterLoginActivity();
                    }
                }
                catch (Exception e)
                {
                }
            }
        }).start();
    }
}
