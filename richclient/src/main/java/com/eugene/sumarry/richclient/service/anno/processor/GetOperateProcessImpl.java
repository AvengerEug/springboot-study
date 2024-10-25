package com.eugene.sumarry.richclient.service.anno.processor;

import com.eugene.sumarry.richclient.client.domain.NullValueResultDO;
import com.eugene.sumarry.richclient.service.anno.CacheOperateType;
import com.eugene.sumarry.richclient.service.anno.Cached;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author muyang
 * @create 2024/10/25 15:49
 */
@Component
public class GetOperateProcessImpl extends AbstractCacheOperateProcess {

    public GetOperateProcessImpl() {
        super(CacheOperateType.GET);
    }

    @Override
    public Object doProcess(ProceedingJoinPoint proceedingJoinPoint, Method method, Cached cachedAnnotation) throws Throwable {
        String cacheKey = this.buildCacheKey(proceedingJoinPoint, cachedAnnotation);
        Object result = get(cacheKey);
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
            this.set(cacheKey, new NullValueResultDO(), this.getExpireTime(cachedAnnotation));
        } else {
            // set实际缓存
            this.set(cacheKey, (Serializable) result, this.getExpireTime(cachedAnnotation));
        }
        return result;
    }
}
