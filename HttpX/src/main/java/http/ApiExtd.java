package http;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * api 扩展,上传,下载文件已经动态url
 */

public interface ApiExtd extends Api {
    @Multipart
    @POST("{" + PATH + "}")
    Observable<String> uploadFilesII(@Path(value = PATH, encoded = true) String path, @Part() List<MultipartBody.Part> parts);

    @Streaming
    @GET
    Observable<String> downFileII(@Url() String downUrl);

    /**
     * 替换url,json提交
     */
    @POST("{" + PATH + "}")
    Observable<String> runPostX(@Url String url, @Path(value = PATH, encoded = true) String path,
                                @Body String json);

    /**
     * @param url   动态url
     * @param path  后缀
     * @param parts 表单数据
     * @return
     */
    @POST("{" + PATH + "}")
    Observable<String> runPostxII(@Url String url, @Path(value = PATH, encoded = true) String path, @Body FormBody parts);
}
