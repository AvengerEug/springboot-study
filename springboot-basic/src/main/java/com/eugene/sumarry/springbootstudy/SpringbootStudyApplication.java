package com.eugene.sumarry.springbootstudy;

import com.eugene.sumarry.autoconfigure.byannotation.EnableEugeneByAnnotationProperties;
import com.eugene.sumarry.autoconfigure.byannotation.EugeneByAnnotationProperties;
import com.eugene.sumarry.autoconfigure.byspringfactories.EugeneBySpringFactoriesProperties;
import com.eugene.sumarry.springbootstudy.poflow.anno.EnablePOFlow;
import com.eugene.sumarry.springbootstudy.properties.UserProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ConfigurableApplicationContext;

@EnableEugeneByAnnotationProperties
@SpringBootApplication
@EnablePOFlow
public class SpringbootStudyApplication {

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(SpringbootStudyApplication.class);
        ConfigurableApplicationContext context = application.run(args);

        System.out.println(context.getBean(UserProperties.class));
        System.out.println(context.getBean(EugeneByAnnotationProperties.class));
        System.out.println(context.getBean(EugeneBySpringFactoriesProperties.class));
    }

}
