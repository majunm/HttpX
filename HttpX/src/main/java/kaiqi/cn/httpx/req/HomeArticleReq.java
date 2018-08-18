package kaiqi.cn.httpx.req;

import http.ReqTags;
import request.CommonRequest;

/**
 * http://www.wanandroid.com/article/list/0/json
 * http://www.wanandroid.com/article/list/0/json?cid=60
 * 方法：GET 参数：页码，拼接在连接中，从0开始
 */
@ReqTags("首页文章列表")
public class HomeArticleReq extends CommonRequest {
    public int page = 0;
    public int cid = 0;
    public boolean skipParams;
    //page
    @Override
    public String postfix() {
        return "article/list/" + page + "/json";
    }

    @Override
    public boolean skipParams() {
        return skipParams;
    }
}
