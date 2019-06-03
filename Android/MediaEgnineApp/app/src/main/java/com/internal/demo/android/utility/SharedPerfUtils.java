package com.internal.demo.android.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPerfUtils {
    private static final String PREFERENCES_NAME = "voip_pref";

    public static void setServerHost(Context context, String rootHost) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("root_host", rootHost).commit();
    }
    public static String getServerHost(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("root_host", "127.0.0.1");
    }

    public static void setServerPort(Context context, String rootPort) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("root_port", rootPort).commit();
    }
    public static String getServerPort(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("root_port", "80");
    }

    public static void setAppAccountId(Context context,String appAccountId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("app_account_id", appAccountId).commit();
    }
    public static String getAppAccountId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("app_account_id", null);
    }


    public static void setPhoneNumber(Context context,String callerNumber) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("user_phone_number", callerNumber).commit();
    }
    public static String getPhoneNumber(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("user_phone_number", null);
    }
    public static void setPassword(Context context,String account) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("user_password", account).commit();
    }
    public static String getPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("user_password", null);
    }
    public static void setAccountId(Context context,String accountId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("account_id", accountId).commit();
    }
    public static String getAccountId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("account_id", null);
    }

    public static void clear(Context context) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }
}
