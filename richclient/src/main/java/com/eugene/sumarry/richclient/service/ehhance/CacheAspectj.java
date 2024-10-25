package com.eugene.sumarry.richclient.service.ehhance;

import com.eugene.sumarry.richclient.client.domain.NullValueResultDO;
import com.eugene.sumarry.richclient.service.anno.CacheOperateType;
import com.eugene.sumarry.richclient.service.anno.Cached;
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

    /**
     * 定义一个切点, 只对dao包下的第一个参数为long的方法增强
     */
    @Pointcut("@annotation(com.eugene.sumarry.richclient.service.anno.Cached)")
    public void aroundPointcut() {
    }

    public Object get(String key) {
        RemoteResult result = remoteCacheService.get(key);
        if (result == null || !result.isSuccess()) {
            return null;
        } else if (result != null && result.getValue() != null) {
            // 不管是有数据，还是为空缓存，都返回。依赖数据过期
            return result.getValue();
        }
        return null;
    }

    @Around("aroundPointcut()")
    public Object cacheAroundPointcut(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("环绕通知---- 入口");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Cached cachedAnnotation = method.getAnnotation(Cached.class);
        String cacheKey = buildCacheKey(proceedingJoinPoint);
        Long expireTime = StringUtils.isEmpty(cachedAnnotation.expireTime()) ? null : Long.valueOf(cachedAnnotation.expireTime());

        CacheOperateType operator = cachedAnnotation.operator();
        Throwable throwable = null;
        if (Objects.equals(operator, CacheOperateType.GET)) {

            // 先从远端缓存获取，远端缓存获取不到，再执行目标方法
            Object result = null;
            try {
                result = get(cacheKey);
                if (result != null) {
                    System.out.println("从缓存中获取。 result: " + result);
                    if (result instanceof NullValueResultDO) {
                        return null;
                    } else {
                        return result;
                    }
                }

                result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
                if (result == null) {
                    // set空缓存对象
                    remoteCacheService.set(cacheKey, new NullValueResultDO(), expireTime);
                } else {
                    // set实际缓存
                    remoteCacheService.set(cacheKey, (Serializable) result, expireTime);
                }
            } catch (Throwable ex) {
                throwable = ex;
                throw throwable;
            }
            return result;
        } else if (Objects.equals(operator, CacheOperateType.DELETE)) {
            // 先执行目标方法，再删除缓存
            Object result = null;
            try {
                result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
                // 删除远端缓存
                remoteCacheService.remove(cacheKey);
            } catch (Throwable ex) {
                throwable = ex;
                throw throwable;
            }
            return result;
        }

        return null;
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
