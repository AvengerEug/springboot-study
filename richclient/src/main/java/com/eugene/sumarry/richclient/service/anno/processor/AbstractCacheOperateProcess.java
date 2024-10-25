package com.eugene.sumarry.richclient.service.anno.processor;

import com.eugene.sumarry.richclient.service.anno.CacheOperateType;
import com.eugene.sumarry.richclient.service.anno.Cached;
import com.eugene.sumarry.richclient.service.anno.PlaceholderResolver;
import com.eugene.sumarry.richclient.service.remotecache.RemoteCacheService;
import com.eugene.sumarry.richclient.service.remotecache.RemoteResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author muyang
 * @create 2024/10/25 15:57
 */
public abstract class AbstractCacheOperateProcess implements CacheOperateProcessor {

    private static final PlaceholderResolver placeholderResolver = PlaceholderResolver.getDefaultResolver();

    protected CacheOperateType cacheOperateType;

    @Autowired
    protected RemoteCacheService remoteCacheService;

    public AbstractCacheOperateProcess(CacheOperateType cacheOperateType) {
        this.cacheOperateType = cacheOperateType;
    }

    public boolean accept(Cached cached) {
        return Objects.equals(cached.operator(), cacheOperateType);
    }

    protected Object get(String key) {
        RemoteResult result = remoteCacheService.get(key);
        if (result == null || !result.isSuccess()) {
            return null;
        } else if (result != null && result.getValue() != null) {
            // 不管是有数据，还是为空缓存，都返回。依赖数据过期
            return result.getValue();
        }
        return null;
    }

    protected void set(String key, Serializable value, Long expireTime) {
        remoteCacheService.set(key, value, expireTime);
    }

    protected String buildCacheKey(ProceedingJoinPoint proceedingJoinPoint, Cached cachedAnnotation) {
        return placeholderResolver.resolve(cachedAnnotation.cacheKey(), proceedingJoinPoint.getArgs());
    }

    protected Long getExpireTime(Cached cached) {
        return StringUtils.isEmpty(cached.expireTime()) ? null : Long.valueOf(cached.expireTime());
    }


    @Override
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 日志打点
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Cached cachedAnnotation = method.getAnnotation(Cached.class);

        Object result = this.doProcess(proceedingJoinPoint, method, cachedAnnotation);

        return result;
    }

    protected abstract Object doProcess(ProceedingJoinPoint proceedingJoinPoint, Method method, Cached cachedAnnotation) throws Throwable;



}
