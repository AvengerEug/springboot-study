package com.eugene.sumarry.springbootredis.controller;

import com.eugene.sumarry.springbootredis.model.Message;
import com.eugene.sumarry.springbootredis.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private Environment environment;

    @GetMapping("/{goodsId}/cache-ct")
    public Message getCacheCT(@PathVariable Long goodsId) {
        return Message.ok(goodsService.getCacheCT(goodsId));
    }


    @GetMapping("/{goodsId}/cache-jc")
    public Message getCacheJC(@PathVariable Long goodsId) {
        // 搭建nginx反向代理负载均衡时，打印此日志来表明当前请求的是哪个实例
        System.out.println(environment.getProperty("server.port"));
        return Message.ok(goodsService.getCacheJC(goodsId));
    }


    @PutMapping("/reduce/{goodsId}")
    public Message reduceGoods(@PathVariable Long goodsId) {
        if (goodsService.reduceGoods(goodsId)) return Message.ok();
        return Message.error("秒杀失败");
    }

}
