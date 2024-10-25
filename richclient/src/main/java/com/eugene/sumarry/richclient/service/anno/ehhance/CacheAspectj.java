package com.eugene.sumarry.richclient.service.anno.ehhance;

import com.eugene.sumarry.richclient.client.domain.NullValueResultDO;
import com.eugene.sumarry.richclient.service.anno.CacheOperateType;
import com.eugene.sumarry.richclient.service.anno.Cached;
import com.eugene.sumarry.richclient.service.anno.factory.CacheOperateProcessorFactory;
import com.eugene.sumarry.richclient.service.remotecache.RemoteCacheService;
import com.eugene.sumarry.richclient.service.remotecache.RemoteResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author muyang
 * @create 2024/10/25 11:12
 */
@Component
@Aspect
public class CacheAspectj {

    @Autowired
    private RemoteCacheService remoteCacheService;

    @Autowired
    private CacheOperateProcessorFactory cacheOperateProcessorFactory;

    /**
     * 定义一个切点, 只对dao包下的第一个参数为long的方法增强
     */
    @Pointcut("@annotation(com.eugene.sumarry.richclient.service.anno.Cached)")
    public void aroundPointcut() {
    }


    @Around("aroundPointcut()")
    public Object cacheAroundPointcut(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Cached cachedAnnotation = method.getAnnotation(Cached.class);

        return cacheOperateProcessorFactory.get(cachedAnnotation).process(proceedingJoinPoint);
    }

}
