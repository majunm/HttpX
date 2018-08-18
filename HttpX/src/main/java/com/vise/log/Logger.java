package com.vise.log;

import android.util.Log;

public class Logger {
    /**
     * 设置tag的...
     *
     * @param tag
     * @param obj
     */
    public static void e(String tag, Object obj) {
        try {
            Log.e(tag, obj + "");
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
