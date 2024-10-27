package com.eugene.sumarry.richclient.client.rpc.anno;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author muyang
 * @create 2024/10/25 16:57
 */
@Configuration
@Import({RichClientEnhanceConfig.class, LocalCacheConfig.class, RemoteCacheConfig.class})
public class RichClientConfig {




}
