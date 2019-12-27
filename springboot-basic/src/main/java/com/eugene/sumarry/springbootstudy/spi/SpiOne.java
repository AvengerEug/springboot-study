package com.eugene.sumarry.springbootstudy.spi;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class SpiOne implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        System.out.println("Spi one");
    }
}
