package com.internal.mediasdkimpl;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.internal.jni.MediaEngineJni;
import com.internal.jni.MediaEngineListenerJni;
import com.internal.mediasdk.ConferenceCtrl;
import com.internal.mediasdk.ErrorCode;
import com.internal.mediasdk.MediaEngineListenerSDK;
import com.internal.mediasdk.MediaType;
import com.internal.mediasdk.MediaEngineSDK;
import com.internal.mediasdk.MessageObjectType;
import com.internal.mediasdk.MessageOfRead;
import com.internal.mediasdk.MessageOfRec;
import com.internal.mediasdk.MessageOneToOne;
import com.internal.mediasdk.MessageStatus;
import com.internal.mediasdk.MimeType;
import com.internal.utility.RandomUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.ContextUtils;
import org.webrtc.voiceengine.WebRtcAudioRecord;

import java.io.File;

public class MediaEngineSDKImpl extends MediaEngineSDK implements MediaEngineListenerJni {
    private String TAG = getClass().getSimpleName();

    private static MediaEngineSDKImpl singleInstance = null;

    private static MediaEngineJni mediaEngineJni = null;

    private static MediaEngineListenerSDK mediaEngineListenerSDK = null;

    public static MediaEngineSDKImpl getInstance()
    {
        if(singleInstance == null)
        {
            singleInstance = new MediaEngineSDKImpl();
        }
        return singleInstance;
    }

    private MediaEngineSDKImpl()
    {
        mediaEngineJni = new MediaEngineJni();
    }

