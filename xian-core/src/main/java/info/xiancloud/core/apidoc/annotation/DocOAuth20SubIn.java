package info.xiancloud.core.apidoc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DocOAuth20 接口入参明细
 * 
 * @author yyq
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface DocOAuth20SubIn {

	// 参数名称
	String name();

	// 参数描述
	String dec() default "暂无";

	// 参数类型
	Class<?> type();

	// 是否是必须的
	boolean require() default true;
}
