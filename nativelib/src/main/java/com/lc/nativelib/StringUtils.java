package com.lc.nativelib;

/**
 * created by lvchao 2023/5/1
 * describe:
 */
class StringUtils {

    public static String[] split(String propertyName, String s) {
        return propertyName.split(s);
    }

    public static String capitalize(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] subsentence = str.split(" ");//把字符串通过空格分成几个存入字符串数组
        String change = "";
        for (String s : subsentence) {
            String a = s.substring(0, 1);//取首部
            change = s.replace(a, a.toUpperCase());
            stringBuilder.append(change).append(" ");
        }
        return stringBuilder.toString();
    }
}
