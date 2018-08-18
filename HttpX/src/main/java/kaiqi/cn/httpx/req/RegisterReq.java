package kaiqi.cn.httpx.req;

import http.ReqTags;
import request.CommonRequest;

/**

 {
     "data": {
     "collectIds": [],
     "email": "",
     "icon": "",
     "id": 9073,
     "password": "123456",
     "token": "",
     "type": 0,
     "username": "19910222"
     },
     "errorCode": 0,
     "errorMsg": ""
 }
 * 只返回成功,失败,data是对象 所以用HttpCommObjResp即可,不关注data
 */
@ReqTags("注册")
public class RegisterReq extends CommonRequest {
    public String username;
    public String password;
    public String repassword;

    public RegisterReq(String username, String password, String repassword) {
        this.username = username;
        this.password = password;
        this.repassword = repassword;
    }

    public RegisterReq() {
    }

    @Override
    public String postfix() {
        return "user/register";
    }
}
