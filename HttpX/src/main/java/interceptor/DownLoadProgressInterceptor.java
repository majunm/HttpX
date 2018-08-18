package interceptor;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import http.UCallback;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @Description: 下注进度拦截
 */
public class DownLoadProgressInterceptor implements Interceptor {

    private UCallback callback;
    private DownloadProgressRespBody mDownloadProgressRespBody;

    public void injection(UCallback callback) {
        this.callback = callback;
        if (mDownloadProgressRespBody != null) {
            mDownloadProgressRespBody.injection(callback);
        }
    }

    public DownLoadProgressInterceptor() {
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

    public DownLoadProgressInterceptor(UCallback callback) {
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
        // 拦截
        Response originalResponse = chain.proceed(chain.request());
        System.out.println("##下载进度拦截执行##");
        Log.e("x","##下载进度拦截执行##");
        // 包装响应体并返回
        return originalResponse
                .newBuilder()
                .body(mDownloadProgressRespBody = new DownloadProgressRespBody(originalResponse.body(),
                        callback)).build();
    }
}
