package interceptor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这里的实现是为了进行token过期拦截
 */

public interface HttpInterceptor {
    int TOKEN_EXPIRE = 14004; // 用户未登录,token过期code
    int KICKED_OFF_LINE = 14005; // 用户已在其他设备登录
    int ILLEGAL_TOKEN = 14004; // 用户未登录,token过期
    int PROHIBIT_USED = 16002; // 该用户已经被禁用
    int PROHIBIT_USEDII = 140021; // 用户被封禁,不能登录
    int UN_LOGIN = 14004; // 未登录
    String KEY_TOKEN = "token"; // token过期code
    public static AtomicInteger AUTO_INCREMENT = new AtomicInteger(0);

    void doInterceptor(int... cmds);
}
