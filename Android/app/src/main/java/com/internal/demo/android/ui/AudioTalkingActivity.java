package com.internal.demo.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.internal.demo.R;
import com.internal.demo.android.utility.CallAudioHelper;
import com.internal.mediasdk.CallState;
import com.internal.mediasdk.MediaEngineSDK;
import com.internal.mediasdk.RecCallType;

public class AudioTalkingActivity extends BaseActivity {
    private static final String TAG = AudioTalkingActivity.class.getSimpleName();

    private String m_callId;

    private String m_dstId;

    private int is_callee = 0;

    private int mute = 1;
    private int hold = 1;
    private int speaker = 0;
    private TextView callStatusTextView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String status = (String) msg.obj;
            callStatusTextView.setText(status);
        }
    };
    private AppSimpleListener appSimpleListener = new AppSimpleListener()
    {
        @Override
        public void onReceiveCallEvent(String callId, String callerId, String calleeId, String callerName, int media, int callType)
        {
            if (callType == RecCallType.RECEIVE_CALL_TYPE_MISSED_CALL && callId.equals(m_callId))
            {
                MyApplication.isTalking = false;
                finish();
            }
        }

        @Override
        public void onCallStateEvent(String callId, int state, int reason)
        {
            Log.d(TAG, "onCallStateEvent callId: " + callId + ",state: " + state + ",reason: " + reason);

            if (!callId.equals(m_callId)) {
                Log.e(TAG,"错误的通知事件,CALL ID不正确!");
                return;
            }

            Message msg = handler.obtainMessage();
            switch (state) {
                case CallState.CALL_STATE_HANGUP:
                    msg.obj = "通话结束";
                    CallAudioHelper.stopRingback();

                    if (reason == 2 || reason == 3)// 被叫忙
                    {
                        //Toast.makeText(AudioTalkingActivity.this,"对方正在通话中",Toast.LENGTH_SHORT).show();
//                        showToastMessage("对方正在通话中");
                        Log.e(TAG,"对方正在通话中!");
                    }

                    MediaEngineSDK.getInstance().setAudioOutput(0);
                    MyApplication.isTalking = false;
                    finish();
                    break;
                case CallState.CALL_STATE_ANSWER:
                    CallAudioHelper.stopRingback();
                    msg.obj = "音频通话中";
                    break;
                case CallState.CALL_STATE_HOLD:
                    if (reason == 0) {
                        msg.obj = "保持中";
                    } else {
                        msg.obj = "音频通话中";
                    }
                    break;
                case CallState.CALL_STATE_INITIATE:
                    CallAudioHelper.startRingback();
                    msg.obj = "通话连接中";
                    break;
                default:
                    break;
            }
            handler.sendMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_talking);
        ((MyApplication) getApplication()).registerEventListener(appSimpleListener);

        Intent intent = getIntent();
        if (intent != null) {
            m_callId = intent.getStringExtra("call_id");
            m_dstId = intent.getStringExtra("dst_id");
            is_callee = intent.getIntExtra("is_callee", 0);
        }
        callStatusTextView = (TextView) findViewById(R.id.tv_status);
        TextView accountTextView = (TextView) findViewById(R.id.tv_account);
        accountTextView.setText(m_dstId);

        Button hangupButton = (Button) findViewById(R.id.btn_hangup);
        final Button muteButton = (Button) findViewById(R.id.btn_mute);
        final Button holdButton = (Button) findViewById(R.id.btn_hold);
        final Button speakerButton = (Button) findViewById(R.id.btn_speaker);

        speakerButton.setText("扬声器");
        MediaEngineSDK.getInstance().setAudioOutput(0);

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaEngineSDK.getInstance().muteCall(m_callId, mute);
                if (mute == 1) {
                    muteButton.setText("取消静音");
                    mute = 0;
                } else {
                    muteButton.setText("静音");
                    mute = 1;
                }
            }
        });

        holdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaEngineSDK.getInstance().holdCall(m_callId, hold);
                if (hold == 1) {
                    holdButton.setText("恢复通话");
                    hold = 0;
                } else {
                    holdButton.setText("保持");
                    hold = 1;
                }
            }
        });
        hangupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallAudioHelper.stopRingback();

                new Thread(new Runnable() {
                    public void run() {
                        MediaEngineSDK.getInstance().hangupCall(m_callId);
                    }
                }).start();

                MyApplication.isTalking = false;
                finish();
            }
        });
        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speaker == 1) {
                    speakerButton.setText("扬声器");
                    speaker = 0;
                } else {
                    speakerButton.setText("关闭扬声器");
                    speaker = 1;
                }

                MediaEngineSDK.getInstance().setAudioOutput(speaker);
            }
        });

        if(is_callee == 1) {
            int ret = MediaEngineSDK.getInstance().answerCall(m_callId);
            if (ret != 0) {
                Log.e(TAG, "接听来电失败, " + ret);
                MyApplication.isTalking = false;
                finish();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MyApplication) getApplication()).unRegisterEventListener(appSimpleListener);
    }
}
