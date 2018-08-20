package com.vise.log;

import android.util.Log;

import config.ResLibConfig;
import http.HttpRequestManager;

public class Logger {
    /**
     * 设置tag的...
     *
     * @param tag
     * @param obj
     */
    public static void e(String tag, Object obj) {
        try {
            HttpRequestManager.getInstance().createLogImpl().e(tag, obj);
        } catch (Exception e) {
            e.printStackTrace();
            if (ResLibConfig.DEBUG) {
                Log.e(tag, obj + "");
                System.out.println(obj);
            }
        }
    }
}
