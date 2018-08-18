package callbacks;


import com.google.gson.internal.$Gson$Types;
import com.vise.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import http.ApiException;
import resp.CommonRespWrapI;
import resp.CommonRespWrapIs;

/**
 */

public abstract class ResultCallback<T> {
    public Type mType;
    public int mRespType;

    public ResultCallback() {
        mType = getSuperclassTypeParameter(getClass());
        Logger.e("http", "=type=" + mType);
        System.out.println("=type=" + mType);
        if (mType == String.class) {
//            mRespType = RespType.TYPE_STRING;
        } else if (mType == RespType.class) {
//            mRespType = ((RespType)mType).optRespType();
        } else {
//            mRespType = RespType.TYPE_STRING;
        }
        System.out.println((mType == RespType.class) + "=当前类型是=========" + mRespType);
        System.out.println((mType instanceof RespType) + "=当前类型是=========" + mRespType);
    }

    public static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("泛型异常");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types
                .canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    // public abstract void onError(Call<T> request, Throwable e);
    public abstract void onError(ApiException e);

    public abstract void onResponse(T response);

    public static int parseCode(String resp) {
        try {
            long start = System.currentTimeMillis();
            JSONObject json = new JSONObject(resp);
            int code = json.optInt(CommonRespWrapIs.CODE);
            long end = System.currentTimeMillis();
            return code;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String parseMsg(String resp) {
        try {
            long start = System.currentTimeMillis();
            JSONObject json = new JSONObject(resp);
            String msg = json.optString(CommonRespWrapIs.MSG);
            long end = System.currentTimeMillis();
            return msg;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public ApiException createException(String msg) {
        return new ApiException(new Exception(msg));
    }

    public static String parseKey(String resp, String key) {
        //if (isSuccess(resp)) {
        try {
            long start = System.currentTimeMillis();
            JSONObject json = new JSONObject(resp);
            JSONObject obj = json.optJSONObject(CommonRespWrapIs.DATA);
            if (obj == null || CommonRespWrapI.EMPTY_JSON.equals(obj.toString())) {
                JSONArray arrays = json.optJSONArray(CommonRespWrapIs.DATA);
                if (arrays != null && arrays.length() > 0) {
                    // ....
                }
            }
            long end = System.currentTimeMillis();
            return obj.optString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        return "";
    }

    public static boolean isSuccess(String resp) {
        return parseCode(resp) == CommonRespWrapIs.SUCCESS;
    }


    /**
     * 获取第一级type
     */
    protected <T> Type getType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            finalNeedType = type;
        }
        return finalNeedType;
    }


    /**
     * 获取次一级type(如果有)
     *
     * @param t
     * @param <T>
     * @return
     */
    protected <T> Type getSubType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            if (type instanceof ParameterizedType) {
                finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                finalNeedType = type;
            }
        }
        return finalNeedType;
    }
}
