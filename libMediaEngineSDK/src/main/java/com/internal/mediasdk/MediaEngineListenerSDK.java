package com.internal.mediasdk;

public interface MediaEngineListenerSDK
{
    void onSystemEvent(int eventType);

    void onSendMessageEvent(String messageId, int sendResult, long timestamp);

    void onReceiveMessageEvent(int type, MessageBase message);

    void onDownloadMessageAttachmentEvent(String messageId, int downloadResult);

    void onUploadMessageAttachmentProgressEvent(String messageId, int uploadProgress);

    void onDownloadMessageAttachmentProgressEvent(String messageId, int downloadProgress);

    void onReceiveCallEvent(String callId, String callerId, String calleeId, String callerName, int media, int callType);

    void onCallStateEvent(String callId, int state, int reason);

    void onRemoteVideoStateEvent(String callId, int videoState);

    void onConferenceStateEvent(String conferenceId, String callId, String callerId, int state, int reason);

    void onConferenceVadListEvent(String conferenceId, String callerId, String listInfo);
}
