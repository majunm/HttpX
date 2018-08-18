package convert;

import com.vise.log.Logger;

import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * RequestBody 对请求体进行加密处理,暂已弃用
 * 指定Content-Type: application/json;charset=UTF-8
 */
public class IRequestBodyConverter2<T> implements Converter<T, RequestBody> {
    static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final MediaType MEDIA_TYPE = MediaType
            .parse("application/json; charset=" + UTF_8 + "");
    private static final String TAG = "IRequestBodyConverter2";
//
//    final Gson gson;
//    final TypeAdapter<T> adapter;
//
//    IRequestBodyConverter2(Gson gson, TypeAdapter<T> adapter) {
//        this.gson = gson;
//        this.adapter = adapter;
//    }

    @Override
    public RequestBody convert(T value) {
        String json = value.toString();
        Logger.e(TAG, "#json发起网络请求#" + json + "##");
        return RequestBody.create(MEDIA_TYPE, json);
    }

}
