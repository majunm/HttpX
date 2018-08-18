package resp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import config.HttpConfig;

public class CommResp implements Serializable {
    public static final long serialVersionUID = 1L;
    public static int SUCCESS = 200;
    public static String KEY_MSG = HttpConfig.MSG;
    public static String KEY_TOKEN = HttpConfig.TOKEN;
    //验证码code
    public static String KEY_VERIFY_CODE = HttpConfig.VERIFY_CODE;
    public static String KEY_CODE = HttpConfig.CODE;
    public static String KEY_DATA = HttpConfig.DATA;
    @SerializedName(value = HttpConfig.CODE, alternate = {HttpConfig.CODE_EXTD_I, HttpConfig.CODE_EXTD_II})
    public int code;
    @SerializedName(value = HttpConfig.MSG, alternate = {HttpConfig.MSG_EXTD_I, HttpConfig.MSG_EXTD_II})
    public String msg;
//    public String time;

    public boolean isSuccess() {
        return code == SUCCESS; // 200 成功 -1 失败 -2 退出登录
    }

    @Override
    public String toString() {
        return "CommResp{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}

