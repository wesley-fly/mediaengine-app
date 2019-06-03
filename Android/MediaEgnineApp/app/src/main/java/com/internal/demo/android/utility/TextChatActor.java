package com.internal.demo.android.utility;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.internal.demo.android.ui.AppSimpleListener;
import com.internal.mediasdk.MediaEngineSDK;
import com.internal.mediasdk.MessageBase;
import com.internal.mediasdk.MessageOfRead;
import com.internal.mediasdk.MessageOfRec;
import com.internal.mediasdk.MessageOneToOne;
import com.internal.mediasdk.MessageStatus;
import com.internal.mediasdk.MimeType;

public class TextChatActor extends ChatActor
{
    public static final String TAG = TextChatActor.class.getSimpleName();

    public TextChatEventListener mTextChatEventListener;

    public static final int TEXT_BASE = CHAT_BASE << 0;
    public static final int TEXT_SEND_MSG = TEXT_BASE + 1;
    public static final int TEXT_RECV_MSG = TEXT_BASE + 2;
    public static final int REC_OF_MSG = TEXT_BASE + 3;
    public static final int READ_OF_MSG = TEXT_BASE + 4;

    public TextChatActor(Handler handler)
    {
        mHandler = handler;
        mMimeTypeStr = MimeType.TEXT_PLAIN.toString();
        mTextChatEventListener = new TextChatEventListener();
    }

    public int sendTextMessage(String destId, String textMsg) {

        String textMsgId = MediaEngineSDK.getInstance().sendMessage(destId, mMimeTypeStr, textMsg, null, null);

        if (textMsgId == null || textMsgId.length() == 0) {
            return -1;
        }
        mSendTextMsg = textMsg;
        mSendMsgMap.put(textMsgId, textMsg);
        return 0;
    }

    class TextChatEventListener extends AppSimpleListener
    {
        @Override
        public void onSendMessageEvent(String messageId, int sendResult,long timestamp)
        {
            if (mSendMsgMap.containsKey(messageId)) {
                Message msg = new Message();
                msg.what = TEXT_SEND_MSG;
                msg.obj = mSendMsgMap.get(messageId);
                Bundle bundle = new Bundle();
                bundle.putString("msgId", messageId);
                bundle.putInt("resultCode", sendResult);
                bundle.putLong("timestamp", timestamp);
                msg.setData(bundle);
                mHandler.sendMessage(msg);

                mSendMsgMap.remove(messageId);
            }
        }

        @Override
        public void onReceiveMessageEvent(int type, MessageBase message) {
            super.onReceiveMessageEvent(type, message);
            if (message.getClass() == MessageOneToOne.class)
            {
                MessageOneToOne o2oMsg = (MessageOneToOne) message;
                if (!o2oMsg.getMimeType().isFile())
                {
                    Log.e(TAG, "收到文本消息,开始处理");
                    String dstSender = o2oMsg.getSenderId();
                    String msgId = o2oMsg.getMessageId();
                    MediaEngineSDK.getInstance().reportMessageStatus(dstSender, msgId, MessageStatus.MSG_STATUS_RECEIVED); // 已送达
                    MediaEngineSDK.getInstance().reportMessageStatus(dstSender, msgId, MessageStatus.MSG_STATUS_READED); // 已读

                    mRecvTextMsg = o2oMsg.getTextContent();
                    Message msg = new Message();
                    msg.what = TEXT_RECV_MSG;
                    msg.obj = mRecvTextMsg;
                    Bundle bundle = new Bundle();
                    bundle.putString("msgId", msgId);
                    bundle.putInt("resultCode", 0);
                    bundle.putString("senderId", dstSender);
                    msg.setData(bundle);

                    mHandler.sendMessage(msg);
                }
            }
            else if (message.getClass() == MessageOfRec.class)
            {
                MessageOfRec messageOfRec = (MessageOfRec) message;

                Log.e(TAG, "收到文本消息已送达状态,开始处理");
                String msgId = messageOfRec.getMessageId();

                Message msg = new Message();
                msg.what = REC_OF_MSG;
                msg.obj = msgId;

                mHandler.sendMessage(msg);
            }
            else if (message.getClass() == MessageOfRead.class)
            {
                MessageOfRead messageOfRead = (MessageOfRead) message;

                Log.e(TAG, "收到文本消息已读状态,开始处理");
                String msgId = messageOfRead.getMessageId();

                Message msg = new Message();
                msg.what = READ_OF_MSG;
                msg.obj = msgId;
                mHandler.sendMessage(msg);
            }
        }
    }
}
