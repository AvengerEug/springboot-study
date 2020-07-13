package com.eugene.sumarry.springbootredis.dao;

import com.eugene.sumarry.springbootredis.model.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsDao {


    Goods getById(Long goodsId);

    Long count();

    List<Long> fetchIdByPagination(@Param("offset") Long offset, @Param("pageSize") Long pageSize);

    void reduceGoods(Long goodsId);

    Long getCountById(Long goodsId);

}
