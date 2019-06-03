package com.internal.demo.android.ui;


import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.internal.demo.android.utility.SharedPerfUtils;
import com.internal.mediasdk.MediaEngineListenerSDK;
import com.internal.mediasdk.MediaEngineSDK;
import com.internal.mediasdk.MessageBase;
import com.internal.mediasdk.ParametersNames;

import java.util.concurrent.CopyOnWriteArrayList;

public class MyApplication extends Application implements MediaEngineListenerSDK {
    private String TAG = getClass().getSimpleName();

    private CopyOnWriteArrayList<AppSimpleListener> listeners = new CopyOnWriteArrayList<AppSimpleListener>();

    public static Context context = null;

    public volatile static  boolean  isTalking = false;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        String ServerHost = SharedPerfUtils.getServerHost(this);
        String ServerPort = SharedPerfUtils.getServerPort(this);
        String LocalDBPath = getApplicationContext().getFilesDir().toString();
        String LocalSDPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/voipmedia";

        //nbKpDGP0Bs1kwIqEe6WPOGlEkN2lPAYG
        boolean initOk = MediaEngineSDK.getInstance().initialize(context,"111",this);
        Log.e(TAG, "VoIP 初始化,初始化需要先设定服务器地址: " + ServerHost + ":" + ServerPort);
        MediaEngineSDK.getInstance().setParameter(ParametersNames.KEY_ROOT_CS, ServerHost);
        MediaEngineSDK.getInstance().setParameter(ParametersNames.KEY_ROOT_CS_PORT, ServerPort);
        MediaEngineSDK.getInstance().setParameter(ParametersNames.KEY_LOCAL_DB_PATH, LocalDBPath);
        MediaEngineSDK.getInstance().setParameter(ParametersNames.KEY_LOCAL_SD_PATH, LocalSDPath);

        if(initOk)
        {
            tryAutoLoginAccount();
        }
        else
        {
            Log.e(TAG, "VoIP初始化失败!");
        }
    }
    private void tryAutoLoginAccount() {
        final String appAccountId = SharedPerfUtils.getAppAccountId(this);

        if (appAccountId != null)
        {
            new Thread(new Runnable(){
                @Override
                public void run()
                {
                    Log.e(TAG, "自动登陆帐号: " + appAccountId);
                    String AccountId = MediaEngineSDK.getInstance().bindAppAccount(appAccountId);
                    if (AccountId.length() == 8)
                    {
                        Log.e(TAG, "自动登陆帐号成功,返回AccountId = " + AccountId);
                    }
                    else
                    {
                        Log.e(TAG, "自动登陆帐号失败!");
                    }
                }
            }).start();
        }
    }
    public void registerEventListener(AppSimpleListener listener)
    {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    public void unRegisterEventListener(AppSimpleListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void onSystemEvent(int eventType)
    {
//        Log.e(TAG, "onSystemEvent : type = " + eventType);
        for (MediaEngineListenerSDK listener : listeners) {
            listener.onSystemEvent(eventType);
        }
    }

    @Override
    public void onSendMessageEvent(String messageId, int sendResult, long timestamp)
    {
//        Log.e(TAG, "onSendMessageEvent messageId = " + messageId + ", sendResult = " + sendResult + ", timestamp = " + timestamp);
        for (MediaEngineListenerSDK listener : listeners) {
            listener.onSendMessageEvent(messageId, sendResult,timestamp);
        }
    }
    @Override
    public void onReceiveMessageEvent(int type, MessageBase message)
    {
        for (MediaEngineListenerSDK listener : listeners) {
            listener.onReceiveMessageEvent(type, message);
        }
    }
    @Override
    public void onDownloadMessageAttachmentEvent(String messageId, int downloadResult)
    {
//        Log.e(TAG, "onDownloadMessageAttachmentEvent messageId = " + messageId + ", downloadResult = " + downloadResult);
        for (MediaEngineListenerSDK listener : listeners) {
            listener.onDownloadMessageAttachmentEvent(messageId, downloadResult);
        }
    }
    @Override
    public void onUploadMessageAttachmentProgressEvent(String messageId, int uploadProgress)
    {
//        Log.e(TAG, "onUploadMessageAttachmentProgressEvent messageId = " + messageId + ", uploadProgress = " + uploadProgress);
        for (MediaEngineListenerSDK listener : listeners) {
            listener.onUploadMessageAttachmentProgressEvent(messageId, uploadProgress);
        }
    }
    @Override
    public void onDownloadMessageAttachmentProgressEvent(String messageId, int downloadProgress)
    {
//        Log.e(TAG, "onDownloadMessageAttachmentProgressEvent messageId = " + messageId + ", downloadProgress = " + downloadProgress);
        for (MediaEngineListenerSDK listener : listeners) {
            listener.onDownloadMessageAttachmentProgressEvent(messageId,downloadProgress);
        }
    }

    @Override
    public void onReceiveCallEvent(String callId, String callerId, String calleeId, String callerName, int media, int callType)
    {
        for (MediaEngineListenerSDK listener : listeners) {
            listener.onReceiveCallEvent(callId,callerId,calleeId,callerName,media,callType);
        }
    }
    @Override
    public void onCallStateEvent(String callId, int state, int reason)
    {
        for (MediaEngineListenerSDK listener : listeners) {
            listener.onCallStateEvent(callId,state, reason);
        }
    }
    @Override
    public void onRemoteVideoStateEvent(String callId, int videoState)
    {
        Log.e(TAG, "onRemoteVideoStateEvent");
    }
    @Override
    public void onConferenceStateEvent(String conferenceId, String callId, String callerId, int state, int reason)
    {
        Log.e(TAG, "onConferenceStateEvent");
    }
    @Override
    public void onConferenceVadListEvent(String conferenceId, String callerId, String listInfo)
    {
        Log.e(TAG, "onConferenceVadListEvent");
    }
}