package com.internal.mediasdk;


public class MessageOfRead extends MessageBase
{
    private String m_messageId;

    public MessageOfRead(long creatTime)
    {
        super(creatTime);
    }

    public String getMessageId()
    {
        return m_messageId;
    }

    public void setMessageId(String msgId)
    {
        m_messageId = msgId;
    }
}
