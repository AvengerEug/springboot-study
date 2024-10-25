package com.eugene.sumarry.richclient.client.rpc.anno;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author muyang
 * @create 2024/10/25 16:57
 */
@Configuration
@Import({RichClientEhhanceConfig.class, LocalCacheConfig.class, RemoteCacheConfig.class})
public class RichClientConfig {




}
