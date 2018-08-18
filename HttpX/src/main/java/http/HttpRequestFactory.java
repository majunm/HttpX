package http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.vise.log.Logger;

import java.io.File;
import java.util.Map;

import callbacks.HttpCallback;
import callbacks.HttpCallbackArraysAdapt;
import callbacks.HttpCallbackObjAdapt;
import callbacks.ResultCallback;
import config.ResLibConfig;
import io.reactivex.Observable;
import loading.ILoadingI;
import okhttp3.ResponseBody;
import request.CommonRequest;

/**
 */

public class HttpRequestFactory {
    private static final String TAG = "HttpRequestFactory";

    private HttpRequestFactory() {
    }

    /**
     * 尝试取消单个请求
     */
    public static void cancel(String mRequestTag) {
        HttpRequestManager.getInstance().tryCancelForTags(mRequestTag);
    }

    /**
     * 尝试取消多个请求
     */
    public static void cancel(String... mRequestTags) {
        HttpRequestManager.getInstance().tryCancelForTags(mRequestTags);
    }

    private static class HttpRequestFactoryHolder {
        private static final HttpRequestFactory INSTANCE = new HttpRequestFactory();
    }

    public static HttpRequestFactory getInstance() {
        return HttpRequestFactoryHolder.INSTANCE;
    }

    public static <T> void exec(Object obj, ResultCallback<T> mCallback, boolean isPost, ILoadingI mloading) {
        exec(obj, mCallback, isPost, mloading, null, null);
    }

    /**
     * @param obj        请求体
     * @param mCallback  回掉函数
     * @param isPost     是post请求吗?
     * @param mloading   加载框需要吗
     * @param mUCallback 下载|上传回掉需要吗
     * @param isDownLoad 是下载|上传 取值为null认为是普通请求,否则是下载或者上传
     * @param <T>
     */
    public static <T> void exec(Object obj, ResultCallback<T> mCallback, boolean isPost, ILoadingI mloading, UCallback mUCallback, Boolean isDownLoad) {
        if (obj == null) {
            throw new RuntimeException("请求体不能为空~");
        }
        String pathPostfix = "";
        if (obj instanceof CommonRequest) {
            long start = System.currentTimeMillis();
            CommonRequest common = (CommonRequest) obj;
            common.token = common.getToken();
            pathPostfix = common.postfix();
            long end = System.currentTimeMillis();
            Logger.e(TAG, "##解析耗时##" + (end - start) + "##毫秒##" + (end - start) / 1000
                    + "##秒##");
            if (TextUtils.isEmpty(pathPostfix)) {
                throw new RuntimeException("网络请求路径后缀不能为空!~");
            }
        } else {
            throw new RuntimeException("请求类型必须是CommonRequest 或者 其子类吆~");
        }
        if (!Network.isConnected(ResLibConfig.CONTEXT) && isPost) {
            if (mloading != null && mloading.isShowingI()) {
                mloading.beginDismiss();
            }
            if (mCallback != null) {
                mCallback.onError(new ApiException(new Exception("没有网络")));
            }
            Logger.e(TAG, "================没有网络,post请求退出================");
            return;
        }
        if (isPost) {
            HttpRequestManager.doPost(pathPostfix, obj, mCallback == null ? new ResultCallback() {


                @Override
                public void onError(ApiException e) {
                    Logger.e(TAG, "#返回错误#" + e.getMessage());
                }

                @Override
                public void onResponse(Object response) {
                    Log.d(TAG, "#返回结果是#" + response);
                }
            } : mCallback, mloading, mUCallback, isDownLoad);
        } else {
            HttpRequestManager.doGet(pathPostfix, obj, mCallback == null ? new ResultCallback() {
                @Override
                public void onError(ApiException e) {
                    Logger.e(TAG, "#返回错误#" + e.getMessage());
                }

                @Override
                public void onResponse(Object response) {
                    Logger.e(TAG, "#返回结果是#" + response);
                }
            } : mCallback, mloading);
        }

    }

    // Retrofit + Rxjava 简单封装,不需要知道返回值时,一般统计上报不需要
    public static <T> void exec(Object obj) {
        exec(obj, null, false);
    }

    public static <T> void doPost(Object obj, ResultCallback<T> mCallback) {
        exec(obj, mCallback, true);
    }

    public static <T> void doPost(Object obj, ResultCallback<T> mCallback, ILoadingI mILoadingI) {
        exec(obj, mCallback, true, mILoadingI);
    }

