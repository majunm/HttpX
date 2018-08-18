package http;

/**
 */

public interface PostfixImpl {
    /**
     * 后缀
     */
    String postfix();

    /**
     * 跳过携带参数吗,默认false
     * @return
     */
    boolean skipParams();
}
