package com.internal.jni;

public interface MediaEngineListenerJni
{
    public void onReceiveCallEvent(String JsonString);

    public void onCallStateEvent(String JsonString);

    public void onRemoteVideoStateEvent(String JsonString);

    public void onConferenceStateEvent(String JsonString);

    public void onConferenceVadListEvent(String JsonString);

    public void onSendSignal(String JsonString);
}

