package org.hxj.annotation;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HxjMapperScanRegister.class)
public @interface HxjMapperScan {
    String value() default "";
}
