package http;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 */

public interface Api2 {
    public final String KEY = "data";
    public final String PATH = "path";

    @FormUrlEncoded
    @POST("{path}")
    public Observable<String> runPost(@Path(value = PATH, encoded = true) String path,
                                      @Field(KEY) String json);

    @GET("{path}")
    public Observable<String> runGet(@Path(value = PATH, encoded = true) String path,
                                     @Query(KEY) String json);

    @Streaming
    @GET
    Observable<ResponseBody> downFile(@Url() String url, @QueryMap Map<String, String> maps);

    /**
     * 最开始用这个
     *
     * @param url
     * @param parts
     * @return
     * @Multipart
     * @POST("{path}") public Observable<String> runPostII(@Path(value = PATH, encoded = true) String path,@Part() List<MultipartBody.Part> parts);
     * form表单和文件通用,后来测试到微信登录,后台大佬说form-data是流,
     * 我要的是application/x-www-form-urlencoded;charset=utf-8
     * 可是,其它接口没问题的,难道微信校验比较严格吗!或许吧
     * 反正改动也不大~
     * Content-Disposition: form-data; name="platform"
     * 上传图片
     */
    @Multipart
    @POST("{path}")
    Observable<String> uploadFilesII(@Path(value = PATH, encoded = true) String path, @Part() List<MultipartBody.Part> parts);

    /**
     * 替换url
     */
    @FormUrlEncoded
    @POST
    public Observable<String> runPost2(@Url String url,
                                       @Field(KEY) String json);

    /**
     * form表单提交
     */
    @POST("{path}")
    public Observable<String> runPostII(@Path(value = PATH, encoded = true) String path, @Body FormBody parts);

    @Streaming
    @GET
    Observable<String> downFileII(@Url() String downUrl);
}
