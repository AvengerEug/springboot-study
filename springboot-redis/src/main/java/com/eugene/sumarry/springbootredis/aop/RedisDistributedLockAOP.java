package com.eugene.sumarry.springbootredis.aop;

import com.eugene.sumarry.springbootredis.anno.RedisDistributedLock;
import com.eugene.sumarry.springbootredis.utils.SpringContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * redis分布式锁aop
 */
@Component
@Aspect
public class RedisDistributedLockAOP {

    /**
     * 定义了一个切点, 表示带了@RedisDistributedLock注解的才会被增强
     */
    @Pointcut("@annotation(com.eugene.sumarry.springbootredis.anno.RedisDistributedLock)")
    public void pointcutAnnotation() {
    }

    @Around("pointcutAnnotation()")
    public Object aroundPointcutAnnotation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RedisDistributedLock redisDistributedLock = method.getAnnotation(RedisDistributedLock.class);

        RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
        RLock lock = redissonClient.getLock(redisDistributedLock.value());
        boolean locked = false;
        try {
            if (locked = lock.tryLock()) {
                return proceedingJoinPoint.proceed();
            }
        } finally {
            if (locked){
                lock.unlock();
            }
        }

        return false;
    }

}
