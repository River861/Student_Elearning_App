package com.example.elearning.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    // 保存在手机里的SP文件名
    private static final String FILE_NAME = "elearning_sp";
    /* 保存数据 */
    public static void put(Context context, String key, String val) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, val);
        editor.apply();
    }
    /* 获取数据 */
    public static String get(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }
    /* 删除数据 */
    public static void remove(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
    /* 检查key对应的数据是否存在 */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

}