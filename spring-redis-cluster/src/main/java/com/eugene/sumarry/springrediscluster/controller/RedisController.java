package com.eugene.sumarry.springrediscluster.controller;

import com.eugene.sumarry.springrediscluster.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/add")
    public void add(@RequestParam(name = "key") String key, @RequestParam(name = "value") String value) {
        redisService.add(key, value);
    }


    @GetMapping("/get")
    public Object get(@RequestParam(name = "key") String key) {
        return redisService.get(key);
    }


}
