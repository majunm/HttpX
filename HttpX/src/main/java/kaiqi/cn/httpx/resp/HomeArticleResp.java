package kaiqi.cn.httpx.resp;

import java.util.List;

import resp.Data;

/**
 * data:{}  是对象
 */
public class HomeArticleResp implements Data {
    public int curPage;
    public int offset;
    public boolean over;
    public int pageCount;
    public int size;
    public int total;
    public List<HomeArticle> datas;

    @Override
    public String toString() {
        return "HomeArticleResp{" +
                "curPage=" + curPage +
                ", offset=" + offset +
                ", over=" + over +
                ", pageCount=" + pageCount +
                ", size=" + size +
                ", total=" + total +
                ", datas=" + datas +
                "} " + super.toString();
    }
}
