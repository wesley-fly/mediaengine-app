package com.internal.demo.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.internal.demo.R;
import com.internal.demo.android.utility.CallAudioHelper;
import com.internal.mediasdk.CallState;
import com.internal.mediasdk.MediaEngineSDK;
import com.internal.mediasdk.RecCallType;
import com.internal.mediasdk.RemoteVideoState;

import android.view.*;

import org.webrtc.videoengine.ViERenderer;
import org.webrtc.videoengine.VideoCaptureAndroid;

public class VideoTalkingActivity extends BaseActivity {
    private static final String TAG = VideoTalkingActivity.class.getSimpleName();

    private String m_callId;
    private String m_dstId;
    private int is_callee = 0;

    private int camera = 1;//1:Front, 0:Back
    private int mute = 1;
    private int speaker = 0;
    private int isOpenCamera = 0;

    private TextView callStatusTextView;

    private FrameLayout mLocalLinearLayout = null,mRemoteLinearLayout = null;
    private SurfaceView mLocalSurfaceView = null, mRemoteSurfaceView = null;

    private final int WHAT_STOP_VIDEO = 1;
    private final int WHAT_START_VIDEO = 2;
    private final int WHAT_TALKING = 3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_STOP_VIDEO) {
                String status = (String) msg.obj;
                callStatusTextView.setText(status);
            }
            else if(msg.what == WHAT_START_VIDEO)
            {
                String status = (String) msg.obj;
                callStatusTextView.setText(status);
            }
            else {
                String status = (String) msg.obj;
                callStatusTextView.setText(status);
            }
        }
    };

    private AppSimpleListener appSimpleListener = new AppSimpleListener()
    {
        @Override
        public void onRemoteVideoStateEvent(String callId, int videoState)
        {
            Log.e(TAG, "onRemoteVideoStateEvent-------callId: " + callId + " videoState: " + videoState);
            Message msg = handler.obtainMessage();
			switch (videoState)
            {
                case RemoteVideoState.REMOTE_VIDEO_STOP:
                    msg.what = WHAT_STOP_VIDEO;
                    msg.obj = "视频关闭";
                    break;
                case RemoteVideoState.REMOTE_VIDEO_START:
                    msg.what = WHAT_START_VIDEO;
                    msg.obj = "视频开启";
                    break;
                default:
                    break;
            }
			handler.sendMessage(msg);
        }
        @Override
        public void onCallStateEvent(String callId,int state, int reason) {
            Log.e(TAG, "onCallStateEvent callId = " + callId + ", state = " + state + ", reason = " + reason);
            if (!callId.equals(m_callId))
                return;

            Message msg = handler.obtainMessage();

            switch (state) {
                case CallState.CALL_STATE_HANGUP:
                    CallAudioHelper.stopRingback();
                    msg.obj = "通话结束";
                    MyApplication.isTalking = false;
                    MediaEngineSDK.getInstance().setVideoDisplay(null,null);
                    finish();
                    break;
                case CallState.CALL_STATE_HOLD:
                    if (reason == 0) {
                        msg.obj = "保持中";
                    } else {
                        msg.obj = "视频通话中";
                    }
                    break;
                case CallState.CALL_STATE_INITIATE:
                    CallAudioHelper.startRingback();
                    msg.obj = "通话连接中";
                    break;
                case CallState.CALL_STATE_ANSWER:
                    CallAudioHelper.stopRingback();
                    msg.what = WHAT_TALKING;
                    msg.obj = "视频通话中";

                    break;
                default:
                    break;
            }
            handler.sendMessage(msg);
        }
        @Override
        public void onReceiveCallEvent(String callId, String callerId, String calleeId, String callerName, int media, int callType)
        {
            Log.d(TAG, "onReceiveCallEvent-------callId: " + callId + " callerId: " + callerId + " calleeId: " + calleeId + " callerName: " + callerName
                    + " media: " + media + " callType: " + callType);

            if (callType == RecCallType.RECEIVE_CALL_TYPE_MISSED_CALL && callId.equals(m_callId)) {
                CallAudioHelper.stopRingback();
                MyApplication.isTalking = false;

                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_talking);
        ((MyApplication) getApplication()).registerEventListener(appSimpleListener);

        Intent intent = getIntent();
        if (intent != null) {
            m_callId = intent.getStringExtra("call_id");
            m_dstId = intent.getStringExtra("dst_id");
            is_callee = intent.getIntExtra("is_callee", 0);
        }

        callStatusTextView = findViewById(R.id.tv_status);
        TextView accountTextView = findViewById(R.id.tv_account);
        accountTextView.setText(m_dstId);

        final Button switchCamera = findViewById(R.id.btn_switch_camera);

        final Button muteButton = findViewById(R.id.btn_mute);
        final Button speakerButton = findViewById(R.id.btn_speaker);
        final Button videoButton = findViewById(R.id.btn_video);
        final Button hangupButton = findViewById(R.id.btn_hangup);

        speakerButton.setText("扬声器");
        MediaEngineSDK.getInstance().setAudioOutput(0);

        mLocalLinearLayout = findViewById(R.id.fl_local_video);
        mRemoteLinearLayout = findViewById(R.id.fl_remote_video);
        PrepareVideoRender();

        switchCamera.setOnClickListener(v -> {
            if (camera == 1) {
                switchCamera.setText("前置");
                camera = 0;
            } else {
                switchCamera.setText("后置");
                camera = 1;
            }
            MediaEngineSDK.getInstance().setCamera(camera);
        });
        muteButton.setOnClickListener(v -> {
            MediaEngineSDK.getInstance().muteCall(m_callId, mute);
            if (mute == 1) {
                muteButton.setText("取消静音");
                mute = 0;
            } else {
                muteButton.setText("静音");
                mute = 1;
            }
        });
        speakerButton.setOnClickListener(v -> {
            if (speaker == 1) {
                speakerButton.setText("扬声器");
                speaker = 0;
            } else {
                speakerButton.setText("关闭扬声器");
                speaker = 1;
            }

            MediaEngineSDK.getInstance().setAudioOutput(speaker);
        });
        videoButton.setOnClickListener(v -> {
            if (isOpenCamera == 1) {
                videoButton.setText("开启视频");
                isOpenCamera = 0;
                MediaEngineSDK.getInstance().stopVideoSend(m_callId,0);
            } else {
                videoButton.setText("关闭视频");
                isOpenCamera = 1;
                MediaEngineSDK.getInstance().startVideoSend(m_callId);
            }
        });
        hangupButton.setOnClickListener(v -> {
            CallAudioHelper.stopRingback();

            new Thread(() -> MediaEngineSDK.getInstance().hangupCall(m_callId)).start();

            MyApplication.isTalking = false;
            finish();
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
    private void PrepareVideoRender()
    {
        mLocalSurfaceView = ViERenderer.CreateRenderer(VideoTalkingActivity.this, false);
        if(mLocalLinearLayout != null)
        {
            ViewGroup.LayoutParams params = mLocalLinearLayout.getLayoutParams();
            ViewGroup.LayoutParams newParams = new ViewGroup.LayoutParams(params.width, params.height);
            mLocalLinearLayout.setPadding(1, 1, 1, 1);
            mLocalSurfaceView.setZOrderMediaOverlay(true);
            mLocalLinearLayout.addView(mLocalSurfaceView,newParams);
            VideoCaptureAndroid.setLocalPreview(mLocalSurfaceView.getHolder());
        }
        mRemoteSurfaceView = ViERenderer.CreateRenderer(VideoTalkingActivity.this,true);
        if(mRemoteLinearLayout != null)
        {
            mRemoteLinearLayout.addView(mRemoteSurfaceView);
        }
        MediaEngineSDK.getInstance().setVideoDisplay(mLocalSurfaceView,mRemoteSurfaceView);
    }
}
