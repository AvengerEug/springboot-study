package com.eugene.sumarry.richclient.client.rpc.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 富客户端标识，打在方法上，表示当前方法启动3级缓存（1，2级缓存）
 * @author muyang
 * @create 2024/10/25 16:35
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RichClient {
    /**
     * 缓存key，后期支持表达式
     * @return
     */
    String cacheKey();
}
