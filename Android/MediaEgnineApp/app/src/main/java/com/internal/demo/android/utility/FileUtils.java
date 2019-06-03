package com.internal.demo.android.utility;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.internal.mediasdk.MimeType;

import java.util.Hashtable;

public class FileUtils
{
    private static Hashtable<String, MimeType> extTable = new Hashtable<String, MimeType>();

    private static boolean isInit = false;

    private static void init()
    {
        //text file
        extTable.put("txt", MimeType.APPLICATION_OCTET_STREAM);
        extTable.put("pdf", MimeType.APPLICATION_PDF);
        extTable.put("doc", MimeType.APPLICATION_MSWORD);

        //audio file
        extTable.put("3gp", MimeType.AUDIO_3GP);
        extTable.put("aac", MimeType.AUDIO_AAC);
        extTable.put("amr", MimeType.AUDIO_AMR);
        extTable.put("mp3", MimeType.AUDIO_mp3);

        //picture file
        extTable.put("jpeg", MimeType.IMAGE_JPEG);
        extTable.put("jpg", MimeType.IMAGE_JPEG);
        extTable.put("png", MimeType.IMAGE_PNG);

        //video file
        extTable.put("asf", MimeType.VIDEO_ASF);
        extTable.put("avi", MimeType.VIDEO_AVI);
        extTable.put("mov", MimeType.VIDEO_MOV);
        extTable.put("mp4", MimeType.VIDEO_MP4);
        extTable.put("mpeg", MimeType.VIDEO_MPEG);

        extTable.put(" ", MimeType.APPLICATION_OCTET_STREAM);
    }

    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public  static String getExtName(String s, char split) {
        int i = s.indexOf(split);
        int leg = s.length();
        return (i > 0 ? (i + 1) == leg ? " " : s.substring(i+1, s.length()).toLowerCase() : " ");
    }

    public static MimeType getMimeType(String extName)
    {
        if(isInit == false)
        {
            init();
            isInit = true;
        }

        MimeType mimeType = ((MimeType)(extTable.get(extName)));
        return mimeType == null ? MimeType.APPLICATION_OCTET_STREAM : mimeType;
    }
}