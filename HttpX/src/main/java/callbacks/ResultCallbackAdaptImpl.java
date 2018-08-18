package callbacks;

import com.vise.log.Logger;

import http.ApiException;

/**
 */

public abstract class ResultCallbackAdaptImpl<T> extends ResultCallbackAdapt<T> {

    @Override
    public abstract void doOnResponse(T response);

    @Override
    public void doOnError(ApiException ex) {
        Logger.e(TAG, ex);
    }
}
