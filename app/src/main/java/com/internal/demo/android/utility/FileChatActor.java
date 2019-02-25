package com.internal.demo.android.utility;


import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.internal.demo.android.ui.AppSimpleListener;
import com.internal.mediasdk.MediaEngineSDK;
import com.internal.mediasdk.MessageBase;
import com.internal.mediasdk.MessageOneToOne;
import com.internal.mediasdk.MessageStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class FileChatActor extends ChatActor
{
    public static final String TAG = FileChatActor.class.getSimpleName();

    public final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/voipmedia";

    public FileChatEventListener mFileChatEventListener;

    public static final int FILE_BASE = CHAT_BASE << 4;
    public static final int FILE_SEND_MSG = FILE_BASE + 1;
    public static final int FILE_RECV_MSG = FILE_BASE + 2;
    public static final int FILE_UPLOAD_MSG = FILE_BASE + 3;
    public static final int FILE_DOWNLOAD_MSG = FILE_BASE + 4;

    public static Map<String,String> addTextMap = null;

    public FileChatActor(Handler handler)
    {
        mHandler = handler;
        if(addTextMap == null){
            addTextMap = new LinkedHashMap<String, String>();
        }
        mFileChatEventListener = new FileChatEventListener();
    }

    public int sendFileMessage(String destId, String fileSendPath, String addText)
    {
        String extName = FileUtils.getExtName(fileSendPath, '.');

        mMimeTypeStr = FileUtils.getMimeType(extName).toString();
        String fileMsgId = MediaEngineSDK.getInstance().sendMessage(destId, mMimeTypeStr,addText, fileSendPath, null);

        if (fileMsgId == null)
        {
            return -1;
        }
        mSendFileMsgMap.put(fileMsgId, fileSendPath);
        addTextMap.put(fileMsgId, addText);

        return 0;
    }

    class FileChatEventListener extends AppSimpleListener
    {
        @Override
        public void onSendMessageEvent(String messageId, int sendResult,long timestamp)
        {
            if (mSendFileMsgMap.containsKey(messageId)) {
                Message msg = new Message();
                msg.what = FILE_SEND_MSG;
                msg.obj = mSendFileMsgMap.get(messageId)+"\n"+addTextMap.get(messageId);
                Bundle bundle = new Bundle();
                bundle.putString("msgId", messageId);
                bundle.putInt("resultCode", sendResult);
                bundle.putLong("timestamp", timestamp);
                msg.setData(bundle);
                mHandler.sendMessage(msg);

                mSendFileMsgMap.remove(messageId);
                addTextMap.remove(messageId);
            }
        }
        @Override
        public void onReceiveMessageEvent(int type, MessageBase message) {
            super.onReceiveMessageEvent(type,message);
            if (message.getClass() == MessageOneToOne.class)
            {
                MessageOneToOne o2oMsg = (MessageOneToOne)message;
                if(o2oMsg.getMimeType().isFile())
                {
                    Log.e(TAG, "收到附件类型消息,开始处理");
                    String dstSender = o2oMsg.getSenderId();
                    String msgId = o2oMsg.getMessageId();
                    MediaEngineSDK.getInstance().reportMessageStatus(dstSender, msgId, MessageStatus.MSG_STATUS_RECEIVED); // 已送达
                    MediaEngineSDK.getInstance().reportMessageStatus(dstSender, msgId, MessageStatus.MSG_STATUS_READED); // 已读
                    Message msg = new Message();
                    msg.what = FILE_RECV_MSG;
                    String fileName = SDCARD_PATH+ "/" + o2oMsg.getFilename();
                    String textContent = o2oMsg.getTextContent();
                    Bundle bundle = new Bundle();
                    bundle.putString("msgId", msgId);
                    bundle.putString("addContent", textContent);
                    bundle.putString("senderId", dstSender);
                    bundle.putInt("resultCode", 0);
                    msg.setData(bundle);
                    msg.obj = fileName;
                    mHandler.sendMessage(msg);
                    MediaEngineSDK.getInstance().downloadMessageAttachment(o2oMsg.getMessageId(), 0, fileName);
                }
            }
        }

        @Override
        public void onUploadMessageAttachmentProgressEvent(String messageId, int uploadProgress)
        {
            if (mSendFileMsgMap.containsKey(messageId))
            {
                Message msg = new Message();
                msg.what = FILE_UPLOAD_MSG;
                msg.arg1 = uploadProgress;
                mHandler.sendMessage(msg);
            }
        }

        @Override
        public void onDownloadMessageAttachmentProgressEvent(String messageId, int downloadProgress)
        {
            Message msg = new Message();
            msg.what = FILE_DOWNLOAD_MSG;
            msg.arg1 = downloadProgress;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onDownloadMessageAttachmentEvent(String messageId, int downloadResult)
        {
            if (downloadResult != 0)
            {
                Log.e(TAG, "下载附件文件失败,错误码:" + downloadResult);
            }
            else
            {
                Log.e(TAG, "下载附件文件OK!");
            }
        }
    }
}
