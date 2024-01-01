package org.hxj.annotation;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MiniMapperScanRegister.class)
public @interface MiniMapperScan {
    String value() default "";
}
