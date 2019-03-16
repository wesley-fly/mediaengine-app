package com.internal.demo.android.utility;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import com.internal.demo.R;
import com.internal.demo.android.ui.MyApplication;

import java.io.IOException;

public class CallAudioHelper
{

    private static MediaPlayer m_mediaPlayer = new MediaPlayer();
    private CallAudioHelper()
    {
        m_mediaPlayer = new MediaPlayer();
    }
    public static void startAlarm() {
        try {
            m_mediaPlayer.reset();
            m_mediaPlayer.setDataSource(MyApplication.context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            m_mediaPlayer.setLooping(true);
            m_mediaPlayer.prepare();
            m_mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void stopAlarm() {
        if (m_mediaPlayer != null && m_mediaPlayer.isPlaying())
        {
            try {
                m_mediaPlayer.stop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void startRingback() {
        try {
            m_mediaPlayer.reset();
            Uri alert = Uri.parse("android.resource://com.internal.demo/" + R.raw.ringback_dialing);
            m_mediaPlayer.setDataSource(MyApplication.context, alert);
            m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            m_mediaPlayer.setLooping(true);
            m_mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        m_mediaPlayer.start();
    }
    public static void stopRingback() {
        if (m_mediaPlayer != null && m_mediaPlayer.isPlaying())
        {
            try {
                m_mediaPlayer.stop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
