package callbacks;

import java.util.List;

import http.ApiException;
import resp.HttpCommObjsResp;

/**
 * 抽取data = List<Data>
 */

public class HttpCallbackArraysAdapt<T> extends ResultCallbackAdapt<HttpCommObjsResp<T>> {
    HttpCallback<List<T>> mHttpCallbackWrap;

    public HttpCallbackArraysAdapt(HttpCallback<T> mHttpCallback) {
        mHttpCallbackWrap = new HttpCallbackArraysWrap(mHttpCallback);
    }

    @Override
    public void doOnResponse(HttpCommObjsResp<T> response) {
        if (response.isSuccess()) {
            mHttpCallbackWrap.onResponse(response.data);
        } else {
            doOnError(createException(response.msg));
        }
    }

    @Override
    public void doOnError(ApiException ex) {
        mHttpCallbackWrap.onError(ex);
    }
}
