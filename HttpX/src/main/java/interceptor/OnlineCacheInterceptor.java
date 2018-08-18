package interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.vise.log.Logger;

import java.io.IOException;

import config.ResLibConfig;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @Description: 在线缓存拦截
 * maxAge ：设置最大失效时间，失效则不使用
 * <p>
 * maxStale ：设置最大失效时间，失效则不使用
 * <p>
 * max-stale在请求头设置有效，在响应头设置无效。
 * <p>
 * max-stale和max-age同时设置的时候，缓存失效的时间按最长的算
 */
public class OnlineCacheInterceptor implements Interceptor {
    private String cacheControlValue;

    public OnlineCacheInterceptor() {
        this(ResLibConfig.MAX_AGE_ONLINE);
    }

    public OnlineCacheInterceptor(int cacheControlValue) {
        this.cacheControlValue = String.format("max-age=%d", cacheControlValue);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        if (TextUtils.isEmpty(cacheControl) || cacheControl.contains("no-store") || cacheControl.contains("no-cache") || cacheControl
                .contains("must-revalidate") || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            Logger.e("http", "================在线缓存" + ResLibConfig.MAX_AGE_ONLINE + "秒================");
            return originalResponse.newBuilder()
                    .removeHeader("Cache-Control")
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, " + cacheControlValue)
                    .build();
        } else {
            return originalResponse;
        }
    }
}
