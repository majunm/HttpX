package callbacks;

import http.ApiException;
import resp.HttpCommObjResp;

/**
 */

public class HttpCallbackObjsWrap<T> implements HttpCallback<HttpCommObjResp<T>> {
    public HttpCallback mHttpCallback;

    public HttpCallbackObjsWrap(HttpCallback mHttpCallback) {
        this.mHttpCallback = mHttpCallback;
    }

    @Override
    public void onError(ApiException e) {
        if (mHttpCallback != null) {
            mHttpCallback.onError(e);
        }
    }

    @Override
    public void onResponse(HttpCommObjResp<T> response) {
        if (mHttpCallback != null) {
            // mHttpCallback.onResponse(response.data);
        }
    }

//    @Override
//    public void onResponse(T response) {
//        if (mHttpCallback != null) {
//            mHttpCallback.onResponse(response);
//        }
//    }

}
