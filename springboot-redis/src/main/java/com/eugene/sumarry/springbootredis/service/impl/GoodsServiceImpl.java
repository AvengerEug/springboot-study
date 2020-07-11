package com.eugene.sumarry.springbootredis.service.impl;

import com.eugene.sumarry.springbootredis.anno.CacheCT;
import com.eugene.sumarry.springbootredis.dao.GoodsDao;
import com.eugene.sumarry.springbootredis.model.Goods;
import com.eugene.sumarry.springbootredis.service.GoodsService;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl implements GoodsService {


    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private RedissonClient redissonClient;

    @CacheCT
    @Override
    public Goods getCacheCT(Long goodsId) {
        return goodsDao.getById(goodsId);
    }


    @Override
    public Goods getCacheJC(Long goodsId) {

        RMap<Object, Object> rMap = redissonClient.getMap("goods:" + goodsId);
        if (rMap.size() > 0) {
            Goods goods = new Goods();
            goods.setGoodsId((Long)rMap.get("id"));
            goods.setName((String)rMap.get("name"));
            goods.setCount((Long)rMap.get("count"));
            return goods;
        }

        return goodsDao.getById(goodsId);
    }
}
