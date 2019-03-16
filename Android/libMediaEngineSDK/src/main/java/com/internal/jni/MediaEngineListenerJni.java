package com.internal.jni;

public interface MediaEngineListenerJni
{
    public void onSystemEvent(String JsonString);

    public void onSendMessageEvent(String jsonString);

    public void onReceiveMessageEvent(String jsonString);

    public void onDownloadMessageAttachmentEvent(String jsonString);

    public void onUploadMessageAttachmentProgressEvent(String jsonString);

    public void onDownloadMessageAttachmentProgressEvent(String jsonString);

    public void onReceiveCallEvent(String JsonString);

    public void onCallStateEvent(String JsonString);

    public void onRemoteVideoStateEvent(String JsonString);

    public void onConferenceStateEvent(String JsonString);

    public void onConferenceVadListEvent(String JsonString);
}

