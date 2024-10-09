package com.erebelo.springmongodbdemo.context.history;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DocumentHistory {

    @AliasFor("collection")
    String value() default "";

    @AliasFor("value")
    String collection() default "";

}
