package com.eugene.sumarry.springbootredis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.eugene.sumarry.springbootredis.dao")
public class SpringRedisClusterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRedisClusterApplication.class, args);
    }
}
