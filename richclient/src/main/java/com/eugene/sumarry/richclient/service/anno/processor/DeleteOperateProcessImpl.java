package com.eugene.sumarry.richclient.service.anno.processor;

import com.eugene.sumarry.richclient.service.anno.CacheOperateType;
import com.eugene.sumarry.richclient.service.anno.Cached;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author muyang
 * @create 2024/10/25 15:49
 */
@Component
public class DeleteOperateProcessImpl extends AbstractCacheOperateProcess {


    public DeleteOperateProcessImpl() {
        super(CacheOperateType.DELETE);
    }

    @Override
    public Object doProcess(ProceedingJoinPoint proceedingJoinPoint, Method method, Cached cachedAnnotation) throws Throwable {
        // 先执行目标方法，再删除缓存
        Object result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        // 删除远端缓存
        remoteCacheService.remove(this.buildCacheKey(proceedingJoinPoint, cachedAnnotation));
        return result;
    }
}
