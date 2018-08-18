package http;

import com.vise.log.Logger;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 */

public class RetryFunc implements Function<Observable<? extends Throwable>, Observable<?>> {

    private final String TAG = RetryFunc.class.getSimpleName();
    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;

    public RetryFunc(int maxRetries, int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    public Observable<?> apply(@NonNull Observable<? extends Throwable> observable) throws Exception {
        return observable
                .flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        if (++retryCount <= maxRetries && (throwable instanceof SocketTimeoutException
                                || throwable instanceof ConnectException)) {
                            Logger.e(TAG, "get response data error, it will try after " + retryDelayMillis
                                    + " millisecond, retry count " + retryCount);
                            System.out.println("get response data error, it will try after " + retryDelayMillis
                                    + " millisecond, retry count " + retryCount);
                            return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
                        }
                        Logger.e(TAG, "=重试次数=" + retryCount);
                        return Observable.error(ApiException.handleException(throwable));
                    }
                });
    }
}


