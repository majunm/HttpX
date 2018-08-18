package kaiqi.cn.httpx.resp;

import resp.Data;

/**
 * {
 * "collectIds": [],
 * "email": "",
 * "icon": "",
 * "id": 9073,
 * "password": "123456",
 * "token": "",
 * "type": 0,
 * "username": "19910222"
 * }
 */
public class RegisterResp implements Data {
    public String email;
    public String icon;
    public int id;
    public String password;
    public int type;
    public String username;
    //collectIds // what 暂时不写

    @Override
    public String toString() {
        return "RegisterResp{" +
                "email='" + email + '\'' +
                ", icon='" + icon + '\'' +
                ", id=" + id +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", username='" + username + '\'' +
                "} " + super.toString();
    }
}
