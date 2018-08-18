package kaiqi.cn.httpx.req;

import http.ReqTags;
import request.CommonRequest;

/**
 */
@ReqTags("上传")
public class UploadReq extends CommonRequest {
    public UploadReq(String fileData) {
        this.fileData = fileData;
    }
    public UploadReq() {
    }
    public String fileData; // 文件键值对
    //============多文件就这样吧============
    public String fileData1; // 文件键值对
    public String fileData2; // 文件键值对
    public String fileData3; // 文件键值对
    //============多文件就这样吧============
    public String avatar;
    @Override
    public String postfix() {
        return "user/setting/avatar"; // 修改头像
    }
}
