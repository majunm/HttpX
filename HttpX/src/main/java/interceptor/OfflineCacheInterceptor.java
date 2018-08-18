package interceptor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.vise.log.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import config.ResLibConfig;
import http.Network;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 离线缓存拦截
 * maxAge ：设置最大失效时间，失效则不使用
 * <p>
 * maxStale ：设置最大失效时间，失效则不使用
 * <p>
 * max-stale在请求头设置有效，在响应头设置无效。
 * <p>
 * max-stale和max-age同时设置的时候，缓存失效的时间按最长的算
 */
public class OfflineCacheInterceptor implements Interceptor {
    private Context context;
    private String cacheControlValue;
    private int cacheControlTime;

    public OfflineCacheInterceptor(Context context) {
        this(context, ResLibConfig.MAX_AGE_OFFLINE);
    }

    public OfflineCacheInterceptor(Context context, int cacheControlValue) {
        this.context = context;
        this.cacheControlTime = cacheControlValue;
        this.cacheControlValue = String.format("max-stale=%d", cacheControlValue);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (!Network.isConnected(context)) {
            int maxStale = cacheControlTime == 0 ? ResLibConfig.MAX_AGE_OFFLINE : cacheControlTime; // 离线时缓存保存时间,单位:秒
            CacheControl tempCacheControl = new CacheControl.Builder()
                    .onlyIfCached()
                    .maxStale(maxStale, TimeUnit.SECONDS)
                    .build();
            request = request.newBuilder()
                    .removeHeader("Cache-Control")
                    .removeHeader("Pragma")
                    .cacheControl(tempCacheControl)
                    .build();
            Logger.e("http", "======================离线缓存命中======================");
        }
        return chain.proceed(request);
    }
}