    public static <T> void doGet(Object obj, ResultCallback<T> mCallback) {
        exec(obj, mCallback, false);
    }

    public static <T> void doGet(Object obj, ResultCallback<T> mCallback, ILoadingI mILoadingI) {
        exec(obj, mCallback, false, mILoadingI);
    }

    public static <T> void exec(Object obj, ResultCallback<T> mCallback, boolean isPost) {
        exec(obj, mCallback, isPost, null);
    }

    public static void downFile(String url, Map<String, String> maps, UCallback mUCallback, ResultCallback<ResponseBody> mCallback) {
        HttpRequestManager.downFile(url, maps, mUCallback, mCallback);
    }

    public static void uploadFiles(String url, Map<String, String> maps, UCallback mUCallback, ResultCallback<ResponseBody> mCallback, File... files) {
        HttpRequestManager.uploadFile(url, maps, mUCallback, mCallback, files);
    }

    public static <T> void uploadFilesII(Object obj, ResultCallback<T> mCallback, UCallback mUCallback) {
        exec(obj, mCallback, true, null, mUCallback, false);
    }

    public static <T> void downFileII(Object obj, ResultCallback<T> mCallback, UCallback mUCallback) {
        exec(obj, mCallback, true, null, mUCallback, true);
    }

    //
    public static <T> void doPostII(Object obj, HttpCallback<T> mHttpCallback, boolean isArrays) {
        doPostII(obj, mHttpCallback, isArrays, null);
    }

    public static <T> void doPostII(Object obj, HttpCallback<T> mHttpCallback, ILoadingI mloading) {
        doPostArrays(obj, mHttpCallback, mloading);
    }

    public static <T> void doPostArrays(Object obj, HttpCallback<T> mHttpCallback, ILoadingI mloading) {
        doPostII(obj, mHttpCallback, true, mloading);
    }

    /**
     * @param obj
     * @param mHttpCallback
     * @param isArrays      = 默认是HttpCallbackArraysAdapt,如果是对象请传false
     * @param mloading
     * @param <T>
     */
    public static <T> void doPostII(Object obj, HttpCallback<T> mHttpCallback, boolean isArrays, ILoadingI mloading) {
        if (isArrays) {
            exec(obj, new HttpCallbackArraysAdapt<T>(mHttpCallback), true, mloading);
        } else {
            exec(obj, new HttpCallbackObjAdapt<T>(mHttpCallback), true, mloading);
        }
    }


    public static <T> Observable<T> runPostX(Object obj, ResultCallback<T> mCallback, boolean isPost, ILoadingI mloading) {
        if (obj == null) {
            throw new RuntimeException("请求体不能为空~");
        }
        String pathPostfix = "";
        if (obj instanceof CommonRequest) {
            long start = System.currentTimeMillis();
            CommonRequest common = (CommonRequest) obj;
            pathPostfix = common.postfix();
            long end = System.currentTimeMillis();
            Logger.e(TAG, "##解析耗时##" + (end - start) + "##毫秒##" + (end - start) / 1000
                    + "##秒##");
            if (TextUtils.isEmpty(pathPostfix)) {
                throw new RuntimeException("网络请求路径后缀不能为空!~");
            }
        } else {
            throw new RuntimeException("请求类型必须是CommonRequest 或者 其子类吆~");
        }

        if (!Network.isConnected(ResLibConfig.CONTEXT)) {
            // ToastTool.showNetisDead(ResLibConfig.CONTEXT);
            if (mloading != null && mloading.isShowingI()) {
                mloading.beginDismiss();
            }
            Logger.e(TAG, "#没有网络,退出#");
        } else {

        }
        Observable<T> result = HttpRequestManager.doPostX(pathPostfix, obj, mCallback, mloading);
        return result;
    }

    /**
     * 初始化http
     *
     * @param context
     * @return
     */
    public static HttpRequestManager doCreateHttpReqManager(Context context) {
        return doCreateHttpReqManager(context, "");
    }

    /**
     * @param context
     * @param apiHost
     * @return
     */
    public static HttpRequestManager doCreateHttpReqManager(Context context, String apiHost) {
        if (TextUtils.isEmpty(apiHost)) {
        } else {
            ResLibConfig.API_HOST = apiHost;
        }
        if (ResLibConfig.CONTEXT == null) {
            ResLibConfig.CONTEXT = context;
        }
        return HttpRequestManager.getInstance();
    }
}
