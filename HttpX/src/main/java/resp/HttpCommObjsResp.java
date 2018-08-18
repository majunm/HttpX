package resp;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import config.HttpConfig;

/**
 * data对象数组
 */
public class HttpCommObjsResp<T> extends CommonResp {
    private static final long serialVersionUID = 1L;
    @SerializedName(value = HttpConfig.DATA, alternate = {HttpConfig.DATA_EXTD_I, HttpConfig.DATA_EXTD_II, HttpConfig.DATA_EXTD_III})
    public List<T> data;

    public List<T> getDatas() {
        if (data == null) {
            data = new ArrayList<T>();
        }
        return data;
    }

    @Override
    public String toString() {
        return data + "|" + super.toString();
    }
}

