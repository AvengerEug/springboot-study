package com.eugene.sumarry.springbootredis.runner;

import com.eugene.sumarry.springbootredis.dao.GoodsDao;
import com.eugene.sumarry.springbootredis.model.Goods;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SaveHotGoodsInfo implements ApplicationRunner {


    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GoodsDao goodsDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Goods goods = goodsDao.getById(200L);
        RMap cache = redissonClient.getMap("goods:" + goods.getGoodsId());
        cache.fastPut("id", goods.getGoodsId());
        cache.fastPut("name", goods.getName());
        cache.fastPut("count", goods.getCount());
        // 此处模拟10s后过期，因为这个key不是作为分布式锁的key，所以不需要担心程序在此处挂了导致死锁的问题。
        cache.expire(10, TimeUnit.SECONDS);

    }
}
