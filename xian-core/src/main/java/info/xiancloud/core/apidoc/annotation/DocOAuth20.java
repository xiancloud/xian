package info.xiancloud.core.apidoc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标志类拥有 DocOAuth20 接口文档 子项
 * 
 * @author yyq
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface DocOAuth20 {

	String name() default "auth20接口文档";

	String description() default "auth20接口文档";

}
