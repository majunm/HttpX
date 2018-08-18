package callbacks;

import java.util.List;

import http.ApiException;

/**
 */

public class HttpCallbackArraysWrap<T> implements HttpCallback<List<T>> {
    public HttpCallback mHttpCallback;

    public HttpCallbackArraysWrap(HttpCallback mHttpCallback) {
        this.mHttpCallback = mHttpCallback;
    }

    @Override
    public void onError(ApiException e) {
        if (mHttpCallback != null) {
            mHttpCallback.onError(e);
        }
    }

    @Override
    public void onResponse(List<T> response) {
        if (mHttpCallback != null) {
            mHttpCallback.onResponse(response);
        }
    }

}
