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
import com.internal.mediasdk.EventListener;
import com.internal.mediasdk.MediaType;
import com.internal.mediasdk.MediaEngineSDK;
import com.internal.utility.PassWordAESUtil;
import com.internal.utility.RandomUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.ContextUtils;
import org.webrtc.voiceengine.WebRtcAudioRecord;

public class MediaEngineSDKImpl extends MediaEngineSDK implements MediaEngineListenerJni {
    private String TAG = getClass().getSimpleName();

    private static MediaEngineSDKImpl singleInstance = null;

    private static MediaEngineJni m_wtk_sdk_jni = null;

    private static EventListener m_WtkSdkEventListener = null;

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
        m_wtk_sdk_jni = new MediaEngineJni();
    }

    @Override
    public synchronized boolean initialize(Context c, String AppKey, EventListener listener)
    {
        m_WtkSdkEventListener = listener;

        ContextUtils.initialize(c);
        boolean isInitedOk = m_wtk_sdk_jni.Initialize(c, AppKey,this);

        if(isInitedOk)
        {
            m_wtk_sdk_jni.SetDeviceInfo("android", Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
        }
        return isInitedOk;
    }
    @Override
    public String bindAppAccount(String appId)
    {
        String appPassWd = RandomUtil.generatePassword(8);
        String nm = "+8613812345678";
        return m_wtk_sdk_jni.BindAppAccount(appId, PassWordAESUtil.encryptDatas(nm, appPassWd));

    }
    @Override
    public int setParameter(String key, String value)
    {
        return m_wtk_sdk_jni.SetParameter(key, value);
    }
    @Override
    public String getParameter(String key)
    {
        String value = m_wtk_sdk_jni.GetParameter(key);
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
    public int receiveCallNotification(String callNotification)
    {
        if(0 == callNotification.length())
        {
            return ErrorCode.PARAM_ERROR;
        }

        return m_wtk_sdk_jni.ReceiveCallNotification(callNotification);
    }
    @Override
    public void setUserProfile(final String profile, final String jsonRS)
    {
        if( 0 == profile.length() || 0 == jsonRS.length())
        {
            return;
        }
        m_wtk_sdk_jni.SetUserProfile(profile, jsonRS);
    }
    @Override
    public String makeCall(String dstID, String srcID, int media)
    {
        if(media == MediaType.MEDIA_AUDIO || media == MediaType.MEDIA_VIDEO) {

            String callId = m_wtk_sdk_jni.MakeCall(dstID, srcID, media);

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
        return m_wtk_sdk_jni.AnswerCall(callID);
    }
    @Override
    public int holdCall(String callID, int hold)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }

        return m_wtk_sdk_jni.HoldCall(callID, hold);
    }
    @Override
    public int muteCall(String callID, int mute)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }

        return m_wtk_sdk_jni.MuteCall(callID,mute);
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

        return m_wtk_sdk_jni.HangupCall(callID);
    }
    @Override
    public int setVideoDisplay(View localeDisplay, View remoteView)
    {
        if(null == localeDisplay || null == remoteView)
        {
            return ErrorCode.PARAM_ERROR;
        }
        return m_wtk_sdk_jni.SetVideoDisplay(localeDisplay, remoteView);
    }
    @Override
    public int startVideoSend(String callID)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return m_wtk_sdk_jni.StartVideoSend(callID);
    }
    @Override
    public int stopVideoSend(String callID, int reason)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return m_wtk_sdk_jni.StopVideoSend(callID,reason);
    }
    @Override
    public int setCamera(int device)
    {
        return m_wtk_sdk_jni.SetCamera(device);
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
            String callID = m_wtk_sdk_jni.MakeOutboundCall(dstID, srcID, media, positionInfo, rsInfo, via);
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

            String retId = m_wtk_sdk_jni.JoinConference(jaDstIDs.toString(), groupID, media, srcID, confID);

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
        return m_wtk_sdk_jni.HangupConference(confID);
    }
    @Override
    public int listConference(String confID)
    {
        if(null == confID || 0 == confID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return m_wtk_sdk_jni.ListConference(confID);
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
            return m_wtk_sdk_jni.CtrlConference(confID, action, dstID);
        }
        else
        {
            return ErrorCode.PARAM_ERROR;
        }
    }
    @Override
    public String getServerCallID(String callID)
    {
        if(null == callID || 0 == callID.length())
        {
            return null;
        }

        return m_wtk_sdk_jni.GetServerCallID(callID);
    }
    @Override
    public int getCallQualityLevel(String callID)
    {
        if(null == callID || 0 == callID.length())
        {
            return ErrorCode.PARAM_ERROR;
        }
        return m_wtk_sdk_jni.GetCallQualityLevel(callID);
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

            m_WtkSdkEventListener.onReceiveCallEvent(callId, callerId, calleeId, callerName, media, callType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCallStateEvent(String JsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JsonString);

            String callId = jsonObject.optString("callID");
            int state = jsonObject.optInt("state");
            int reason = jsonObject.optInt("reason");

            m_WtkSdkEventListener.onCallStateEvent(callId, state, reason);
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

            m_WtkSdkEventListener.onRemoteVideoStateEvent(callId, state);
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

            m_WtkSdkEventListener.onConferenceStateEvent(conferenceId, callId, callerId, state, reason);
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

            m_WtkSdkEventListener.onConferenceVadListEvent(conferenceId, callerId, listInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onSendSignal(String JsonString)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JsonString);
            String targetId = jsonObject.optString("targetId");
            String isConf = jsonObject.optString("isConfEvent");
            String srcID= jsonObject.optString("srcID");
            String jsonContent = jsonObject.optString("jsonContent");
            String signalType = jsonObject.optString("signalType");
            String signalCause = jsonObject.optString("signalCause");
            String coreServerCallID = jsonObject.optString("coreServerCallID");

            m_WtkSdkEventListener.onSendSignal(targetId, isConf, srcID, jsonContent, signalType,signalCause,coreServerCallID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
