package info.xiancloud.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface InputBind {
    boolean required() default false;
    boolean disable() default false;//是否禁用，默认否
    String description() default "";
    String[] eitherOr() default {};//多选一
    String[] association() default {};//关联必填
}
