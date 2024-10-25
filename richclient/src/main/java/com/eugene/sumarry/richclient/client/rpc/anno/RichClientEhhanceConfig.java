package com.eugene.sumarry.richclient.client.rpc.anno;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author muyang
 * @create 2024/10/25 16:58
 */
public class RichClientEhhanceConfig {

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

        // 3、从远端缓存获取

        // 4、从远端服务获取

        return null;
    }
}
