package com.eugene.sumarry.springbootredis.controller;

import com.eugene.sumarry.springbootredis.model.Message;
import com.eugene.sumarry.springbootredis.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/{goodsId}/cache-ct")
    public Message getCacheCT(@PathVariable Long goodsId) {
        return Message.ok(goodsService.getCacheCT(goodsId));
    }


    @GetMapping("/{goodsId}/cache-jc")
    public Message getCacheJC(@PathVariable Long goodsId) {
        return Message.ok(goodsService.getCacheJC(goodsId));
    }

}
