package com.internal.demo.android.ui;

import com.internal.mediasdk.MediaEngineListenerSDK;
import com.internal.mediasdk.MessageBase;

public class AppSimpleListener implements MediaEngineListenerSDK
{
    @Override
    public void onSystemEvent(int eventType)
    {}
    @Override
    public void onSendMessageEvent(String messageId, int sendResult, long timestamp)
    {}
    @Override
    public void onReceiveMessageEvent(int type, MessageBase message)
    {}
    @Override
    public void onDownloadMessageAttachmentEvent(String messageId, int downloadResult)
    {}
    @Override
    public void onUploadMessageAttachmentProgressEvent(String messageId, int uploadProgress)
    {}
    @Override
    public void onDownloadMessageAttachmentProgressEvent(String messageId, int downloadProgress)
    {}
    @Override
    public void onReceiveCallEvent(String callId, String callerId, String calleeId, String callerName, int media, int callType)
    {}
    @Override
    public void onCallStateEvent(String callId, int state, int reason)
    {}
    @Override
    public void onRemoteVideoStateEvent(String callId, int videoState)
    {}
    @Override
    public void onConferenceStateEvent(String conferenceId, String callId, String callerId, int state, int reason)
    {}
    @Override
    public void onConferenceVadListEvent(String conferenceId, String callerId, String listInfo)
    {}
}