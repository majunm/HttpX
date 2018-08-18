package resp;

import com.google.gson.annotations.SerializedName;

import config.HttpConfig;

/**
 * data对应对象
 */
public class HttpCommObjResp<T> extends CommonResp {
    private static final long serialVersionUID = 1L;
    @SerializedName(HttpConfig.DATA)
    public T data;

    public T getData() {
        return data == null ? null : data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data + "|" + super.toString();
    }
}
