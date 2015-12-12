package com.martingold.nfcreader.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.URLUtil;

/**
 * Created by martin on 26.10.15.
 */
public class Methods {
    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static boolean isUrlValid(String URL){
        if(!URL.startsWith("http://")){
            URL = "http://" + URL;
        }
        if(URL.contains("www.")){
            URL.replace("www.", "");
        }
        return URLUtil.isValidUrl(URL);
    }

    public static String getSharedPref(Context c, String key){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
        return settings.getString(key, "");
    }


    public static void setSharedPref(Context c, String key, String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
