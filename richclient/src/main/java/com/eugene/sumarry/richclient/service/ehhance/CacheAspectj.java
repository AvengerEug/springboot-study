package com.eugene.sumarry.richclient.service.ehhance;

import com.eugene.sumarry.richclient.client.domain.NullValueResultDO;
import com.eugene.sumarry.richclient.service.anno.CacheOperateType;
import com.eugene.sumarry.richclient.service.anno.Cached;
import com.eugene.sumarry.richclient.service.remotecache.RemoteCacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /**
     * 定义一个切点, 只对dao包下的第一个参数为long的方法增强
     */
    @Pointcut("@annotation(com.eugene.sumarry.richclient.service.anno.Cached)")
    public void aroundPointcut() {
    }

    @Around("aroundPointcut()")
    public void cacheAroundPointcut(ProceedingJoinPoint proceedingJoinPoint) {
        System.out.println("环绕通知---- 入口");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Cached cachedAnnotation = method.getAnnotation(Cached.class);
        String cacheKey = buildCacheKey(proceedingJoinPoint);

        CacheOperateType operator = cachedAnnotation.operator();
        if (Objects.equals(operator, CacheOperateType.GET)) {
            // 先从远端缓存获取，远端缓存获取不到，再执行目标方法
            Object result = null;
            try {
                result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                if (result == null) {
                    // set空缓存对象
                    remoteCacheService.set(cacheKey, new NullValueResultDO());
                } else {
                    // set实际缓存
                    remoteCacheService.set(cacheKey, (Serializable) result);
                }
            }
        } else if (Objects.equals(operator, CacheOperateType.DELETE)) {
            // 先执行目标方法，再删除缓存
            try {
                proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                // 删除远端缓存
                remoteCacheService.remove(cacheKey);
            }
        }

        System.out.println("环绕结束");
    }

    public String buildCacheKey(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        StringBuilder cacheKeySB = new StringBuilder();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                cacheKeySB.append(args[i].toString()).append("_");
            }
        }
        return cacheKeySB.toString();
    }

}
