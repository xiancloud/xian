package info.xiancloud.plugin.rules.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 规则引擎入参
 *
 * @author happyyangyuan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Params {
    String value() default "";
}