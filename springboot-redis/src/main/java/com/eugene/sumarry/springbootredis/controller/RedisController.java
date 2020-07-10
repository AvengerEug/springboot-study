package com.eugene.sumarry.springbootredis.controller;

import com.eugene.sumarry.springbootredis.service.GoodsService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/add")
    public void add(@RequestParam(name = "key") String key, @RequestParam(name = "value") String value) {
        redissonClient.getBucket(key).set(value);
    }


    @GetMapping("/get")
    public Object get(@RequestParam(name = "key") String key) {
        return redissonClient.getBucket(key).get();
    }


}
