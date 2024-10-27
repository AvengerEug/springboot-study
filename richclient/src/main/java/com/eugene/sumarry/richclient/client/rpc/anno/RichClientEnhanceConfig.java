package com.eugene.sumarry.richclient.client.rpc.anno;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author muyang
 * @create 2024/10/25 16:58
 */
public class RichClientEnhanceConfig {

    /**
     * 定义一个切点,
     */
    @Pointcut("@annotation(com.eugene.sumarry.richclient.client.rpc.anno.RichClient)")
    public void aroundRichClientPointcut() {
    }


    @Around("aroundRichClientPointcut()")
    public Object richClientAroundPointcut(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 1、日志打点

        // 2、从本地缓存获取(判断过期时间)

        // 3、从远端缓存获取(需要开发成一个模板，远端获取数据之前，为当前业务缓存key添加一个读写锁，防止缓存穿透)，针对远端获取服务设置统一的返回结果，
        // todo 优化点： 如果是高并发场景，可以为每个业务场景(每个读写锁)添加指定的生命周期，当业务执行完毕后，在finally中移除锁，防止锁占用内存，造成内存泄露。

        // 4、从远端服务获取(需要开发成一个模板，远端获取数据之前，为当前业务缓存key添加一个读写锁，防止缓存穿透)

        return null;
    }
}
