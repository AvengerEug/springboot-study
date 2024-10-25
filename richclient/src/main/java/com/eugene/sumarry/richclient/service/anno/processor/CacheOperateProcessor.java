package com.eugene.sumarry.richclient.service.anno.processor;

import com.eugene.sumarry.richclient.service.anno.Cached;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author muyang
 * @create 2024/10/25 15:49
 */
public interface CacheOperateProcessor {

    boolean accept(Cached cached);


    /**
     * 切面的处理逻辑
     * @param proceedingJoinPoint
     * @return 出参。从缓存中获取，或者从下游服务获取
     */
    Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;


}
