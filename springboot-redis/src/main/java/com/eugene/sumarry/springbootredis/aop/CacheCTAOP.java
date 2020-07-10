package com.eugene.sumarry.springbootredis.aop;

import com.eugene.sumarry.springbootredis.dao.GoodsDao;
import com.eugene.sumarry.springbootredis.model.Pagination;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 缓存穿透aop
 */
@Component
@Aspect
public class CacheCTAOP {

    @Autowired
    private GoodsDao goodsDao;

    private final BloomFilter<Long> bloomFilter = BloomFilter.create(Funnels.longFunnel(), 100, 0.01);

    @PostConstruct
    private void populateGoodsBloomFilter() {

        Long count = goodsDao.count();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                5,
                5,
                20,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy()
                );

        for (int i = 0; i < 5; i++) {
            final int pageIndex = i;
            threadPool.submit(() -> {
                Pagination pagination = new Pagination();
                pagination.setPageIndex(Long.valueOf(pageIndex + 1));
                pagination.setPageSize(10000L);
                pagination.setTotalCount(count);

                List<Long> goodsIds = goodsDao.fetchIdByPagination(pagination.getOffset(), pagination.getPageSize());

                for (Long goodsId : goodsIds) {
                    bloomFilter.put(goodsId);
                }
            });

        }

    }

    /**
     * 定义了一个切点, 表示带了@AspectAnnotation注解的才会被增强
     */
    @Pointcut("@annotation(com.eugene.sumarry.springbootredis.anno.CacheCT)")
    public void pointcutAnnotation() {
    }

    @Around("pointcutAnnotation()")
    public Object beforePointcutAnnotation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 获取切点的所有参数，第一个参数为商品ID
        Object[] args = proceedingJoinPoint.getArgs();
        if (args == null || args.length < 0) {
            throw new Exception("参数为空，抛异常");
        }

        if (args != null && args.length > 0) {

            if (bloomFilter.mightContain((Long)args[0])) {
                return proceedingJoinPoint.proceed(args);
            }
        }

        return null;
    }

}
