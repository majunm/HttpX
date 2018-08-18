package http;

/**
 */

public class HttpCommResps<T> extends DisposableObserver<T> {
    //============
    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
    //============
//    public Type mType;
//
//    public HttpCommResps() {
//        mType = getSuperclassTypeParameter(getClass());
//    }
//
//    public static Type getSuperclassTypeParameter(Class<?> subclass) {
//        Type superclass = subclass.getGenericSuperclass();
//        if (superclass instanceof Class) {
//            throw new RuntimeException("泛型异常");
//        }
//        ParameterizedType parameterized = (ParameterizedType) superclass;
//        return $Gson$Types
//                .canonicalize(parameterized.getActualTypeArguments()[0]);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mLoading != null) {
//            mLoading.beginShow();
//        }
//    }
//
//    @Override
//    public void onNext(T t) {
//        if (mCallback != null) {
//            if (mCallback.mType != null) {
//                System.out.println("#类型#" + mCallback.mType);
//                if (mCallback.mType == String.class) {
//                    paserToString((String) t);
//                } else {
//                    parseToObjs(t);
//                }
//            } else {
//                throw new RuntimeException("不可达错误");
//            }
//        } else {
//            // throw new RuntimeException("回调函数不能为空");
//        }
//    }
//
//    public void printLog(@NonNull String resp) {
////        if (CommonConfig.DEBUG) {
////            Logger.e(TAG, CommonConfig.API_HOST + mCurrentPathPostfix);
////            try {
////                String tag = mCurrentPathPostfix.split("/")[1];
////                Logger.e(tag, resp);
////            } catch (Exception e) {
////                e.printStackTrace();
////                try {
////                    String tag = mCurrentPathPostfix.split("/")[0];
////                    Logger.e(tag, resp);
////                } catch (Exception e1) {
////                    e1.printStackTrace();
////                }
////            }
////            System.out.println("|========|");
////            System.out.println(CommonConfig.API_HOST + mCurrentPathPostfix);
////            System.out.println(resp);
////            System.out.println("|========|");
////        }
//    }
//
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void execErrorCallback(final Throwable e,
//                                  final ResultCallback<?> callback, final ILoadingI mloading) {
//        if (mHandler != null) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    dismiss(mloading);
//                    if (callback != null) {
//                        ApiException apiException = null;
//                        if (e != null) {
//                            if (e instanceof ApiException) {
//                                apiException = (ApiException) e;
//                            } else {
//                                apiException = new ApiException(e);
//                            }
//                        }
//                        callback.onError(apiException);
//                    }
//                }
//            });
//        }
//    }
//
//    private void doParseTask(String resp) {
//        if (mCallback != null) {
//            if (mCallback.mType != null) {
//                System.out.println("#类型#" + mCallback.mType);
//                if (mCallback.mType == String.class) {
//                    paserToString(resp);
//                } else {
//                    parseToObjs(resp);
//                }
//            } else {
//                throw new RuntimeException("不可达错误");
//            }
//        } else {
//            // throw new RuntimeException("回调函数不能为空");
//        }
//    }
//
//    @Override
//    public void onError(Throwable error) {
//        if (error != null) {
//            printLog(error.getMessage() + "#errormsgs#");
//        }
//        execErrorCallback(error, mCallback, mLoading);
//    }
//
//    public void dismiss(ILoadingI mloading) {
//        try {
//            if (mloading != null) {
//                if (mloading.isShowingI()) {
//                    mloading.beginDismiss();
//                }
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onComplete() {
//        dismiss(mLoading);
//    }
//
//    public String mCurrentPathPostfix; // 请求后缀
//    public Handler mHandler;
//    public ILoadingI mLoading;
//    public ResultCallback<T> mCallback;
//    public static final String CODE = "code";
//    public static final String MSG = "msg";
//    public static final String DATA = "data";
//    public static final int SUCCESS = 1;
//    public String TAG;
//
//    public HttpCommResps(String mCurrentPathPostfix, Handler mHandler,
//                         ResultCallback<T> mCallback, ILoadingI mloading) {
//        this.mCurrPathPostfix = mCurrPathPostfix;
//        this.mHandler = mHandler;
//        this.mCallback = mCallback;
//        this.mLoading = mloading;
//        this.TAG = getClass().getSimpleName();
//    }
//
//
//    /**
//     * string对象处理<br/>
//     */
//    public void paserToString(String resp) {
//        if (mCallback.mType == String.class) {
//            if (TextUtils.isEmpty(resp)) {
//                Exception e = new Exception("返回结果为空!!!");
//                execErrorCallback(e, mCallback, mLoading);
//            } else {
//                execSuccessCallback(resp, mCallback, mLoading);
//            }
//        }
//    }
//
//    @SuppressWarnings({"unchecked", "rawtypes"})
//    public void execSuccessCallback(final T o, final ResultCallback<T> callback, final ILoadingI mloading) {
//        if (mHandler != null) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    dismiss(mLoading);
//                    if (callback != null) {
//                        callback.onResponse(o);
//                    }
//                }
//            });
//        }
//    }
//
//    /**
//     * 泛型对象处理<br/>
//     */
//    public void parseToObjs(T resp) {
//        if (resp == null) {
//            Exception e = new Exception("返回结果为空!!!");
//            execErrorCallback(e, mCallback, mLoading);
//        } else {
//            try {
//                long start = System.currentTimeMillis();
//                T t = HttpRequestManager.GSON.fromJson(resp, mCallback.mType);
//                long end = System.currentTimeMillis();
//                // Logger.log(TAG, "#类型是#" + mCallback.mType);
//                Logger.e("RESP", "##解析耗时##" + (end - start) + "##毫秒##" + (end - start) / 1000
//                        + "##秒##");
//                if (obj == null) {
//                    Exception e = new Exception(
//                            "mGson.fromJson(finalStr,callback.mType) return null!");
//                    execErrorCallback(e, mCallback, mLoading);
//                } else {
//                    execSuccessCallback(obj, mCallback, mLoading);
//                }
//            } catch (JsonSyntaxException e) {
//                Exception ex = new Exception(
//                        "|当前请求路径|" + mCurrentPathPostfix + "-||--->" + e);
//                execErrorCallback(ex, mCallback, mLoading);
//                e.printStackTrace();
//            }
//        }
//    }
}
