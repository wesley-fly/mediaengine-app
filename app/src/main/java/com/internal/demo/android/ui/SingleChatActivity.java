package com.internal.demo.android.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.internal.demo.R;
import com.internal.demo.android.utility.FileChatActor;
import com.internal.demo.android.utility.FileUtils;
import com.internal.demo.android.utility.MsgAdapter;
import com.internal.demo.android.utility.MsgEntity;
import com.internal.demo.android.utility.SharedPerfUtils;
import com.internal.demo.android.utility.TextChatActor;
import com.internal.mediasdk.MessageStatus;

import java.util.concurrent.CopyOnWriteArrayList;

public class SingleChatActivity extends ListActivity
{
    protected static final String TAG = SingleChatActivity.class.getSimpleName();

    EditText m_filePath;

    EditText m_textAdd;

    Button m_sendButton;

    Button m_fileButton;

    ProgressBar m_uploadBar;

    ProgressBar m_downloadBar;

    public TextView mTvAccountId;

    public TextView mTvDstAccountId;

    private String mDstAccountId;
    private String mSrcAccountId;
    String m_sendMsg;
    String m_sendFilePath;
    protected boolean m_isFileSend;
    TextChatActor mTextChatActor;
    FileChatActor mFileChatActor;

    CopyOnWriteArrayList<MsgEntity> msgList;
    MsgAdapter m_msgAdapter;

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case TextChatActor.TEXT_SEND_MSG:
                {
                    String sendMsg = (String) msg.obj;
                    Bundle bundle = msg.getData();
                    String msgId = bundle.getString("msgId");
                    int sendStatus = bundle.getInt("resultCode", -1);
                    long timestamp = bundle.getLong("timestamp", -1);

                    MsgEntity msgSend = new MsgEntity();
                    msgSend.setMsgId(msgId);
                    msgSend.setMsgAccount(mSrcAccountId);
                    msgSend.setMsgText(sendMsg);
                    msgSend.setMsgSendState(sendStatus);
                    msgSend.setMsgPeerState(0);

                    msgList.add(msgSend);
                    m_msgAdapter.setDate(msgList);
                }
                break;
                case TextChatActor.TEXT_RECV_MSG:
                {
                    String recMsg = (String) msg.obj;
                    Bundle bundle = msg.getData();

                    String msgId = bundle.getString("msgId");
                    int sendStatus = bundle.getInt("resultCode", -1);
                    String senderId = bundle.getString("senderId");

                    MsgEntity msgRec = new MsgEntity();
                    msgRec.setMsgId(msgId);
                    msgRec.setMsgAccount(senderId);
                    msgRec.setMsgText(recMsg);
                    msgRec.setMsgSendState(sendStatus);
                    msgRec.setMsgPeerState(MessageStatus.MSG_STATUS_READED);

                    msgList.add(msgRec);
                    m_msgAdapter.setDate(msgList);
                }
                break;
                case TextChatActor.REC_OF_MSG:
                {
                    String statusMsgId = (String) msg.obj;
                    Log.e(TAG, "msgList.size(): " + msgList.size());
                    Log.e(TAG, "statusMsgId: " + statusMsgId);
                    for (int i = 0; i < msgList.size(); i++) {
                        if (msgList.get(i).getMsgId().equalsIgnoreCase(statusMsgId)) {
                            MsgEntity msgStatus = new MsgEntity();

                            msgStatus.setMsgId(msgList.get(i).getMsgId());
                            msgStatus.setMsgAccount(msgList.get(i).getMsgAccount());
                            msgStatus.setMsgText(msgList.get(i).getMsgText());
                            msgStatus.setMsgSendState(msgList.get(i).getMsgSendState());
                            msgStatus.setMsgPeerState(MessageStatus.MSG_STATUS_RECEIVED);
                            msgList.remove(i);
                            msgList.add(i, msgStatus);
                            m_msgAdapter.setDate(msgList);
                            break;
                        }
                    }
                }
                break;
                case TextChatActor.READ_OF_MSG:
                {
                    String statusMsgId = (String) msg.obj;

                    for (int i = 0; i < msgList.size(); i++) {
                        if (msgList.get(i).getMsgId().equalsIgnoreCase(statusMsgId)) {
                            MsgEntity msgStatus = new MsgEntity();

                            msgStatus.setMsgId(msgList.get(i).getMsgId());
                            msgStatus.setMsgAccount(msgList.get(i).getMsgAccount());
                            msgStatus.setMsgText(msgList.get(i).getMsgText());
                            msgStatus.setMsgSendState(msgList.get(i).getMsgSendState());
                            msgStatus.setMsgPeerState(MessageStatus.MSG_STATUS_READED);
                            msgList.remove(i);
                            msgList.add(i, msgStatus);
                            m_msgAdapter.setDate(msgList);
                            break;
                        }
                    }
                }
                break;
                case FileChatActor.FILE_SEND_MSG:
                {
                    String filePath = (String) msg.obj;
                    Bundle bundle = msg.getData();
                    String msgId = bundle.getString("msgId");
                    int sendStatus = bundle.getInt("resultCode", -1);
                    String senderId = bundle.getString("senderId");

                    MsgEntity msgFile = new MsgEntity();
                    msgFile.setMsgId(msgId);
                    msgFile.setMsgAccount(mSrcAccountId);
                    msgFile.setMsgText(filePath );
                    msgFile.setMsgSendState(sendStatus);
                    msgFile.setMsgPeerState(0);

                    msgList.add(msgFile);
                    m_msgAdapter.setDate(msgList);
                }
                break;
                case FileChatActor.FILE_RECV_MSG:
                {
                    String fileName = (String) msg.obj;
                    Bundle bundle = msg.getData();
                    String text = bundle.getString("addContent");

                    MsgEntity msgFile = new MsgEntity();

                    String msgId = bundle.getString("msgId");
                    int sendStatus = bundle.getInt("resultCode", -1);
                    String senderId = bundle.getString("senderId");
                    msgFile.setMsgId(msgId);
                    msgFile.setMsgAccount(senderId);
                    msgFile.setMsgText(fileName + "\n" + text);
                    msgFile.setMsgSendState(sendStatus);
                    msgFile.setMsgPeerState(MessageStatus.MSG_STATUS_READED);

                    msgList.add(msgFile);
                    m_msgAdapter.setDate(msgList);
                }
                break;
                case FileChatActor.FILE_UPLOAD_MSG:
                {
                    int upProgress = msg.arg1;
                    m_uploadBar.setProgress(upProgress);
                    if (upProgress == 100) {
                        m_uploadBar.setProgress(0);
                        Log.e(TAG, "附件上传进度: " + upProgress);
                    }
                }
                break;
                case FileChatActor.FILE_DOWNLOAD_MSG:
                {
                    int downProgress = msg.arg1;
                    m_downloadBar.setProgress(downProgress);
                    if (downProgress == 100) {
                        m_downloadBar.setProgress(0);
                        Log.e(TAG, "附件下载进度: " + downProgress);
                    }
                }
                break;
            }

            m_msgAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        m_filePath = (EditText) findViewById(R.id.file_path);
        m_textAdd = (EditText) findViewById(R.id.edit_add_text);

        m_uploadBar = (ProgressBar) findViewById(R.id.uploadBar);
        m_downloadBar = (ProgressBar) findViewById(R.id.downloadBar);
        m_sendButton = (Button) findViewById(R.id.btn_send);
        m_fileButton = (Button) findViewById(R.id.btn_send_file);

        mTvAccountId = (TextView) findViewById(R.id.tv_account);
        mSrcAccountId = SharedPerfUtils.getAccountId(this);

        mTvAccountId.setText(mSrcAccountId + "(" + SharedPerfUtils.getAppAccountId(this) + ")");

        mTextChatActor = new TextChatActor(mHandler);
        mFileChatActor = new FileChatActor(mHandler);

        mTvDstAccountId = (TextView) findViewById(R.id.tv_dst_id);
        Intent intent = getIntent();
        if (intent != null) {
            mDstAccountId = intent.getStringExtra("dst_id");
            mTvDstAccountId.setText(mDstAccountId +  "(" + intent.getStringExtra("dst_app_id") + ")");
        }
        else {
            mTvDstAccountId.setText("unknown");
        }

        msgList = new CopyOnWriteArrayList<>();
        m_msgAdapter = new MsgAdapter(this, msgList);
        setListAdapter(m_msgAdapter);
        setOnListeners();
    }

    private void setOnListeners()
    {
        m_sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_isFileSend == true) {
                    m_sendMsg = m_textAdd.getText().toString();
                    mFileChatActor.sendFileMessage(mDstAccountId, m_sendFilePath, m_sendMsg);
                    m_isFileSend = false;
                } else {
                    m_sendMsg = m_textAdd.getText().toString();
                    mTextChatActor.sendTextMessage(mDstAccountId, m_sendMsg);
                }
                m_textAdd.setText(null);
                m_filePath.setText(null);
            }
        });

        m_fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                m_isFileSend = true;
                Intent intent = null;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                } else {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 0);
            }
        });
    }
    @Override
    protected void onResume()
    {
        ((MyApplication) getApplication()).registerEventListener(mTextChatActor.mTextChatEventListener);
        ((MyApplication) getApplication()).registerEventListener(mFileChatActor.mFileChatEventListener);
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        ((MyApplication) getApplication()).unRegisterEventListener(mTextChatActor.mTextChatEventListener);
        ((MyApplication) getApplication()).unRegisterEventListener(mFileChatActor.mFileChatEventListener);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    m_sendFilePath = FileUtils.getPath(this, uri);
                    m_filePath.setText(m_sendFilePath);
                } else {
                    m_isFileSend = false;
                    m_filePath.setText(null);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
