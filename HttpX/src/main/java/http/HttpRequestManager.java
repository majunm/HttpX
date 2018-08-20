package http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vise.log.ILog;
import com.vise.log.ILogImpl;
import com.vise.log.Logger;
import com.vise.log.PrintLogIntecepter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import callbacks.ResultCallback;
import config.ResLibConfig;
import convert.IGsonFactory2;
import headers.HttpHeadersImpl;
import interceptor.DownLoadProgressInterceptor;
import interceptor.HeadersInterceptor;
import interceptor.HttpInterceptor;
import interceptor.HttpLogInterceptor;
import interceptor.OfflineCacheInterceptor;
import interceptor.OnlineCacheInterceptor;
import interceptor.UploadProgressInterceptor;
import interceptor.UploadProgressRequestBody;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import loading.ILoadingI;
import okhttp3.Cache;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import proxy.IProxy;
import request.CommonRequest;
import resp.CommonRespWrapI;
import resp.CommonRespWrapIs;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import tool.GsonUtil;

/**
 * 请求核心代码
 */
public class HttpRequestManager {
    public static final String BASE_URL = ResLibConfig.API_HOST;
    public EncryptFuncs mEncryptsInternal;
    public static final Gson GSON = new Gson();
    public static final String TAG = HttpRequestManager.class.getSimpleName();
    private static final long FILE_READ_TIME_MIN = 5L;
    private static final long FILE_WRITE_TIME_MIN = 5L;
    private static final long DEFAULT_CONNECT_TIME_SEC = 10L;
    private static final long FILE_CONNECT_TIME_SEC = 100L;
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;
    private Retrofit mRetrofitII;
    private Retrofit mRetrofitIII;
    public int retryDelayMillis = ResLibConfig.DEFAULT_RETRY_DELAY_MILLIS;//请求失败重试间隔时间
    public int retryCount = ResLibConfig.DEFAULT_RETRY_COUNT;//重试次数
    public Handler mHandler;
    /**
     * 使用form表单提交吗 false = json true = form表单
     */
    public boolean isSubmitForm = false;
    private HttpInterceptor mHttpInterceptor;
    private HeadersInterceptor mHeadersInterceptor;

    /**
     * 解密,线程不安全吆~
     *
     * @param resp
     * @return
     */
    public String decrypt(String reqTag, String resp) {
        Logger.e(TAG, reqTag + ":解密前\n" + resp);
        if (!isSubmitForm && mEncryptsInternal != null && mEncryptsInternal.accept(reqTag)) {
            resp = mEncryptsInternal.decrypt(resp);
        }
        Logger.e(TAG, reqTag + ":解密后\n" + resp);
        return resp;
    }

    /**
     * isSubmitForm = true 表单提交
     * isSubmitForm = false json提交
     */
    public HttpRequestManager asSubmmitForm(boolean isSubmitForm) {
        this.isSubmitForm = isSubmitForm;
        return this;
    }

    public OkHttpClient createClient() {
        return createClient(DEF);
    }

    public static final int DEF = 1;
    public static final int UP_LOAD = 2;
    public static final int DOWN_LOAD = 3;
    DownLoadProgressInterceptor mDownsInterceptor;
    UploadProgressInterceptor mUploadInterceptor;

