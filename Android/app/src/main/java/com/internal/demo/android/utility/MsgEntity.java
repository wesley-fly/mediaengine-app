package com.internal.demo.android.utility;

public class MsgEntity
{
    private String msgId = "";

    private String msgAccount;

    private String msgText;

    private int msgSendState;

    private int msgPeerState;

    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }
    public void setMsgAccount(String msgAccount)
    {
        this.msgAccount = msgAccount;
    }
    public void setMsgText(String msgText)
    {
        this.msgText = msgText;
    }
    public void setMsgSendState(int msgSendState)
    {
        this.msgSendState = msgSendState;
    }
    public void setMsgPeerState(int msgPeerState)
    {
        this.msgPeerState = msgPeerState;
    }
    public String getMsgId()
    {
        return msgId;
    }
    public String getMsgAccount()
    {
        return msgAccount;
    }
    public String getMsgText()
    {
        return msgText;
    }
    public int getMsgSendState()
    {
        return msgSendState;
    }
    public int getMsgPeerState()
    {
        return msgPeerState;
    }
}
