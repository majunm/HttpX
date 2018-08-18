package resp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {
 * "code": 200,
 * "data": {
 * "list": [
 * {
 * "mothod_name": "领取"
 * }
 * <p>
 * ],
 * "total_page": 4,
 * "total_count": "71",
 * "page": 1
 * }
 * }
 */

public class CommonListTypeResp<T> implements Serializable {
    public List<T> getList() {
        return list == null ? new ArrayList<T>() : list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<T> list;
    public int total_page;
    public int total_count;
    public int page = 1;
}
