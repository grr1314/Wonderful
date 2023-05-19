package com.lc.im;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by lvchao 2023/5/16
 * describe:
 */
public class Util {
    public static String parseHyMessageTxt(String msg) {
        String[] string = msg.split(":");
        String last = string[1];
        last = last.substring(1, last.length() - 1);
        return last;
    }
}
