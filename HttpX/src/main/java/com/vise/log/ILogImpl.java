package com.vise.log;

import android.util.Log;

import config.ResLibConfig;

public class ILogImpl implements ILog {
    public ILogImpl(PrintLogIntecepter mLogIntecepter) {
        this.mLogIntecepter = mLogIntecepter;
    }

    public PrintLogIntecepter mLogIntecepter;

    @Override
    public void e(String tag, Object obj) {
        if (mLogIntecepter != null) {
            mLogIntecepter.e(tag, obj);
            return;
        }
        if (ResLibConfig.DEBUG) {
            Log.e(tag, obj + "");
            System.out.println(obj);
        }
    }
}
