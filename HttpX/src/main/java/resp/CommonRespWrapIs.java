package resp;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.vise.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import callbacks.ResultCallback;
import config.ResLibConfig;
import http.ApiException;
import http.HttpRequestManager;
import interceptor.HttpInterceptor;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import loading.ILoadingI;

/**
 */
public abstract class CommonRespWrapIs extends DisposableObserver<String> {

    public String mRequestTag; // 请求tag
    public boolean isCanPrintLog = false; // 可以打印log吗,默认可以的~
    /**
     * 空json串
     */
    public static final String EMPTY_JSON = "{}";
    public String mCurrentPathPostfix; // 请求后缀
    public Handler mHandler;
    public ILoadingI mLoading;
    public ResultCallback<?> mCallback;
    // public static final String CODE = "code";
    public static final String CODE = CommonResp.KEY_CODE;
    public static final String MSG = CommonResp.KEY_MSG;
    public static final String DATA = CommonResp.KEY_DATA;
    public static final int SUCCESS = CommonResp.SUCCESS;
    public String TAG;
    public int code;

    public HttpInterceptor mHttpInterceptor;
    private HttpRequestManager.RequestPairs mRequestPairs;

    public CommonRespWrapIs(String currPathPostfix, Handler mHandler,
                            ResultCallback<?> mCallback, ILoadingI mloading) {
        this.mCurrentPathPostfix = currPathPostfix;
        this.mHandler = mHandler;
        this.mCallback = mCallback;
        this.mLoading = mloading;
        this.TAG = getClass().getSimpleName();
    }

    public CommonRespWrapIs(String currPathPostfix, Handler mHandler,
                            ResultCallback<?> mCallback, ILoadingI mloading, HttpInterceptor mHttpInterceptor) {
        this.mCurrentPathPostfix = currPathPostfix;
        this.mHandler = mHandler;
        this.mCallback = mCallback;
        this.mLoading = mloading;
        this.TAG = getClass().getSimpleName();
        this.mHttpInterceptor = mHttpInterceptor;
    }

    public int key = 0; // key=0 string key = 1 数组 key =2 对象


