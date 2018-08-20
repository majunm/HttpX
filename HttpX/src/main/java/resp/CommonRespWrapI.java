package resp;

import android.os.Handler;

import com.vise.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import callbacks.ResultCallback;
import config.ResLibConfig;
import http.HttpRequestManager;
import interceptor.HttpInterceptor;
import loading.ILoadingI;

/**
 * 解析核心类
 */

public class CommonRespWrapI extends CommonRespWrapIs {

    // 很多东西扔给父类了,....
    public CommonRespWrapI(String currPathPostfix, Handler mHandler, ResultCallback<?> mCallback, ILoadingI mloading) {
        super(currPathPostfix, mHandler, mCallback, mloading);
    }

    // 很多东西扔给父类了,....
    public CommonRespWrapI(String currPathPostfix, Handler mHandler, ResultCallback<?> mCallback, ILoadingI mloading, HttpInterceptor mHttpInterceptor) {
        super(currPathPostfix, mHandler, mCallback, mloading, mHttpInterceptor);
    }

    public CommonRespWrapI(String requestTag, String currPathPostfix, Handler mHandler, ResultCallback<?> mCallback, ILoadingI mloading, HttpInterceptor mHttpInterceptor, int key) {
        super(requestTag, currPathPostfix, mHandler, mCallback, mloading, mHttpInterceptor, key);
    }

    /**
     * 需要拦截吗
     *
     * @param resp
     * @return
     */
    public boolean isNeedInterceptor(String resp) {
        code = parseCode(resp);
        switch (code) {
//            case HttpInterceptor.TOKEN_EXPIRE:
            case HttpInterceptor.KICKED_OFF_LINE:
//            case HttpInterceptor.ILLEGAL_TOKEN:
            case HttpInterceptor.PROHIBIT_USEDII:
            case HttpInterceptor.PROHIBIT_USED:
//            case HttpInterceptor.UN_LOGIN:
                return true;
        }
        return false;
    }

    /**
     * 只需要在这里处理响应体即可,成功响应<br/>
     * 解密|at here<br/>
     */
    @Override
    public void onNext(String resp) {
        super.onNext(resp);
        if (isCanPrintLog) {
            Logger.e(TAG, "解密前" + resp);
        }
        resp = HttpRequestManager.getInstance().decrypt(mRequestTag, resp);
//        if (ResLibConfig.USE_ENCRYPT) {
//            resp = AesEncryptionUtil.decrypt(resp);
//        }
        if (isCanPrintLog) {
            Logger.e(TAG, "解密后" + resp);
        }
        if (mHttpInterceptor != null) {
            if (ResLibConfig.DEBUG) {
                doParseTask(resp);
            } else {
                if (isNeedInterceptor(resp)) {
                    mHttpInterceptor.doInterceptor(parseCode(resp)); //token过期发生拦截器
                } else {
                    doParseTask(resp);
                }
            }

        } else {
            doParseTask(resp);
        }

    }

    private void doParseTask(String resp) {
        if (ResLibConfig.DEBUG) {
            Logger.e(TAG, "##网络返回_回调函数#" + mCallback);
        }
        resp = faultToleranceX(resp);
        if (mCallback != null) {
            if (mCallback.mType != null) {
                if (ResLibConfig.DEBUG) {
                    if (isCanPrintLog) {
                        Logger.e(TAG, "##网络返回数据类型#" + mCallback.mType);
                    }
                }
                if (mCallback.mType == String.class) {
                    paserToString(resp);
                } else {
                    parseToObjs(resp);
                }
            } else {
                throw new RuntimeException("不可达错误");
            }
        } else {
            // throw new RuntimeException("回调函数不能为空");
        }
    }


    /**
     * 尝试修正类型不统一错误
     */
    public String faultToleranceX(String resp) {
        switch (key) {
            default:
            case 0://String 跳出
            case 2://对象
                return resp;
            case 1:
                boolean error = false;
                int code = 0;
                String msg = "";
                boolean rebuild = false; // 重建json串
                JSONObject json = null;
                // 测试专用
                // resp = "{\"code\":200,\"msg\":\"成功\",\"data\":{\"goods_num\":\"0\"}}";
                try {
                    json = new JSONObject(resp);
                    code = json.optInt(CODE);
                    msg = json.optString(MSG);
                    json.getJSONArray(DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                    if (json != null) {
                        JSONObject obj = json.optJSONObject(DATA);
                        JSONObject newJson = new JSONObject();
                        if (obj != null) {
                            rebuild = true;
                            JSONArray array = new JSONArray();
                            try {
                                if (EMPTY_JSON.equals(newJson.toString())) {
                                    Logger.e(TAG, "重构前,发现空串,跳过");
                                    rebuild = false;
                                } else {
                                    array.put(0, obj);
                                    newJson.put(CODE, code);
                                    newJson.put(MSG, msg);
                                    newJson.put(DATA, array);
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                rebuild = false; // 重建失败了
                            }
                            if (rebuild) {
                                Logger.e(TAG, "重构前,尝试修正前........" + resp);
                                resp = newJson.toString();
                                Logger.e(TAG, "重构后,尝试修正后........" + resp);
                                return resp;
                            }
                        }
                    }
                }
                //返回:{"code":200,"msg":"成功","data":{}}|期望:{"code":200,"msg":"成功","data":[]}
                if (error) {
                    Logger.e(TAG, "返回错误,尝试修正前........" + resp);
                    HttpCommObjsResp result = new HttpCommObjsResp();
                    result.code = code;
                    result.msg = msg;
                    resp = HttpRequestManager.GSON.toJson(result);
                    Logger.e(TAG, "返回错误,尝试修正后........" + resp);
                }
                return resp;
        }
    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) {
        boolean error = false;
        int code = 0;
        String msg = "";
        boolean rebuild = false; // 重建json串
        String resp = "{\"code\":200,\"msg\":\"成功\",\"data\":{\"goods_num\":\"0\"}}";
        JSONObject json = null;
        try {
            json = new JSONObject(resp);
            code = json.optInt(CODE);
            msg = json.optString(MSG);
            json.getJSONArray(DATA);
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
            if (json != null) {
                JSONObject obj = json.optJSONObject(DATA);
                JSONObject newJson = new JSONObject();
                if (obj != null) {
                    rebuild = true;
                    JSONArray array = new JSONArray();
                    try {
                        array.put(0, obj);
                        newJson.put(CODE, code);
                        newJson.put(MSG, msg);
                        newJson.put(DATA, array);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        rebuild = false; // 重建失败了
                    }
                    if (rebuild) {
                        Logger.e("tst", "重构前,尝试修正前........" + resp);
                        resp = newJson.toString();
                        Logger.e("tst", "重构后,尝试修正后........" + resp);
                    }
                }
            }
        }
        //{"code":200,"msg":"成功","data":{}}
        if (error) {
            HttpCommObjsResp result = new HttpCommObjsResp();
            result.code = code;
            result.msg = msg;
            resp = HttpRequestManager.GSON.toJson(result);
        }
        System.out.println(resp);
    }
}

