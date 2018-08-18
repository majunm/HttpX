package headers;


import java.util.Map;

/**
 * 请求头添加通用参数
 */
public interface HttpHeadersImpl {
    Map<String, String>  generateHeaders();
}