    public OkHttpClient createClient(int taskType) {
        HttpLogInterceptor mLogInterceptor = new HttpLogInterceptor();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        switch (taskType) {
            case DEF:
                File cacheDir = new File(ResLibConfig.CONTEXT.getCacheDir(), ResLibConfig.CACHE_NAME);
                //缓存的最大尺寸10m
                Cache cache = new Cache(cacheDir, ResLibConfig.CACHE_MAX_SIZE);
                builder.cache(cache);
                builder.connectTimeout(FILE_CONNECT_TIME_SEC, TimeUnit.SECONDS).writeTimeout(FILE_WRITE_TIME_MIN, TimeUnit.MINUTES).readTimeout(FILE_READ_TIME_MIN, TimeUnit.MINUTES);
                builder.addInterceptor(mHeadersInterceptor = new HeadersInterceptor());
                builder.addInterceptor(new OfflineCacheInterceptor(ResLibConfig.CONTEXT));
                builder.addNetworkInterceptor(mLogInterceptor);
                builder.addNetworkInterceptor(new OnlineCacheInterceptor());
                break;
            case UP_LOAD:
                builder = builder
                        .connectTimeout(FILE_CONNECT_TIME_SEC, TimeUnit.SECONDS).writeTimeout(FILE_WRITE_TIME_MIN, TimeUnit.MINUTES).readTimeout(FILE_READ_TIME_MIN, TimeUnit.MINUTES);
                addHeads(builder);
                builder.addInterceptor(mUploadInterceptor = new UploadProgressInterceptor());
                break;
            case DOWN_LOAD:
                builder = builder
                        .connectTimeout(FILE_CONNECT_TIME_SEC, TimeUnit.SECONDS).writeTimeout(FILE_WRITE_TIME_MIN, TimeUnit.MINUTES).readTimeout(FILE_READ_TIME_MIN, TimeUnit.MINUTES);
                addHeads(builder);
                builder.addNetworkInterceptor(mDownsInterceptor = new DownLoadProgressInterceptor());
                break;
        }
        OkHttpClient httpClient = builder.build();
        return httpClient;
    }

    private void addHeads(OkHttpClient.Builder builder) {
        if (mHeadersInterceptor == null) {
            builder.addInterceptor(mHeadersInterceptor = new HeadersInterceptor());
        } else {
            builder.addInterceptor(mHeadersInterceptor);
        }
    }

    private HttpRequestManager() {
        mHandler = new Handler(Looper.myLooper());
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(IGsonFactory2.create())
                //配置适配器工厂
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 异步吗,false
                .client(mOkHttpClient = createClient())
                .build();
    }

    /**
     * 加密,解密注入
     * 暂时只对json请求进行加密解密<br/>
     * {@link #isSubmitForm}
     *
     * @param mEncryptsInternal
     * @return
     */
    public HttpRequestManager asEncryptFuncs(EncryptFuncs mEncryptsInternal) {
        if (this.mEncryptsInternal == mEncryptsInternal) {
            return this;
        }
        this.mEncryptsInternal = mEncryptsInternal;
        return this;
    }

    public HttpRequestManager registerCallbacks(HttpInterceptor mHttpInterceptor, HttpHeadersImpl mHttpHeadersImpl) {
        if (mHttpInterceptor != null) {
            this.mHttpInterceptor = mHttpInterceptor;
        }
        if (mHeadersInterceptor != null && mHttpHeadersImpl != null) {
            mHeadersInterceptor.setHeaders(mHttpHeadersImpl);
        }
        return this;
    }

    public HttpRequestManager registerCallbacks(HttpHeadersImpl mHttpHeadersImpl) {
        return registerCallbacks(null, mHttpHeadersImpl);
    }

    public HttpRequestManager registerCallbacks(HttpInterceptor mHttpInterceptor) {
        return registerCallbacks(mHttpInterceptor, null);
    }

    private static final class HttpManagerHolder {
        private static final HttpRequestManager INSTANCE = new HttpRequestManager();
    }

    public static HttpRequestManager getInstance() {
        return HttpManagerHolder.INSTANCE;
    }

    ILog impl;
    public PrintLogIntecepter mPrintLogIntecepter;

    public HttpRequestManager asPrintLogIntecepter(PrintLogIntecepter mPrintLogIntecepter) {
        this.mPrintLogIntecepter = mPrintLogIntecepter;
        return this;
    }

    public ILog createLogImpl() {
        if (impl == null) {
            impl = (ILog) IProxy.of().bind(new ILogImpl(mPrintLogIntecepter));
        }
        return impl;
    }

