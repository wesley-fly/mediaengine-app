package com.internal.mediasdk;

import com.internal.mediasdkimpl.MediaEngineSDKImpl;

import android.content.Context;
import android.view.View;

public abstract class MediaEngineSDK {
    public static MediaEngineSDK getInstance()
    {
        return MediaEngineSDKImpl.getInstance();
    }

    public abstract boolean initialize(Context context, String AppKey, EventListener listener);

    public abstract String bindAppAccount(String appId);

    public abstract int setParameter(String key, String value);

    public abstract String getParameter(String key);

    public abstract int receiveCallNotification(String callNotification);

    public abstract void setUserProfile(String profile, String jsonRS);

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

    public abstract String getServerCallID(String callId);

    public abstract int getCallQualityLevel(String callId);
}