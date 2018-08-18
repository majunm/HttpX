package interceptor;

import java.io.IOException;

import http.UCallback;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 时间：2017/12/5 下午10:01
 */

public class DownloadProgressRespBody extends ResponseBody {
    // 实际的待包装响应体
    private final ResponseBody responseBody;
    private UCallback callback;
    private long lastTime;
    // 包装完成的BufferedSource
    private BufferedSource bufferedSource;

    public DownloadProgressRespBody(ResponseBody requestBody, UCallback callback) {
        this.responseBody = requestBody;
        this.callback = callback;
        if (requestBody == null || callback == null) {
            throw new NullPointerException("this requestBody and callback must not null.");
        }
        System.out.println("##下载进度初始化##");
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            System.out.println("##下载进度contentLength#######" + responseBody.contentLength());
            return responseBody.contentLength();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("##下载进度初始化##" + e);
        }
        return -1;
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            // 包装
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private Source source(Source source) {

        return new ForwardingSource(source) {
            // 当前读取字节数
            //long totalBytesRead = 0L;
            //当前字节长度
            private long currentLength = 0L;
            //总字节长度，避免多次调用contentLength()方法
            private long totalLength = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // 增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                bytesRead = (bytesRead != -1 ? bytesRead : 0);
                //增加当前写入的字节数

                // currentLength += byteCount;
                currentLength += bytesRead;
                //获得contentLength的值，后续不再调用
                if (totalLength == 0) {
                    totalLength = contentLength();
                }
                System.out.println("##下载进度##" + totalLength);
                System.out.println("##下载进度bytesRead##" + bytesRead);
                System.out.println("##下载进度currentLength##" + currentLength);
                if (callback != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= 100 || lastTime == 0 || currentLength == totalLength) {
                        lastTime = currentTime;
                        Observable.just(currentLength).subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        System.out.println("down progress currentLength:" + currentLength + ",totalLength:" + totalLength);
                                        callback.onProgress(currentLength, totalLength, (100.0f * currentLength) / totalLength);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        callback.onFail(-1, throwable.getMessage());
                                    }
                                });
                    }
                }
                return bytesRead;
            }
        };
    }

    public void injection(UCallback callback) {
        this.callback = callback;
    }
}