    @Override
    public synchronized boolean initialize(Context c, String AppKey, MediaEngineListenerSDK listener)
    {
        mediaEngineListenerSDK = listener;

        ContextUtils.initialize(c);
        boolean isInitedOk = mediaEngineJni.Initialize(c, AppKey,this);

        if(isInitedOk)
        {
            mediaEngineJni.SetDeviceInfo("android", Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
        }
        return isInitedOk;
    }
    @Override
    public int deBindAppAccount()
    {
        return mediaEngineJni.DeBindAppAccount();
    }
    @Override
    public String bindAppAccount(String appId)
    {
        String appPassWd = RandomUtil.generatePassword(8);
//        String nm = "+8613812345678";
//        String encPass = PassWordAESUtil.encryptData(nm, appPassWd);
        Log.e(TAG, "appPassWd = " + appPassWd);
        return mediaEngineJni.BindAppAccount(appId, appPassWd);

    }
    @Override
    public String queryIdByAppAccount(String appAccountId)
    {
        String jsonResult = mediaEngineJni.QueryIdByAppAccount(appAccountId);

        return jsonResult;
    }
    @Override
    public int setParameter(String key, String value)
    {
        return mediaEngineJni.SetParameter(key, value);
    }
    @Override
    public String getParameter(String key)
    {
        String value = mediaEngineJni.GetParameter(key);
        if(value!=null && value.length()>0)
        {
            return value;
        }
        else
        {
            return null;
        }
    }
    @Override
    public String sendMessage(String dstId, String mimeType, String textContent, String filePath, String messageId)
    {
        String retMsgId;
        MimeType mt = MimeType.buildMimeType(mimeType);
        if(mt.isFile())
        {
            File file = new File(filePath);
            if(!file.exists() || !file.isFile() || !file.canRead())
            {
                Log.e(TAG,"检查文件不可被操作!");
                return "";
            }
        }
        if(filePath == null)
        {
            filePath = "";
        }
        if(messageId == null)
        {
            messageId = "";
        }
        if(textContent == null)
        {
            textContent = "";
        }

        retMsgId = mediaEngineJni.SendMessage(dstId,mimeType,textContent,filePath,messageId);

        if(retMsgId != null && retMsgId.length() > 0)
        {
            return retMsgId;
        }
        else
        {
            return "";
        }
    }
    @Override
    public String downloadMessageAttachment(String messageId, int isThumbnail, String filePath)
    {
        File file = new File(filePath);
        String filePathStr = file.getParent();
        File fileStr = new File(filePathStr);

        if(!fileStr.exists())
        {
            fileStr.mkdirs();
        }

        return mediaEngineJni.DownloadMessageAttachment(messageId,isThumbnail,filePath);
    }
    @Override
    public String reportMessageStatus(String dstId, String messageId, int status)
    {
        return mediaEngineJni.ReportMessageStatus(dstId,messageId,status);
    }

    @Override
    public String makeCall(String dstID, String srcID, int media)
    {
        if(media == MediaType.MEDIA_AUDIO || media == MediaType.MEDIA_VIDEO) {

            String callId = mediaEngineJni.MakeCall(dstID, srcID, media);

            if (callId != null && callId.length() > 0) {
                return callId;
            } else {
                return null;
            }
        }
        else
        {
            Log.e(TAG, "makeCall media must is MEDIA_AUDIO or MEDIA_VIDEO");
            return null;
        }
    }
    @Override
    public int answerCall(String callID)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return mediaEngineJni.AnswerCall(callID);
    }
    @Override
    public int holdCall(String callID, int hold)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }

        return mediaEngineJni.HoldCall(callID, hold);
    }
    @Override
    public int muteCall(String callID, int mute)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }

        return mediaEngineJni.MuteCall(callID,mute);
    }
    @Override
    public int setAudioOutput(int speaker)
    {
        AudioManager audioManager =
                (AudioManager) ContextUtils.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if(speaker == 1)
        {
            audioManager.setSpeakerphoneOn(true);
        }
        else
        {
            audioManager.setSpeakerphoneOn(false);
        }
        return 0;
    }
    @Override
    public int setAudioOutputMode(int mode)
    {
        AudioManager audioManager =
                (AudioManager) ContextUtils.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        switch (mode)
        {
            case 0:
                audioManager.setMode(AudioManager.MODE_NORMAL);
                break;
            case 1:
                audioManager.setMode(AudioManager.MODE_RINGTONE);
                break;
            case 2:
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                break;
            case 3:
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                break;
            default:
                break;
        }

        return 0;
    }
    @Override
    public int hangupCall(String callID)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        //Before hangup, unmute
        WebRtcAudioRecord.setMicrophoneMute(false);

        return mediaEngineJni.HangupCall(callID);
    }
    @Override
    public int setVideoDisplay(View localeDisplay, View remoteView)
    {
        return mediaEngineJni.SetVideoDisplay(localeDisplay, remoteView);
    }
    @Override
    public int startVideoSend(String callID)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return mediaEngineJni.StartVideoSend(callID);
    }
    @Override
    public int stopVideoSend(String callID, int reason)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return mediaEngineJni.StopVideoSend(callID,reason);
    }
    @Override
    public int setCamera(int device)
    {
        return mediaEngineJni.SetCamera(device);
    }
    @Override
    public String makeOutboundCall(String dstID, String srcID, int media, String positionInfo, String rsInfo, String via)
    {
        if(null == dstID || null == srcID || null == rsInfo)
        {
            Log.e(TAG, "makeOutboundCall dstID or srcID or rsInfo is null");
            return null;
        }
        if(media == MediaType.MEDIA_OUTBOUND_AUDIO || media == MediaType.MEDIA_OUTBOUND_VIDEO)
        {
            if(null == positionInfo)
            {
                positionInfo = "";
            }
            String callID = mediaEngineJni.MakeOutboundCall(dstID, srcID, media, positionInfo, rsInfo, via);
            if(callID != null && callID.length() > 0)
            {
                return callID;
            }
            else
            {
                return null;
            }
        }
        else
        {
            Log.e(TAG, "makeOutboundCall media must is MEDIA_OUTBOUND_AUDIO or MEDIA_OUTBOUND_VIDEO");
            return null;
        }
    }
    @Override
    public String joinConference(String[] dstIDs, String groupID, int media, String srcID,String confID)
    {
        JSONArray jaDstIDs = new JSONArray();
        if(media == MediaType.MEDIA_AUDIO_CONFERENCE || media == MediaType.MEDIA_VIDEO_CONFERENCE ||
                media == MediaType.MEDIA_AUDIO_BAND || media == MediaType.MEDIA_VIDEO_BAND) {
            for (String dstID : dstIDs) {
                jaDstIDs.put(dstID);
            }

            String retId = mediaEngineJni.JoinConference(jaDstIDs.toString(), groupID, media, srcID, confID);

            if (retId != null && retId.length() > 0) {
                return retId;
            } else {
                return null;
            }
        }
        else
        {
            Log.e(TAG, "joinConference media must is MEDIA_AUDIO_CONFERENCE or MEDIA_VIDEO_CONFERENCE or MEDIA_AUDIO_BAND or MEDIA_VIDEO_BAND");
            return null;
        }
    }
    @Override
    public int hangupConference(String confID)
    {
        if(null == confID || 0 == confID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return mediaEngineJni.HangupConference(confID);
    }
    @Override
    public int listConference(String confID)
    {
        if(null == confID || 0 == confID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return mediaEngineJni.ListConference(confID);
    }
    @Override
    public int ctrlConference(String confID, int action, String dstID)
    {
        if(null == confID || 0 == confID.length() || null == dstID || 0 == dstID.length() )
        {
            return ErrorCode.PARAM_ERROR;
        }
        if(action == ConferenceCtrl.CONF_KICKOUT || action == ConferenceCtrl.CONF_MUTE || action == ConferenceCtrl.CONF_UNMUTE)
        {
            return mediaEngineJni.CtrlConference(confID, action, dstID);
        }
        else
        {
            return ErrorCode.PARAM_ERROR;
        }
    }
    @Override
    public int getCallQualityLevel(String callID)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return mediaEngineJni.GetCallQualityLevel(callID);
    }
    //Impl MediaEngineSDK Event
    @Override
    public void onReceiveCallEvent(String JsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JsonString);
            String callId = jsonObject.optString("callId");
            String callerId= jsonObject.optString("callerId");
            String calleeId= jsonObject.optString("calleeId");
            String callerName = jsonObject.optString("callerName");
            int media = jsonObject.optInt("media");
            int callType = jsonObject.optInt("callType");

            mediaEngineListenerSDK.onReceiveCallEvent(callId, callerId, calleeId, callerName, media, callType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onSendMessageEvent(String jsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);

            String msgId = jsonObject.optString("messageId");
            int result = jsonObject.optInt("result");
            long timestamp = jsonObject.optInt("timestamp");

            mediaEngineListenerSDK.onSendMessageEvent(msgId, result,timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onReceiveMessageEvent(String jsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            int type = jsonObject.optInt("type");

            switch (type) {
                case MessageObjectType.MSG_OBJECT_TYPE_MMS:
                {
                    MessageOneToOne singleChatMsg;
                    String mimeType = jsonObject.optString("mimeType");
                    String srcId = jsonObject.optString("srcId");
                    String textContent = jsonObject.optString("textContent");
                    String messageID = jsonObject.optString("messageId");
                    String fileName = jsonObject.optString("fileName");
                    String mediaInfo = jsonObject.optString("mediaInfo");
                    int createTime = jsonObject.optInt("createTime");
                    int sessionType = jsonObject.optInt("sessionType");

                    if (sessionType == 0) {
                        singleChatMsg = new MessageOneToOne(MimeType.buildMimeType(mimeType), srcId, messageID, textContent, createTime, fileName, mediaInfo);
                        mediaEngineListenerSDK.onReceiveMessageEvent(MessageObjectType.MSG_OBJECT_TYPE_MMS, singleChatMsg);
                    }
                }
                break;
                case MessageObjectType.MSG_OBJECT_TYPE_STATUS:
                {
                    int messageStatus = jsonObject.optInt("messageStatus");
                    String messageID = jsonObject.optString("messageId");
                    int createTime = jsonObject.optInt("createTime");

                    if (MessageStatus.MSG_STATUS_RECEIVED == messageStatus) {
                        MessageOfRec msgStateRec = new MessageOfRec((long) createTime);
                        msgStateRec.setMessageId(messageID);
//                        Log.e(TAG, "Impl->STATUS->singleChatMsg->onReceiveMessageEvent Peer Received");
                        mediaEngineListenerSDK.onReceiveMessageEvent(MessageObjectType.MSG_OBJECT_TYPE_STATUS, msgStateRec);
                    } else if (MessageStatus.MSG_STATUS_READED == messageStatus) {
                        MessageOfRead msgStateRead = new MessageOfRead((long) createTime);
                        msgStateRead.setMessageId(messageID);
//                        Log.e(TAG, "Impl->STATUS->singleChatMsg->onReceiveMessageEvent Peer Read ed");
                        mediaEngineListenerSDK.onReceiveMessageEvent(MessageObjectType.MSG_OBJECT_TYPE_STATUS, msgStateRead);
                    }
                }
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDownloadMessageAttachmentEvent(String jsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);

            String msgId = jsonObject.optString("messageId");
            int result = jsonObject.optInt("result");
            mediaEngineListenerSDK.onDownloadMessageAttachmentEvent(msgId, result);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public void onUploadMessageAttachmentProgressEvent(String jsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);

            String msgId = jsonObject.optString("messageId");
            int process = jsonObject.optInt("progress");
            mediaEngineListenerSDK.onUploadMessageAttachmentProgressEvent(msgId, process);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public void onDownloadMessageAttachmentProgressEvent(String jsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);

            String msgId = jsonObject.optString("messageId");
            int process = jsonObject.optInt("progress");

            mediaEngineListenerSDK.onDownloadMessageAttachmentProgressEvent(msgId, process);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onCallStateEvent(String JsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JsonString);

            String callId = jsonObject.optString("callId");
            int state = jsonObject.optInt("state");
            int reason = jsonObject.optInt("reason");

            mediaEngineListenerSDK.onCallStateEvent(callId, state, reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRemoteVideoStateEvent(String JsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JsonString);

            String callId = jsonObject.optString("callId");
            int state = jsonObject.optInt("state") ;

            mediaEngineListenerSDK.onRemoteVideoStateEvent(callId, state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConferenceStateEvent(String JsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JsonString);
            String conferenceId = jsonObject.optString("conferenceId");
            String callId = jsonObject.optString("callId");
            String callerId = jsonObject.optString("callerId");
            int state = jsonObject.optInt("state");
            int reason = jsonObject.optInt("reason");

            mediaEngineListenerSDK.onConferenceStateEvent(conferenceId, callId, callerId, state, reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConferenceVadListEvent(String JsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JsonString);
            String conferenceId = jsonObject.optString("conferenceId");
            String callerId = jsonObject.optString("callerId");
            String listInfo = jsonObject.optString("listInfo");

            mediaEngineListenerSDK.onConferenceVadListEvent(conferenceId, callerId, listInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onSystemEvent(String JsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JsonString);
            int type = jsonObject.optInt("eventType");

            mediaEngineListenerSDK.onSystemEvent(type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