    public HttpRequestManager init(Context context) {
        return this;
    }

    public static void doPost(String pathPostfix, Object src, final ResultCallback<?> mCallback) {
        getInstance().runPost(pathPostfix, src, mCallback);
    }

    public static void doPost(String pathPostfix, Object src, final ResultCallback<?> mCallback, ILoadingI mloading, UCallback mUCallback, Boolean isDownLoad) {
        getInstance().runPost(pathPostfix, src, mCallback, mloading, mUCallback, isDownLoad);
    }

    public static void doPost2(String pathPostfix, Object src, final ResultCallback<?> mCallback, ILoadingI mloading) {
        getInstance().runPost2(pathPostfix, src, mCallback, mloading);
    }

    public static void doGet(String pathPostfix, Object src, final ResultCallback<?> mCallback) {
        getInstance().runGet(pathPostfix, src, mCallback);
    }

    public static void doGet(String pathPostfix, Object src, final ResultCallback<?> mCallback, ILoadingI mloading) {
        getInstance().runGet(pathPostfix, src, mCallback, mloading);
    }

    public String mRequestTag = "";
    public boolean mSkipReqParams = false;

    public String reqParams(Object src) {
        String json = "";
        if (src != null && src instanceof String) {
            json = (String) src;
        } else {
            if (src == null) {
            } else {
                json = GSON.toJson(src);
            }
        }
        if (mSkipReqParams) {
            Logger.e(TAG, "#劳资不要请求参数#");
        } else {
            json = encrypt(mRequestTag, json);
        }
        return json;
    }

    private String encrypt(String reqTag, String json) {
        return encrypt(reqTag, json, false);
    }

    public String encrypt(String reqTag, String json, boolean force) {
        Logger.e(TAG, reqTag + ":请求参数加密前\n" + json);
        if ((!isSubmitForm || force) && mEncryptsInternal != null && mEncryptsInternal.accept(reqTag)) {
            json = mEncryptsInternal.encrypt(json);
        }
        Logger.e(TAG, reqTag + ":请求参数加密后\n" + json);
        return json;
    }

    /**
     * 切换线程<br/>
     */
    ObservableTransformer<String, String> mTransformer = new ObservableTransformer<String, String>() {

        @Override
        @NonNull
        public ObservableSource<String> apply(@NonNull Observable<String> apply) {
            return apply.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            // .observeOn(AndroidSchedulers.mainThread()).retryWhen(new RetryFunc(retryCount, retryDelayMillis));
        }

    };

    public <T> void runGet(String pathPostfix, Object src, final ResultCallback<T> mCallback) {
        runGet(pathPostfix, src, mCallback, null);
    }

    public String mCurrPathPostfix;

    public <T> void runGet(String pathPostfix, Object src, final ResultCallback<T> mCallback, ILoadingI mloading) {
        optRequestTag(src);
        String json = reqParams(src);
        mCurrPathPostfix = pathPostfix;
        Api api = generateApi();
        if (isSubmitForm) {
//            exec(api.runGetII(pathPostfix, generateform(json)), mCallback, mloading);
            exec(api.runGetII(pathPostfix, mSkipReqParams ? new HashMap<String, Object>() : generateQueryMap(json)), mCallback, mloading);
            mSkipReqParams = false;
        } else {
            exec(api.runGet(pathPostfix, json), mCallback, mloading);
        }
    }

    public Api mApi;

    public Api generateApi() {
        if (mApi == null) {
            mApi = mRetrofit.create(Api.class);
        }
        return mApi;
    }

    public ApiExtd mApiExtd;

