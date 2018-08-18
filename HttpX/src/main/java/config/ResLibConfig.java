package config;

import android.content.Context;

public class ResLibConfig {
    public static boolean DEBUG = true;
    public static Context CONTEXT;
    public static boolean USE_ENCRYPT = false; //使用加密解密吗
    public static boolean RUN_TEST_CASE = false;//单元测试
    public static String API_BASE_HOST = DEBUG ? "http://www.wanandroid.com/" : "http://www.wanandroid.com/";//默认API主机地址
    //    public static final String API_HOST_VERSION = "besttmwuu-0.0.1/";//版本接口的版本号|| app版本1.3 接
    public static String API_HOST_VERSION = "";//版本接API_HOST口的版本号|| app版本1.3 接
    public static String API_HOST = API_BASE_HOST + API_HOST_VERSION;//默认API主机地址
    public static int MAX_AGE_ONLINE = 60;//默认最大在线缓存时间（秒）
    public static int MAX_AGE_OFFLINE = 24 * 60 * 60;//默认最大离线缓存时间（秒）
    public static final int DEFAULT_TIMEOUT = 60;//默认超时时间（秒）
    public static final int DEFAULT_MAX_IDLE_CONNECTIONS = 5;//默认空闲连接数
    public static final long DEFAULT_KEEP_ALIVE_DURATION = 8;//默认心跳间隔时长（秒）
    public static final long CACHE_MAX_SIZE = 10 * 1024 * 1024;//默认最大缓存大小（字节）
    public static final String CACHE_NAME = "HttpCache";//默认最大缓存大小（字节）

    public static int DEFAULT_RETRY_COUNT = 3;//默认重试次数
    public static int DEFAULT_RETRY_DELAY_MILLIS = 3000;//默认重试间隔时间（毫秒）
}
