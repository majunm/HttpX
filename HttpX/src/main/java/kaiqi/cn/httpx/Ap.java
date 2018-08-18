package kaiqi.cn.httpx;

import android.app.Application;

import com.vise.log.Logger;

import java.util.HashMap;
import java.util.Map;

import config.ResLibConfig;
import headers.HttpHeadersImpl;
import http.EncryptFuncs;
import http.HttpRequestFactory;
import http.ReqTags;
import interceptor.HttpInterceptor;
//import security.util.AesEncryptionUtil;

public class Ap extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("玩安卓:" + ResLibConfig.API_HOST);
        HttpRequestFactory.doCreateHttpReqManager(this, ResLibConfig.DEBUG ? "http://www.wanandroid.com/" : "http://www.wanandroid.com/")
                .registerCallbacks(new HttpHeadersImpl() {
                    @Override
                    public Map<String, String> generateHeaders() {
                        Map<String, String> keys = new HashMap<>();
                        //        keys.put("versionCode", "" + IUtil.obtainAppVersionCode(ResLibConfig.CONTEXT));
                        //        keys.put("version", IUtil.obtainAppVersion(ResLibConfig.CONTEXT));
                        //        keys.put("channel", "");
                        //        keys.put("imei", "");
                        //        keys.put("platform", "2");//1:ios 2:Android
                        //        keys.put("model", IUtil.MODEL);
                        //        keys.put("vendor", IUtil.BRAND);
                        return keys; // 请求头注入,如有需要
                    }
                }).registerCallbacks(new HttpInterceptor() {
            @Override
            public void doInterceptor(int... cmds) {
                if (cmds != null && cmds.length > 0) {
                    int code = cmds[0];
                    switch (code) {
                        case HttpInterceptor.KICKED_OFF_LINE:
                            //Tools.showToast("用户已在其他设备登录");
                            int value = AUTO_INCREMENT.incrementAndGet();
                            if (value == 1) {
                                // relogin(); // 调用一次
                            }
                            Logger.e("http", "用户已在其他设备登录|" + value + "|");
                            break;
                        case HttpInterceptor.PROHIBIT_USED:
                            //Tools.showToast("您被禁止使用");
                            break;
                        case HttpInterceptor.PROHIBIT_USEDII:
                            //Tools.showToast("用户被封禁,不能登录");
                            break;
                        case HttpInterceptor.UN_LOGIN:
                            //Tools.showToast("请先登录");
                            //relogin(false);
                            break;
                    }
                }
            }
        }).asEncryptFuncs(new EncryptFuncs() {
            @Override
            public String encrypt(String plainText) {
                //return AesEncryptionUtil.encrypt(plainText);
                return "";
            }

            @Override
            public String decrypt(String cipherText) {
                //return AesEncryptionUtil.decrypt(cipherText);
                return "";
            }

            /**
             * {@link ReqTags}
             * {@link kaiqi.cn.httpx.req.RegisterReq}
             * 请给予tag,否则取消不掉请求,拦截不了加密解密赛~
             */
            @Override
            public boolean accept(String httpReaTag) {
                if ("注册".equals(httpReaTag)) {
                    // return false; // 你们都同意,我注册第一个不答应
                }
                return true; // 同意加密
            }
        }).asSubmmitForm(true); // true = 表单提交 false = json提交
    }

}
