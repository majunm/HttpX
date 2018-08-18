package interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import headers.HttpHeadersImpl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 通用参数<br/>
 */
public class HeadersInterceptor implements Interceptor {

    public void setHeaders(HttpHeadersImpl mHttpHeadersImpl) {
        if (mHttpHeadersImpl != null) {
            headers = mHttpHeadersImpl.generateHeaders();
        }
    }

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    Object tag;

    public void setRequestTags(Object tag) {
        this.tag = tag;
    }

    private Map<String, String> headers = new HashMap<>();

    public HeadersInterceptor() {

    }

    public HeadersInterceptor(Map<String, String> headers) {
        this.headers = headers;
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.clear();
//        this.headers.put("versionCode", "" + IUtil.obtainAppVersionCode(ResLibConfig.CONTEXT));
//        this.headers.put("version", IUtil.obtainAppVersion(ResLibConfig.CONTEXT));
//        this.headers.put("channel", "");
//        this.headers.put("imei", "");
//        this.headers.put("platform", "2");//1:ios 2:Android
//        this.headers.put("model", IUtil.MODEL);
//        this.headers.put("vendor", IUtil.BRAND);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        // builder.addHeader("Content-Type", "application/json;charset=UTF-8");
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }
        return chain.proceed(builder.build());
    }

}
