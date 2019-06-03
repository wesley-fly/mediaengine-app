package com.internal.mediasdk;

import com.internal.mediasdkimpl.MediaEngineSDKImpl;

import android.content.Context;
import android.view.View;

import java.util.Map;

public abstract class MediaEngineSDK {
    public static MediaEngineSDK getInstance()
    {
        return MediaEngineSDKImpl.getInstance();
    }

    public abstract boolean initialize(Context context, String AppKey, MediaEngineListenerSDK listener);

    public abstract int deBindAppAccount();

    public abstract String bindAppAccount(String appId);

    public abstract String queryIdByAppAccount(String appAccountId);

    public abstract int setParameter(String key, String value);

    public abstract String getParameter(String key);

    public abstract String sendMessage(String dstId, String mimeType, String textContent, String filePath, String messageId);

    public abstract String downloadMessageAttachment(String messageId, int isThumbnail, String filePath);

    public abstract String reportMessageStatus(String dstId, String messageId, int status);

    public abstract String makeCall(String dstId, String srcId, int media);

    public abstract int answerCall(String callId);

    public abstract int holdCall(String callId, int hold);

    public abstract int muteCall(String callId, int mute);

    public abstract int setAudioOutput(int device);

    public abstract int setAudioOutputMode(int mode);//no used at this time

    public abstract int hangupCall(String callId);

    public abstract int setVideoDisplay(View localeDisplay, View remoteView);

    public abstract int startVideoSend(String callId);

    public abstract int stopVideoSend(String callId,int reason);

    public abstract int setCamera(int device);

    public abstract String makeOutboundCall(String dstId, String srcId, int media, String positionInfo, String msInfo, String via);

    public abstract String joinConference(String[] dstIds, String groupId, int media, String srcId,String confId);

    public abstract int hangupConference(String confId);

    public abstract int listConference(String confId);

    public abstract int ctrlConference(String confId, int action, String dstId);

    public abstract int getCallQualityLevel(String callId);
}