    public ApiExtd generateApiExtd(boolean isUpload) {
        if (isUpload && mRetrofitII == null) {
            mRetrofitII = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(IGsonFactory2.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 异步吗,false
                    .client(createClient(UP_LOAD))
                    .build();
        } else if (!isUpload && mRetrofitIII == null) {
            mRetrofitIII = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(IGsonFactory2.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 异步吗,false
                    .client(createClient(DOWN_LOAD))
                    .build();
        }
        mApiExtd = isUpload ? mRetrofitII.create(ApiExtd.class) : mRetrofitIII.create(ApiExtd.class);
        return mApiExtd;
    }

    public <T> CommonRespWrapI exec(Observable<String> obj, final ResultCallback<T> mCallback, final ILoadingI mloading) {
        CommonRespWrapI mCommonRespWrapI = null;
        Type mType = mCallback.mType;
        String s = mType.toString();
        int key = 0;
        if (s.contains("HttpCommonObjsResp")) {
            key = 1;
        } else if (s.contains("HttpCommonObjResp")) {
            key = 2;
        } else {
            key = 0;
        }
        Logger.e(TAG, "#请求后缀名#" + mCurrPathPostfix + "");
        Logger.e("http", s + "|返回类型是:" + key);
        if (mHttpInterceptor != null) {
            if (HttpInterceptor.AUTO_INCREMENT.intValue() >= 1) { // 被其它设备登录
                if (runOnlyOnece) {
                    // 不会发生网络请求,当token过期,所有接口都不走的~
                    Logger.e("http", "token过期,不执行网络请求" + mCurrPathPostfix);
                    if (ResLibConfig.DEBUG) {
                        HttpInterceptor.AUTO_INCREMENT.decrementAndGet();//debug模式放行
                    }
                    if (mJumpPool != null) {
                        for (String postfix : mJumpPool) {
                            if (postfix.equals(mCurrPathPostfix)) {
                                Logger.e("http", "token过期,不执行网络请求,但是我们是特别的几位");
                                obj.compose(mTransformer).retryWhen(new RetryFunc(retryCount, retryDelayMillis))
                                        .subscribe(mCommonRespWrapI = new CommonRespWrapI(mRequestTag, mCurrPathPostfix, mHandler, mCallback, mloading, mHttpInterceptor, key));
                                return mCommonRespWrapI;
                            }
                        }
                    }
                    if (mCallback != null) {
                        mCallback.onError(new ApiException(new Exception(""))); // 设备登录后,不执行网络请求
                    }
                } else {
                    Logger.e("http", "token过期,继续执行网络请求");
                    obj.compose(mTransformer).retryWhen(new RetryFunc(retryCount, retryDelayMillis))
                            .subscribe(mCommonRespWrapI = new CommonRespWrapI(mRequestTag, mCurrPathPostfix, mHandler, mCallback, mloading, mHttpInterceptor, key));
                }
            } else {
//                if (VOTE_REQ_POSTFIX.equals(mCurrPathPostfix) || COMMENT_REQ_POSTFIX.equals(mCurrPathPostfix)) {
//
//                }
                for (String postfix : mLoginJumpPool) {
                    if (postfix.equals(mCurrPathPostfix)) {
//                        if (!LocalSaveServHelper.isLogin(ResLibConfig.CONTEXT)) {
//                            mHttpInterceptor.doInterceptor(HttpInterceptor.UN_LOGIN); // 登录拦截
//                            return;
//                        }
                    }
                }
                obj.compose(mTransformer).retryWhen(new RetryFunc(retryCount, retryDelayMillis))
                        .subscribe(mCommonRespWrapI = new CommonRespWrapI(mRequestTag, mCurrPathPostfix, mHandler, mCallback, mloading, mHttpInterceptor, key));
            }
        } else {
            obj.compose(mTransformer).retryWhen(new RetryFunc(retryCount, retryDelayMillis))
                    .subscribe(mCommonRespWrapI = new CommonRespWrapI(mRequestTag, mCurrPathPostfix, mHandler, mCallback, mloading, mHttpInterceptor, key));
        }
        return mCommonRespWrapI;
    }

    //注册
    public static String REGISTER_REQ_POSTFIX = "member/register-by-sms";
    //验证码
    public static String VERIFY_CODE_REQ_POSTFIX = "passport/send-sms-code";
    //登录
    public static String LOGIN_REQ_POSTFIX = "member/login-by-password";
    //忘记密码/重置密码
    public static String RESET_PWD_REQ_POSTFIX = "member/re-passport";
    // 点赞/未点赞拦截
    public static String VOTE_REQ_POSTFIX = "homework/vote";
    public static String COMMENT_REQ_POSTFIX = "comment/add";
    public static String OPT_USERINFOS_REQ_POSTFIX = "member/info";
    public static String OPT_HOME_REQ_POSTFIX = "home-page/view";
    public static String OPT_HOME_BANNER_REQ_POSTFIX = "home-page/banner";
    public String[] mJumpPool = {
            REGISTER_REQ_POSTFIX,
            LOGIN_REQ_POSTFIX,
            RESET_PWD_REQ_POSTFIX,
            VERIFY_CODE_REQ_POSTFIX,
            OPT_USERINFOS_REQ_POSTFIX,
            VOTE_REQ_POSTFIX,
            COMMENT_REQ_POSTFIX,
            OPT_HOME_REQ_POSTFIX,
            OPT_HOME_BANNER_REQ_POSTFIX,
    };

    /**
     * 网络请求拦截,需要验证登录<br/>
     */
    public String[] mLoginJumpPool = {
            VOTE_REQ_POSTFIX,
            COMMENT_REQ_POSTFIX,
    };
    public boolean runOnlyOnece = true;

    public <T> void runPost(String pathPostfix, Object src, final ResultCallback<T> mCallback) {
        runPost(pathPostfix, src, mCallback, null);
    }

    public <T> void runPost(String pathPostfix, Object src, final ResultCallback<T> mCallback, ILoadingI mloading, UCallback mUCallback, Boolean isDownLoad) {
        optRequestTag(src);
        String json = reqParams(src);
        mCurrPathPostfix = pathPostfix;
        Api api = generateApi();
        if (isSubmitForm) {
            if (isDownLoad == null || mUCallback == null) {
                exec(api.runPostII(pathPostfix, generateform(json)), mCallback, mloading);
            } else {
                if (isDownLoad) {
                    mApiExtd = generateApiExtd(false);
                    Logger.e(TAG, "#下载路径#" + pathPostfix + "|");
                    exec(mApiExtd.downFileII(pathPostfix), mCallback, mloading);
                } else {
                    mApiExtd = generateApiExtd(true);
                    Logger.e(TAG, "#上传路径#" + pathPostfix + "|");
                    exec(mApiExtd.uploadFilesII(pathPostfix, generateFilesform(json)), mCallback, mloading);
                }
            }
        } else {
            exec(api.runPost(pathPostfix, json), mCallback, mloading);
        }

    }

    /**
     * 获取请求tag
     *
     * @param src
     */
    private void optRequestTag(Object src) {
        if (src != null && src instanceof CommonRequest) {
            CommonRequest common = (CommonRequest) src;
            mSkipReqParams = common.skipParams();
            ReqTags mObtainPath;
            try {
                mObtainPath = common.getClass().getAnnotation(
                        ReqTags.class);
                if (mObtainPath != null) {
                    mRequestTag = mObtainPath.value(); // tag
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public <T> void runPost(String pathPostfix, Object src, final ResultCallback<T> mCallback, ILoadingI mloading) {
        runPost(pathPostfix, src, mCallback, null, null, null);
    }

    public Map<String, Object> generateQueryMap(String json) {
        Map<String, Object> map = GsonUtil.fromJson(json,
                new TypeToken<Map<String, Object>>() {
                });
        Logger.e(TAG, "#构造get请求map#" + map + "\n");
        return map;
    }

    /**
     * 构造表单
     *
     * @param json
     * @return
     */
//    public Map<String, Object> generateform(String json) {
    public FormBody generateform(String json) {
        Map<String, Object> map = GsonUtil.fromJson(json,
                new TypeToken<Map<String, Object>>() {
                });
        Logger.e(TAG, "#表单构造#" + map + "\n");
        Logger.e("http", "=========表单开始=========");
        FormBody.Builder bodys = new FormBody.Builder();//Builder||FormBody
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            bodys.add(key, value);
            Logger.e(TAG, "[" + key + "]=[" + value + "]\n");
        }
        FormBody forms = bodys.build();
        Logger.e("http", "=========表单结束=========");
//        return map;
        return forms;
    }

    /**
     * 构造表单
     *
     * @param json
     * @return
     */
//    public Map<String, Object> generateform(String json) {
    public List<MultipartBody.Part> generateformII(String json) {
        Map<String, Object> map = GsonUtil.fromJson(json,
                new TypeToken<Map<String, Object>>() {
                });
        Logger.e(TAG, "#表单构造#" + map + "\n");
        List<MultipartBody.Part> forms = new ArrayList<MultipartBody.Part>();
        Logger.e("http", "=========表单开始=========");
        FormBody.Builder bodys = new FormBody.Builder();//Builder||FormBody
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            bodys.add(key, value);
            RequestBody body = RequestBody.create(MediaTypes.APPLICATION_FORM_URLENCODED_TYPE, value);
            MultipartBody.Part part = MultipartBody.Part.create(body);
            // MultipartBody.Part part = MultipartBody.Part.createFormData(key, "", body);
            forms.add(part);
            Logger.e(TAG, "[" + key + "]=[" + value + "]\n");
        }
        FormBody formBody = bodys.build();
        Logger.e("http", "=========表单结束=========");
//        return map;
        return forms;
    }

    public List<MultipartBody.Part> generateFilesform(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> map = GSON.fromJson(json, type);
        Logger.e(TAG, "#表单构造#" + map + "\n");
        List<MultipartBody.Part> forms = new ArrayList<MultipartBody.Part>();
        Logger.e("http", "=========文件表单开始=========");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            File file = new File(value.toString());
            if (file != null && file.length() > 0) {
                RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
                forms.add(part);
            } else {
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, value + "");
                forms.add(part);
            }
            Logger.e(TAG, "[" + key + "]=[" + value + "]\n");
        }
        Logger.e("http", "=========文件表单结束=========");
        return forms;
    }

    /**
     * @param pathPostfix
     * @param src
     * @param mCallback
     * @param mloading
     * @param <T>
     */
    public <T> void runPost2(String pathPostfix, Object src, final ResultCallback<T> mCallback, ILoadingI mloading) {
    }

    public <T> Observable<T> runPostX(String pathPostfix, Object src, final ResultCallback<T> mCallback, ILoadingI mloading) {
        optRequestTag(src);
        String json = reqParams(src);
        Logger.e(TAG, "#请求后缀名#" + pathPostfix + "\n#请求参数#" + json);
        mCurrPathPostfix = pathPostfix;
        Api api = generateApi();
        String url = "http://192.168.7.142:8094/" + pathPostfix;
        //return execX(api.runPost2(url, json), mCallback, mloading);
        return null;
    }

    public static <T> Observable<T> doPostX(String pathPostfix, Object src, final ResultCallback<T> mCallback, ILoadingI mloading) {
        return getInstance().runPostX(pathPostfix, src, mCallback, mloading);
    }

    public <T> Observable<T> execX(Observable<String> obj, final ResultCallback mCallback, final ILoadingI mloading) {
        //obj.compose(mTransformer)
        //       .subscribe(new CommonRespWrap(mCurrPathPostfix, mHandler, mCallback, mloading));
        if (Network.isConnected(ResLibConfig.CONTEXT)) {
        } else {
        }
        // mTransformerX = new ObservableTransformerI(getType());
        //Observable<T> xx = (Observable<T>) obj.compose(mTransformerX).retryWhen(new RetryFunc(retryCount, retryDelayMillis));
        //xx.subscribe(new CommonRespWrapI(mCurrPathPostfix, mHandler, mCallback, mloading));
        return null;
    }

    protected List<Interceptor> interceptors = new ArrayList<>();//局部请求的拦截器
    protected List<Interceptor> networkInterceptors = new ArrayList<>();//局部请求的网络拦截器
    protected String baseUrl;//基础域名
    protected Object tag;//请求标签
    protected long readTimeOut;//读取超时时间
    protected long writeTimeOut;//写入超时时间
    protected long connectTimeOut;//连接超时时间
    protected boolean isHttpCache;//是否使用Http缓存

    /**
     * 尝试取消所有请求任务,不推荐
     */
    public static void doCancelHttpTask() {
        OkHttpClient mOkHttpClient = getInstance().mOkHttpClient;
        if (mOkHttpClient != null) {
            Logger.e(TAG, "#取消网络请求任务#");
            try {
                mOkHttpClient.dispatcher().cancelAll();
                for (okhttp3.Call call : mOkHttpClient.dispatcher().queuedCalls()) {
                    call.cancel();
                }
                for (okhttp3.Call call : mOkHttpClient.dispatcher().runningCalls()) {
                    call.cancel();
                }
            } catch (Exception e) {
                Logger.e(TAG, "#取消网络请求任务#" + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据tag,取消请求,不推荐
     *
     * @param tag
     */
    public void doCancelHttpTask(Object tag) {
        try {
            Dispatcher dispatcher = getInstance().mOkHttpClient.dispatcher();
            synchronized (dispatcher) {
                for (okhttp3.Call call : dispatcher.queuedCalls()) {
                    if (tag.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
                for (okhttp3.Call call : dispatcher.runningCalls()) {
                    if (tag.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downFile(String url, Map<String, String> maps, UCallback mUCallback, ResultCallback<ResponseBody> mCallback) {
        getInstance().downOrUploadFileTask(url, maps, mUCallback, mCallback, true);
    }

    public static void uploadFile(String url, Map<String, String> maps, UCallback mUCallback, ResultCallback<ResponseBody> mCallback, File... files) {
        getInstance().downOrUploadFileTask(url, maps, mUCallback, mCallback, false, files);
    }

    public static void uploadFileII(String url, UCallback mUCallback, ResultCallback<?> mCallback, File... files) {
        getInstance().downOrUploadFileTaskII(url, mUCallback, mCallback, false, files);
    }

    protected List<MultipartBody.Part> multipartBodyParts = new ArrayList<>();
    protected UCallback mUploadCallback;//上传进度回调

    public void downOrUploadFileTaskII(String src, UCallback mUCallback, final ResultCallback<?> mCallback, boolean isDownLoad, File... files) {
    }

    public void downOrUploadFileTask(String url, Map<String, String> maps, UCallback mUCallback, final ResultCallback<ResponseBody> mCallback, boolean isDownLoad, File... files) {
        mUploadCallback = mUCallback;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (isDownLoad) {
            if (mUploadCallback != null) {
                // newBuilder.addNetworkInterceptor(new UploadProgressInterceptor(mUploadCallback));
                builder.addNetworkInterceptor(new DownLoadProgressInterceptor(mUploadCallback));
                // builder.addInterceptor(new DownLoadProgressInterceptor(mUploadCallback));
            }
        }
        OkHttpClient mHttpClient = builder.retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS).build();
        // generateLocalConfig();
        Api api = new Retrofit.Builder()
                .baseUrl(url)
                .client(mHttpClient)
                .addConverterFactory(IGsonFactory2.create())
                //配置适配器工厂
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(Api.class);
        Observable<ResponseBody> obs = null;
        if (isDownLoad) {
            // obs = api.downFile(url, maps);
        } else {
            if (files != null && files.length != 0) {
                int index = 0;
                for (File file : files) {
                    addFile("key" + index, file, mUCallback);
                    index += 1;
                }
            }
//            obs = api.uploadFiles(url, multipartBodyParts);
        }
        obs.compose(mTransformerII)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("执行onSubscribe");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        System.out.println("执行成功" + responseBody);
                        if (mCallback != null) {
                            mCallback.onResponse(responseBody);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("执行出错" + e);
                        if (mCallback != null) {
                            mCallback.onError(new ApiException(e));
                        }
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("执行完毕");
                    }
                });
    }

    /**
     * 变身<br/>
     */
    ObservableTransformer<ResponseBody, ResponseBody> mTransformerII = new ObservableTransformer<ResponseBody, ResponseBody>() {

        @Override
        @NonNull
        public ObservableSource<ResponseBody> apply(@NonNull Observable<ResponseBody> apply) {
            return apply.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

    };

    public HttpRequestManager addFile(String key, File file, UCallback callback) {
        if (key == null || file == null) {
            return this;
        }
        RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, file);
        if (callback != null) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), uploadProgressRequestBody);
            this.multipartBodyParts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
            this.multipartBodyParts.add(part);
        }
        return this;
    }

    List<RequestPairs> mRequestPool = new ArrayList<>();

    public static class RequestPairs {
        public String key;
        public Disposable value;

        public RequestPairs(String key, Disposable value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "RequestPairs{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    public HttpRequestManager attach(RequestPairs mRequestPairs) {
        if (mRequestPool != null && mRequestPairs != null) {
            mRequestPool.add(mRequestPairs);
        }
        Logger.e(TAG, "#当前请求#" + mRequestPairs);
        Logger.e(TAG, "#当前请求池#" + mRequestPool);
        return this;
    }

    public HttpRequestManager detach(RequestPairs mRequestPairs) {
        Logger.e(TAG, "#当前请求池处理前#" + mRequestPool);
        if (mRequestPool != null && mRequestPairs != null) {
            boolean remove = mRequestPool.remove(mRequestPairs);
            Logger.e(TAG, "##" + remove + "##");
        }
        Logger.e(TAG, "#当前请求池处理后#" + mRequestPool);
        return this;
    }

    /**
     * 取消多个请求
     *
     * @param mRequestTags
     */
    public void tryCancelForTags(final String... mRequestTags) {
        if (mRequestTags != null && mRequestTags.length > 0) {
            for (String mRequestTag : mRequestTags) {
                tryCancelForTags(mRequestTag);
            }
        }
    }

    /**
     * 根据tag,取消请求
     * 必须去调用取消请求,把加载框干掉(如果有加载框的话),因为当发生网络取消,observer生命周期函数
     * onNext onError onComplete 方法不在执行
     * 具体参见{@link retrofit2.adapter.rxjava2.CallEnqueueObservable }
     * onResponse,onFailure函数  取消就返回
     *
     * @param tag
     */
    public void tryCancelForTags(final String mRequestTag) {
        try {
            if (mRequestPool != null) {
                for (int i = mRequestPool.size() - 1; i >= 0; i--) {
                    RequestPairs mRequestPairs = mRequestPool.get(i);
                    if ((mRequestPairs.key + "").equals(mRequestTag)) {
                        Logger.e(TAG, "尝试取消请求:" + mRequestTag + "#命中#");
                        Disposable mDisposableObserver = mRequestPairs.value;
                        if (mDisposableObserver != null && !mDisposableObserver.isDisposed()) {
                            mDisposableObserver.dispose();
                            try {
                                if (mDisposableObserver instanceof CommonRespWrapIs) {
                                    final CommonRespWrapIs result = (CommonRespWrapIs) mDisposableObserver;
                                    result.isCancelRequest();
                                }
                            } catch (Exception e) {
                            }
                            Logger.e(TAG, "##取消该请求##");
                        } else {
                            Logger.e(TAG, "##该请求已经死了##");
                        }
                        mRequestPool.remove(i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
