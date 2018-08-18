package http;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ReqTags("")
 *
 * @ReqTags(value="注册",extdParams={"user/register"},extdCmdParams={"1"})
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReqTags {
    String value() default "";

    /**
     * 扩展string参数,如有需要
     *
     * @return
     */
    String[] extdParams() default {};

    /**
     * 扩展命令,如有需要
     *
     * @return
     */
    int[] extdCmdParams() default {};
}
