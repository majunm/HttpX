package callbacks;

import http.ApiException;
import resp.HttpCommObjResp;

/**
 * 抽取data
 */

public class HttpCallbackObjAdapt<T> extends ResultCallbackAdapt<HttpCommObjResp<T>> {
    public HttpCallback mHttpCallback;

    public HttpCallbackObjAdapt(HttpCallback mHttpCallback) {
//        this.mHttpCallback = new HttpCallbackObjsWrap(mHttpCallback);
        this.mHttpCallback = mHttpCallback;
    }

    @Override
    public  void doOnResponse(HttpCommObjResp<T> response) {
        if (response.isSuccess()) {
            System.out.println("data=" + response.data);
            try {
                mHttpCallback.onResponse(response.data);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("e=" + e);
            }
//            T data = response.data; // 泛型擦除
            mHttpCallback.onResponse(response);
        } else {
            doOnError(createException(response.msg));
        }
    }

    @Override
    public void doOnError(ApiException ex) {
        mHttpCallback.onError(ex);
    }
}
/**
 * Class cls = getClass();
 * Method method = cls.getMethod("doOnResponse", HttpCommonObjResp.class);
 * Type[] type=  method.getGenericParameterTypes();
 * ParameterizedType t = (ParameterizedType)type[0];
 * System.out.println("subType=====================");
 * System.out.println(t.getActualTypeArguments()[0]);//
 * System.out.println(t.getRawType());//
 * System.out.println("subType======================");
 */