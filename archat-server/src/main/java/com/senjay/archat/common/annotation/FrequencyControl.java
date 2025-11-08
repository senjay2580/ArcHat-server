package com.senjay.archat.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 频控注解
 * @author 33813
 */
@Repeatable(FrequencyControlContainer.class)//可重复
@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Target(ElementType.METHOD)//作用在方法上
public @interface FrequencyControl {

    String prefixKey() default "";


    Target target() default Target.EL;


    String spEl() default "";


    int time();


    TimeUnit unit() default TimeUnit.SECONDS;



    int count();

    enum Target {
        UID, IP, EL
    }
}
