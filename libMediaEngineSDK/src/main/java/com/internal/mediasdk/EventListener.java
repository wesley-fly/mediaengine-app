package com.internal.mediasdk;

public interface EventListener {
    void onReceiveCallEvent(String callId, String callerId, String calleeId, String callerName, int media, int callType);

    void onCallStateEvent(String callId, int state, int reason);

    void onRemoteVideoStateEvent(String callId, int videoState);

    void onConferenceStateEvent(String conferenceId, String callId, String callerId, int state, int reason);

    void onConferenceVadListEvent(String conferenceId, String callerId, String listInfo);

    void onSendSignal(String targetId, String isConf,String srcID,String jsonContent,String signalType,String signalCause,String coreServerCallID);
}
