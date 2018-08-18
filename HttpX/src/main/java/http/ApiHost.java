package http;

import com.vise.log.Logger;

import config.ResLibConfig;


/**
 */

public class ApiHost {
    private static String HOST = ResLibConfig.API_HOST;
    public static final String TAG = ApiHost.class.getSimpleName();

    public static String getHost() {
        Logger.e(TAG, HOST);
        return HOST;
    }

    public static void setHost(String url) {
        setHostHttps(url);
    }

    public static void setHostHttp(String url) {
        if (url.startsWith("https://") || url.startsWith("http://")) {
            HOST = url;
            HOST = HOST.replaceAll("https://", "http://");
        } else {
            HOST = "http://" + url;
        }
    }

    public static void setHostHttps(String url) {
        if (url.startsWith("https://") || url.startsWith("http://")) {
            HOST = url;
            HOST = HOST.replaceAll("http://", "https://");
        } else {
            HOST = "https://" + url;
        }
    }

    public static String getHttp() {
        if (HOST.startsWith("https://") || HOST.startsWith("http://")) {
            HOST = HOST.replaceAll("https://", "http://");
        } else {
            HOST = "http://" + HOST;
        }
        return HOST;
    }

    public static String getHttps() {
        if (HOST.startsWith("https://") || HOST.startsWith("http://")) {
            HOST = HOST.replaceAll("http://", "https://");
        } else {
            HOST = "https://" + HOST;
        }
        return HOST;
    }
}
