package com.eugene.sumarry.springbootstudy.spi;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class SpiTwo implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        System.out.println("Spi two");
    }
}
