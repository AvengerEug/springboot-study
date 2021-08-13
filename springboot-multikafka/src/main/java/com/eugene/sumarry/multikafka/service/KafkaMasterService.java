package com.eugene.sumarry.multikafka.service;

import com.eugene.sumarry.multikafka.config.KafkaPropertiesAutoConfiguration;
import com.eugene.sumarry.multikafka.constants.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaMasterService {


    /**
     * 构建kafkaTemplate时，因为存在多个KafkaListenerContainerFactory类型的bean，
     * 因此默认会填充待@Primary注解的bean，
     * 而@Primary注解的KafkaListenerContainerFactory链接的kafka master实例
     * {@link KafkaPropertiesAutoConfiguration#masterKafkaListenerContainerFactory()}
     */
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 调用此方法，发送消息至kafka-master.topic,
     * 由下面的handler方法进行消费
     */
    public void send() {
        kafkaTemplate.send("kafka.master.topic", "hi, master");
    }


    @KafkaListener(containerFactory = Constants.KAFKA_MASTER, topics = "kafka.master.topic")
    public void handler(Object record) {
        System.out.println(record);
    }

}
