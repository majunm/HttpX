package callbacks;

import http.ApiException;

/**
 * T extends HttpCallback<T>
 */

public interface HttpCallback<T> {
    public abstract void onError(ApiException e);

    public abstract void onResponse(T response);
}
