package com.eugene.sumarry.springbootstudy.runafterstarted;

import com.eugene.sumarry.springbootstudy.properties.UserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class MyApplicationRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCommandLinerRunner.class);

    @Autowired
    private UserProperties userProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("以ApplicationRunner方式在springboot启动后调用该方法, 使用Order注解或者Ordered接口可以实现两个调用者的顺序执行, 当前注解是@Order(2)");
        //System.out.println(userProperties);
    }
}
