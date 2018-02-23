package info.xiancloud.plugin.rules.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 规则引擎出参
 *
 * @author happyyangyuan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ReturnParams {
    String value() default "";
}