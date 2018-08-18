package http;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.FormBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * 指定@Body注解时,会调用IRequestBodyConverter2
 * 指定类型为
 * Content-Type: application/json;charset=UTF-8
 * ================================
 * 如果指定@FormUrlEncoded
 * Content-Type: application/x-www-form-urlencoded
 * 具体请参见:
 * ServiceMethod.parseParameterAnnotation
 */

public interface Api {
    String KEY = "data";
    String PATH = "path";

    @POST("{" + PATH + "}")
    Observable<String> runPost(@Path(value = PATH, encoded = true) String path,
                               @Body String json);

    @GET("{" + PATH + "}")
    Observable<String> runGet(@Path(value = PATH, encoded = true) String path,
                              @Query(KEY) String json);

    /**
     * form表单提交
     */
    @POST("{" + PATH + "}")
    Observable<String> runPostII(@Path(value = PATH, encoded = true) String path, @Body FormBody parts);

    /**
     * form表单提交
     */
    @GET("{" + PATH + "}")
    Observable<String> runGetII(@Path(value = PATH, encoded = true) String path, @QueryMap Map<String, Object> json);
}
