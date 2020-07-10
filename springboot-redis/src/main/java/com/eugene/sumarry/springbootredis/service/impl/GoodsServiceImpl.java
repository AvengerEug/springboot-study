package com.eugene.sumarry.springbootredis.service.impl;

import com.eugene.sumarry.springbootredis.anno.CacheCT;
import com.eugene.sumarry.springbootredis.dao.GoodsDao;
import com.eugene.sumarry.springbootredis.model.Goods;
import com.eugene.sumarry.springbootredis.service.GoodsService;
import org.apache.commons.beanutils.BeanUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;


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
        if (rMap != null) {
            Map map = rMap.getAll(rMap.keySet());
            Goods goods = new Goods();
            try {
                BeanUtils.populate(new Goods(), map);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return goods;
        }

        return goodsDao.getById(goodsId);
    }
}