    public CommonRespWrapIs(String requestTag, String currPathPostfix, Handler mHandler,
                            ResultCallback<?> mCallback, ILoadingI mloading, HttpInterceptor mHttpInterceptor, int key) {
        this.mRequestTag = requestTag;
        this.mCurrentPathPostfix = currPathPostfix;
        this.mHandler = mHandler;
        this.mCallback = mCallback;
        this.mLoading = mloading;
        this.TAG = getClass().getSimpleName();
        this.TAG = "http";
        this.mHttpInterceptor = mHttpInterceptor;
        this.key = key;
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (mLoading != null && !mLoading.isShowingI()) {
                mLoading.beginShow();
            }
            if (Looper.getMainLooper() == Looper.myLooper()) {
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRequestPairs = new HttpRequestManager.RequestPairs(mRequestTag, this);
        HttpRequestManager.getInstance().attach(mRequestPairs);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execErrorCallback(final Throwable e,
                                  final ResultCallback<?> callback, final ILoadingI mloading) {
        if (ResLibConfig.DEBUG) {
            Logger.e(TAG, "#######网络失败回调执行#######" + callback);
            Logger.e(TAG, "#######网络失败回调执行#######" + mHandler);
        }
        if (mHandler != null) {
            if (ResLibConfig.RUN_TEST_CASE) {
                errors(mloading, callback, e);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Logger.e(TAG, "#最终结果#");
                        errors(mloading, callback, e);
                    }
                });
            }
        }
    }

    private void errors(ILoadingI mloading, ResultCallback<?> callback, Throwable e) {
        dismiss(mloading);
        if (callback != null) {
            ApiException apiException = null;
            if (e != null) {
                if (e instanceof ApiException) {
                    apiException = (ApiException) e;
                } else {
                    apiException = new ApiException(e);
                }
            }
            callback.onError(apiException);
        }
    }


    public void dismiss(ILoadingI mloading) {
        try {
            if (mloading != null) {
                if (mloading.isShowingI()) {
                    mloading.beginDismiss();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable error) {
        HttpRequestManager.getInstance().detach(mRequestPairs);
        if (ResLibConfig.DEBUG) {
            Logger.e(TAG, "#######网络返回失败#######");
        }
        if (error != null) {
            printLog(error.getMessage() + "#errormsgs#");
        }
        execErrorCallback(error, mCallback, mLoading);
    }


    public void printLog(@NonNull String resp) {
        if (!isCanPrintLog) {
            // return; // 打印结果日志
        }
        try {
            String tag = mCurrentPathPostfix.split("/")[1];
            Logger.e(tag, resp);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String tag = mCurrentPathPostfix.split("/")[0];
                Logger.e(tag, resp);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        Logger.e(TAG, "===========================网络返回信息日志开始==============================");
        String mHttpPath = ResLibConfig.API_HOST + mCurrentPathPostfix;
        Logger.e(mRequestTag + "", mRequestTag + ":" + mHttpPath + "|" + resp + "|"); //日志
        Logger.e(TAG, "===========================网络返回信息日志结束==============================");
    }

    @Override
    public void onComplete() {
        dismiss(mLoading);
        Logger.e(mRequestTag, mRequestTag + "=================请求任务执行完毕=================");

    }

    /**
     * 只需要在这里处理响应体即可,成功响应<br/>
     */
    @Override
    public void onNext(String resp) {
        printLog(resp);
        HttpRequestManager.getInstance().detach(mRequestPairs);
    }

    public String parseMsg(String resp) {
        try {
            long start = System.currentTimeMillis();
            JSONObject json = new JSONObject(resp + "");
            String msg = json.optString(CommonRespWrapIs.MSG);
            long end = System.currentTimeMillis();
            return "错误原因:" + msg;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int parseCode(String resp) {
        try {
            long start = System.currentTimeMillis();
            JSONObject json = new JSONObject(resp + "");
            int code = json.optInt(CODE);
            long end = System.currentTimeMillis();
            return this.code = code;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.code = -1;
    }

    public boolean isSuccess(String resp) {
        return parseCode(resp) == CommonRespWrapIs.SUCCESS;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void execSuccessCallback(final Object o, final ResultCallback callback, final ILoadingI mloading) {
        if (mHandler != null) {
            if (ResLibConfig.RUN_TEST_CASE) { // 单元测试问题,handler不走啊
                oks(callback, o);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        oks(callback, o);
                    }
                });
            }
        }
    }

    private void oks(ResultCallback callback, Object o) {
        dismiss(mLoading);
        if (callback != null) {
            callback.onResponse(o);
        }
    }

    /**
     * string对象处理<br/>
     */
    public void paserToString(String resp) {
        if (mCallback.mType == String.class) {
            if (TextUtils.isEmpty(resp)) {
                Exception e = new Exception("返回结果为空!!!");
                execErrorCallback(e, mCallback, mLoading);
            } else {
                execSuccessCallback(resp, mCallback, mLoading);
            }
        }
    }

    /**
     * 泛型对象处理<br/>
     */
    public void parseToObjs(String resp) {
        if (TextUtils.isEmpty(resp)) {
            Exception e = new Exception("返回结果为空~");
            execErrorCallback(e, mCallback, mLoading);
        } else {
            try {
                long start = System.currentTimeMillis();
                Object obj = HttpRequestManager.GSON.fromJson(resp, mCallback.mType);
                long end = System.currentTimeMillis();
                Logger.e(TAG, "##解析耗时##" + (end - start) + "##毫秒##" + (end - start) / 1000
                        + "##秒##");
                if (obj == null) {
                    Exception e = new Exception(
                            "mGson.fromJson(finalStr,callback.mType) return null!");
                    execErrorCallback(e, mCallback, mLoading);
                } else {
                    execSuccessCallback(obj, mCallback, mLoading);
                }
            } catch (JsonSyntaxException e) {
                Exception ex = new Exception(
                        "|当前请求路径|" + mCurrentPathPostfix + "-||--->" + e);
                execErrorCallback(ex, mCallback, mLoading);
                e.printStackTrace();
                Logger.e(TAG, "#错误情况#" + e);
            }
        }
    }


    public void isCancelRequest() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dismiss(mLoading);
                    mCallback.onError(new ApiException(new Exception(mRequestTag + "请求已取消")));
                }
            });
        }
    }
}
