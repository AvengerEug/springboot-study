package com.eugene.sumarry.springbootredis.service;

import com.eugene.sumarry.springbootredis.model.Goods;

public interface GoodsService {

    Goods getCacheCT(Long goodsId);

    Goods getCacheJC(Long goodsId);
}