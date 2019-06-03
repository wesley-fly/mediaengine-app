package com.internal.mediasdk;

public class MessageOneToOne extends MessageBase
{
    private MimeType m_mimeType;

    private String m_senderId;

    private String m_messageId;

    private String m_textContent;

    private String m_filename;

    private String m_mediaInfo;

    public MessageOneToOne(MimeType mimeType, String sender, String messageId, String textContent, long createTime, String filename, String mediaInfo)
    {
        super(createTime);
        m_mimeType = mimeType;
        m_senderId = sender;
        m_messageId = messageId;
        m_textContent = textContent;
        m_filename = filename;
        m_mediaInfo = mediaInfo;
    }

    public MimeType getMimeType() {
        return m_mimeType;
    }

    public String getSenderId() {
        return m_senderId;
    }

    public String getMessageId() {
        return m_messageId;
    }

    public String getTextContent() {
        return m_textContent;
    }

    public String getFilename() {
        return m_filename;
    }

    public String getMediaInfo() {
        return m_mediaInfo;
    }
}
