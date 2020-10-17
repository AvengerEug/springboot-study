package com.eugene.sumarry.springbootstudy.poflow.spring;

import com.eugene.sumarry.springbootstudy.poflow.action.MongoDBAction;
import com.eugene.sumarry.springbootstudy.poflow.aspect.MongoTestAspectBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

public class POFlowConfigured {

    @Bean
    public MongoDBAction mongoDBAction() {
        System.out.println("================mongoDBAction==============");
        return new MongoDBAction();
    }

    @Bean
    @ConditionalOnBean(MongoDBAction.class)
    public MongoTestAspectBean mongoTestAspectBean() {
        return new MongoTestAspectBean();
    }

}
