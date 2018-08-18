package request;

import java.io.Serializable;

import http.ReqTags;
import http.PostfixImpl;

/**
 * Multipart body must have at least one part
 * 通用参数
 */
@ReqTags("")
public abstract class CommonRequest implements Serializable, PostfixImpl {
    public String token;//客户端版本整型值

    //    public String version;//客户端版本
//    public String channel;//来源渠道
//    public String imei;//客户端唯一标识
//    public String model = IUtil.MODEL;//设备型号
//    public String vendor = IUtil.BRAND;//设备厂商
    public String getToken() {
        //return LocalSaveServ.getToken(ResLibConfig.CONTEXT) + "";
        return "";
    }
//    public String platform = "1";//平台 1-安卓 2-IOS 3-H5

    public CommonRequest() {
//        versionCode = "" + IUtil.obtainAppVersionCode(ResLibConfig.CONTEXT);
//        version = IUtil.obtainAppVersion(ResLibConfig.CONTEXT);
    }

    @Override
    public boolean skipParams() {
        return false; // 不跳过携带参数
    }
}
