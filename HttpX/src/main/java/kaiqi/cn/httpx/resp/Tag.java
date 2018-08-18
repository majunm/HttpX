package kaiqi.cn.httpx.resp;

import resp.Data;

/**
 */
public class Tag implements Data {
    public String name;
    public String url;

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                "} " + super.toString();
    }
}
