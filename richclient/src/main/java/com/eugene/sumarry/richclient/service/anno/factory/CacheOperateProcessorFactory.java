package com.eugene.sumarry.richclient.service.anno.factory;

import com.eugene.sumarry.richclient.service.anno.Cached;
import com.eugene.sumarry.richclient.service.anno.processor.CacheOperateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 缓存操作工厂类
 * @author muyang
 * @create 2024/10/25 15:49
 */
@Component
public class CacheOperateProcessorFactory {

    @Autowired
    private List<CacheOperateProcessor> processorList;


    public CacheOperateProcessor get(Cached cached) {
        if (cached == null) {
            throw new RuntimeException("Cached annotation is null");
        }

        for (CacheOperateProcessor cacheOperateProcessor : processorList) {
            if (cacheOperateProcessor.accept(cached)) {
                return cacheOperateProcessor;
            }
        }

        throw new RuntimeException("Not support cached operation type");
    }

}
