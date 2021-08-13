package com.eugene.sumarry.multikafka;

import com.eugene.sumarry.multikafka.service.KafkaMasterService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
@EnableConfigurationProperties
public class MultiKafkaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(MultiKafkaApplication.class);
        KafkaMasterService bean = run.getBean(KafkaMasterService.class);
        KafkaTemplate slaveKafkaTemplate = (KafkaTemplate) run.getBean("slaveKafkaTemplate");
        KafkaTemplate masterKafkaTemplate = (KafkaTemplate) run.getBean("masterKafkaTemplate");
        run.getBean(KafkaMasterService.class).send();
        System.out.println(1);
    }
}
