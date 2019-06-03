package com.internal.demo.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.internal.demo.R;
import com.internal.demo.android.utility.CallAudioHelper;
import com.internal.mediasdk.CallState;
import com.internal.mediasdk.MediaEngineSDK;
import com.internal.mediasdk.MediaType;
import com.internal.mediasdk.RecCallType;

public class IncomingCallActivity extends BaseActivity
{
    private static final String TAG = IncomingCallActivity.class.getSimpleName();

    private String m_callId;

    private String m_callerName;

    private int m_callType;

    private String m_callerId;

    private Intent intent;

    private AppSimpleListener appSimpleListener = new AppSimpleListener()
    {
        @Override
        public void onCallStateEvent(String callId, int state, int reason)
        {
            Log.e(TAG, "onCallStateEvent callId: " + callId + " state: " + state + " reason: " + reason);
            switch (state)
            {
                case CallState.CALL_STATE_HANGUP:
                    if (callId.equals(m_callId)){
                        MyApplication.isTalking = false;
                        CallAudioHelper.stopAlarm();
                        finish();
                    }
                    break;
                case CallState.CALL_STATE_ANSWER:
                    CallAudioHelper.stopAlarm();
                    finish();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onReceiveCallEvent(String callId, String callerId, String calleeId, String callerName, int media, int callType)
        {
            Log.e(TAG, "onReceiveCallEvent callId: " + callId + " callerId: " + callerId + " callerName: " + callerName + " media: " + media + " callType: " + callType);

            if (callType == RecCallType.RECEIVE_CALL_TYPE_MISSED_CALL)
            {
                Log.e(TAG, "未接来电");
                CallAudioHelper.stopAlarm();

                MyApplication.isTalking = false;
                finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        MyApplication.isTalking = true;
        CallAudioHelper.startAlarm();

        Intent getIntent = getIntent();
        if (getIntent != null) {
            m_callId = getIntent.getStringExtra("call_id");
            m_callerName = getIntent.getStringExtra("caller_name");
            m_callType = getIntent.getIntExtra("call_type", 0);
            m_callerId = getIntent.getStringExtra("caller_id");
        }
        TextView callerNameTextView = (TextView) findViewById(R.id.tv_caller_name);
        callerNameTextView.setText(m_callerName);
        TextView callTypeNameTextView = (TextView) findViewById(R.id.tv_call_type);
        String callTypeName = null;
        switch (m_callType) {
            case MediaType.MEDIA_AUDIO:
                callTypeName = "语音呼叫";
                intent = new Intent(IncomingCallActivity.this, AudioTalkingActivity.class);
                break;
            case MediaType.MEDIA_VIDEO:
                callTypeName = "视频呼叫";
                intent = new Intent(IncomingCallActivity.this, VideoTalkingActivity.class);
                break;
            default:
                break;
        }
        callTypeNameTextView.setText(callTypeName);

        findViewById(R.id.btn_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("call_id", m_callId);
                intent.putExtra("dst_id", m_callerId);
                intent.putExtra("is_callee", 1);
                CallAudioHelper.stopAlarm();
                if (!isFinishing()) {
                    startActivity(intent);
                    finish();
                }
            }
        });
        findViewById(R.id.btn_hangup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallAudioHelper.stopAlarm();
                MediaEngineSDK.getInstance().hangupCall(m_callId);
                MyApplication.isTalking = false;
                finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ((MyApplication) getApplication()).registerEventListener(appSimpleListener);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ((MyApplication) getApplication()).unRegisterEventListener(appSimpleListener);
    }
}
