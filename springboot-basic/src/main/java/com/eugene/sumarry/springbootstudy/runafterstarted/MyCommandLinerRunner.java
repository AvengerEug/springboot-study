package com.eugene.sumarry.springbootstudy.runafterstarted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class MyCommandLinerRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCommandLinerRunner.class);

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("springboot 启动后会调用该方法, 以CommandLineRunner方式, 使用Order注解或者Ordered接口可以实现两个调用者的顺序执行, 当前注解是@Order(1)");
    }
}
