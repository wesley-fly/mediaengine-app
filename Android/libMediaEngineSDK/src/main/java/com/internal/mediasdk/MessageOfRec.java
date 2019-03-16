package com.internal.mediasdk;

public class MessageOfRec extends MessageBase
{
    private String m_messageId;

    public MessageOfRec(long createTime)
    {
        super(createTime);
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
