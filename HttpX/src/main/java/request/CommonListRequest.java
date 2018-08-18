package request;

/**
 * 列表通用请求体,抽取page通用参数
 */

public abstract class CommonListRequest extends CommonRequest {
    public int page = 1; // page
}
