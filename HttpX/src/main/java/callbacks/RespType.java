package callbacks;


/**
 * 获取返回类型
 */
public interface RespType {
    public static final int TYPE_STRING = 0;
    public static final int TYPE_OBJS = 1;
    public static final int TYPE_OBJ = 2;

    public int optRespType();
}
