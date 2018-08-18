package http;

import android.net.ParseException;
import android.text.TextUtils;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

/**
 */

public class ApiException extends Exception {
    public static final String TAG = ApiException.class.getSimpleName();
    /**
     * 接口返回code
     */
    public int code;
    /**
     * 堆栈消息
     */
    public String message;
    /**
     * 接口返回的消息
     */
    public String msg;
    /**
     * 接口返回的源消息,没有呗处理过
     */
    public String orginErrorMsg;

    public ApiException(Throwable throwable, int code, String msg) {
        super(throwable);
        this.code = code;
        if (throwable != null) {
            this.message = throwable.getMessage();
        }
        this.orginErrorMsg = msg;
        this.msg = "错误信息:" + msg; // 取消错误信息提示 2019 9 22日
        this.msg = msg;
        this.message = msg; // 取消错误信息提示
    }

    public ApiException(Throwable throwable) {
        super(throwable);
        if (throwable != null) {
            this.message = throwable.getMessage();
            this.msg = "错误信息:" + this.message;
            this.msg = this.message;
        }
    }

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ApiException setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getDisplayMessage() {
        return message + "(code:" + code + ")";
    }

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ApiException(e, ApiCode.Request.HTTP_ERROR);
            // Logger.e(TAG, ex.code);
            switch (httpException.code()) {
                case ApiCode.Http.INTERNAL_SERVER_ERROR:
                    ex.message = "服务器内部错误,错误码:" + ApiCode.Http.INTERNAL_SERVER_ERROR;
                    break;
                case ApiCode.Http.BAD_GATEWAY:
                    ex.message = "网关错误,错误码:" + ApiCode.Http.BAD_GATEWAY;
                    break;
                case ApiCode.Http.UNAUTHORIZED:
                case ApiCode.Http.FORBIDDEN:
                case ApiCode.Http.NOT_FOUND:
                case ApiCode.Http.REQUEST_TIMEOUT:
                case ApiCode.Http.GATEWAY_TIMEOUT:
                case ApiCode.Http.SERVICE_UNAVAILABLE:
                default:
                    ex.message = "网络错误..." + e;
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            ex = new ApiException(e, ApiCode.Request.PARSE_ERROR);
            ex.message = "解析错误,错误代码:" + ApiCode.Request.NETWORK_ERROR;
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, ApiCode.Request.NETWORK_ERROR);
            ex.message = "网络错误,错误代码:" + ApiCode.Request.NETWORK_ERROR;
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, ApiCode.Request.SSL_ERROR);
            ex.message = "SSL错误,错误代码:" + ApiCode.Request.SSL_ERROR;
            return ex;
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(e, ApiCode.Request.TIMEOUT_ERROR);
            ex.message = "请求超时错误,错误代码:" + ApiCode.Request.TIMEOUT_ERROR;
            return ex;
        } else {
            ex = new ApiException(e, ApiCode.Request.UNKNOWN);
            ex.message = "未知错误,错误代码:" + ApiCode.Request.UNKNOWN + e.getMessage();
            return ex;
        }
    }

    @Override
    public String toString() {
        //return message + "|" + msg + "|" + super.toString();
        if (!TextUtils.isEmpty(msg) || !"null".equals(msg)) {
            message = msg;
        }
        return message;
    }
}

