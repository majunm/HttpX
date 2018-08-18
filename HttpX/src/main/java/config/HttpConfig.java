package config;

/***
 =====当服务器返回统一格式,类似如下=======
 {  "msg": "失败~",  "code": 2,  "data": {}  }
 MSG = msg
 CODE = code
 data = data
 =====当服务器返回统一格式,类似如下=======
 { 	"message": "失败~", 	"code": 2, 	"infos": {} }
 MSG = message
 CODE = code
 data = infos
 就是你们服务器返回的格式会重命名为msg,code,data
 "errorCode": 0,
 "errorMsg": ""
 ========现在提供三种样式=========
 */
public interface HttpConfig {
    String MSG = "msg";
    String MSG_EXTD_I = "message";
    String MSG_EXTD_II = "errorMsg";

    String CODE = "code";
    String CODE_EXTD_I = "errorCode";
    String CODE_EXTD_II = "error";


    String DATA = "data";
    String DATA_EXTD_I = "infos"; // infos
    String DATA_EXTD_II = "results";
    String DATA_EXTD_III = "lists";

    String VERIFY_CODE = "code";
    String TOKEN = "token";
}
