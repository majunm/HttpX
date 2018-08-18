package interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;

import http.UCallback;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 上传进度拦截
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-08 15:10
 */
public class UploadProgressInterceptor implements Interceptor {

    private UCallback callback;

    public void injection(UCallback callback) {
        this.callback = callback;
        if (mUploadProgressRequestBody != null) {
            mUploadProgressRequestBody.injection(callback);
        }
    }

    public UploadProgressRequestBody mUploadProgressRequestBody;

    public UploadProgressInterceptor() {
        if (this.callback == null) {
            this.callback = new UCallback() {
                @Override
                public void onProgress(long currentLength, long totalLength, float percent) {

                }

                @Override
                public void onFail(int errCode, String errMsg) {

                }
            };
        }
    }

    public UploadProgressInterceptor(UCallback callback) {
        this.callback = callback;
        if (this.callback == null) {
            this.callback = new UCallback() {
                @Override
                public void onProgress(long currentLength, long totalLength, float percent) {

                }

                @Override
                public void onFail(int errCode, String errMsg) {

                }
            };
        }
        if (callback == null) {
            throw new NullPointerException("this callback must not null.");
        }
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null) {
            System.out.println("##上传进度拦截1##");
            return chain.proceed(originalRequest);
        }
        Request progressRequest = originalRequest.newBuilder()
                .method(originalRequest.method(),
                        mUploadProgressRequestBody = new UploadProgressRequestBody(originalRequest.body(), callback))
                .build();
        System.out.println("##上传进度拦截2##");
        return chain.proceed(progressRequest);
    }
}
