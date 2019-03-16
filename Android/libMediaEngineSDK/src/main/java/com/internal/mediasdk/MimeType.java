package com.internal.mediasdk;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MimeType
{
    private static Map<String, MimeType> m_mimeTypes = new ConcurrentHashMap<String, MimeType>();

    /** Mime: text/plain */
    public static final MimeType TEXT_PLAIN = buildMimeType("text/plain");
    /** Mime: image/png */
    public static final MimeType IMAGE_PNG = buildMimeType("image/png");
    /** Mime: image/bmp */
    public static final MimeType IMAGE_BMP = buildMimeType("image/bmp");
    /** Mime: image/jpg */
    public static final MimeType IMAGE_JPG = buildMimeType("image/jpg");
    /** Mime: image/jpeg */
    public static final MimeType IMAGE_JPEG = buildMimeType("image/jpeg");
    /** Mime: audio/arm */
    public static final MimeType AUDIO_AMR = buildMimeType("audio/arm");
    /** Mime: audio/3gp */
    public static final MimeType AUDIO_3GP = buildMimeType("audio/3gp");
    /** Mime: audio/mp3 */
    public static final MimeType AUDIO_mp3 = buildMimeType("audio/mp3");
    /** Mime: audio/aac */
    public static final MimeType AUDIO_AAC = buildMimeType("audio/aac");
    /** Mime: video/mpeg */
    public static final MimeType VIDEO_MPEG = buildMimeType("video/mpeg");
    /** Mime: video/mp4 */
    public static final MimeType VIDEO_MP4 = buildMimeType("video/mp4");
    /** Mime: video/avi */
    public static final MimeType VIDEO_AVI = buildMimeType("video/avi");
    /** Mime: video/asf */
    public static final MimeType VIDEO_ASF = buildMimeType("video/asf");
    /** Mime: video/mov */
    public static final MimeType VIDEO_MOV = buildMimeType("video/mov");
    /** Mime: application/pdf */
    public static final MimeType APPLICATION_PDF = buildMimeType("application/pdf");
    /** Mime: application/msword */
    public static final MimeType APPLICATION_MSWORD = buildMimeType("application/msword");
    /** Mime: application/octet-stream */
    public static final MimeType APPLICATION_OCTET_STREAM = buildMimeType("application/octet-stream");
    /** Mime: application/json */
    public static final MimeType APPLICATION_JSON = buildMimeType("application/json");

    private String m_type;
    private String m_subtype;
    private String m_mimeString;

    public static MimeType buildMimeType(String mimeString) {
        if (mimeString == null || mimeString.length() == 0)
            return null;
        MimeType mt = m_mimeTypes.get(mimeString);
        if (mt != null)
            return mt;

        String[] strs = mimeString.split("/");
        if (strs.length == 1)
            return new MimeType(strs[0], "unknown");
        else
            return new MimeType(strs[0], strs[1]);
    }

    private MimeType(String type, String subtype)
    {
        m_type = type;
        m_subtype = subtype;
        m_mimeString = String.format("%s/%s", type, subtype);
        m_mimeTypes.put(m_mimeString, this);
    }

    /**
     * 获取 Mime 的主类型，比如 text/plain 中的 text
     *
     * @return the type
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * 获取 Mime 的子类型，比如 text/plain 中的 plain
     *
     * @return the subtype
     */
    public String getSubtype()
    {
        return m_subtype;
    }

    /**
     * 获取 Mime 字符串，比如 text/plain
     *
     * @return the mimeString
     */
    public String getMimeString()
    {
        return m_mimeString;
    }

    /**
     * 获取 Mime 字符串，比如 text/plain
     *
     * @return the mimeString
     */
    public String toString() {
        return m_mimeString;
    }

    /**
     * 是不是文件类型。规则：只有 "text/***" 和 "application/json" 不是文件，其他都为文件类型
     *
     * @return 如果是文件返回true，否则返回false
     */
    public boolean isFile()
    {
        return !(m_type.equals("text") || this.equals(APPLICATION_JSON));
    }

    @Override
    public boolean equals(Object o)
    {
        return (o instanceof MimeType) && (m_mimeString.equals(((MimeType) o).m_mimeString));
    }
}