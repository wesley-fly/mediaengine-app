package com.internal.jni;

import android.content.Context;

public class MediaEngineJni {
    static
    {
        System.loadLibrary("WtkMediaSDKJni");
    }

    public native boolean Initialize(Context context, String AppKey, MediaEngineListenerJni listener);

    public native void SetDeviceInfo(String os, String mf, String md, String ov, int api);

    public native String BindAppAccount(String appId, String appPassWd);

    public native void SetUserProfile(String profile, String jsonRS);

    public native int SetParameter(String key, String value);

    public native String GetParameter(String key);

    public native int ReceiveCallNotification(String callNotification);

    public native String MakeCall(String dstID, String srcID, int media);

    public native int AnswerCall(String callID);

    public native int HoldCall(String callID, int hold);

    public native int MuteCall(String callID, int mute);

    public native int HangupCall(String callID);

    public native int SetVideoDisplay( Object localView, Object remoteView);

    public native int StartVideoSend(String callID);

    public native int StopVideoSend(String callID,int reason);

    public native int SetCamera(int device);

    public native String MakeOutboundCall(String dstID, String srcID, int media, String positionInfo, String rsInfo, String via);

    public native String JoinConference(String dstIDs, String groupID, int media, String srcID, String confID);

    public native int HangupConference(String confID);

    public native int ListConference(String confID);

    public native int CtrlConference(String confID, int action, String dstID);

    public native String GetServerCallID(String callID);

    public native int GetCallQualityLevel(String callID);
}