package com.eugene.sumarry.richclient.service.anno;

import java.lang.annotation.*;

/**
 * 缓存注解
 * @author muyang
 * @create 2024/10/25 11:03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {

    /**
     * 操作缓存的类型，默认为get
     * @return
     */
    CacheOperateType operator() default CacheOperateType.GET;

    /**
     * 缓存key，后期支持表达式
     * @return
     */
    String cacheKey();

    /**
     * 过期时间，单位为s
     * @return
     */
    String expireTime() default "";


}
