package info.xiancloud.plugin.apidoc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标志方法属于 DocOAuth20 接口
 * 
 * @author yyq
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface DocOAuth20Sub {

	// 接口名称
	String name() default "暂无";

	// 接口描述
	String dec() default "暂无";

	// 路径
	String url();

	// 请求方式 默认 GET
	String method() default "GET";

	// 接口入参明细
	DocOAuth20SubIn[] args() default {};
}
