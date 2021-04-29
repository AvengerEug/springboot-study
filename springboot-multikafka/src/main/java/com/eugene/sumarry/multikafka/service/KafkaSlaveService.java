package com.eugene.sumarry.multikafka.service;

import com.eugene.sumarry.multikafka.constants.Constants;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaSlaveService {


    /**
     * 大数据通过 kafka.master.slave 推送消息，由此handler进行消费
     * @param content
     */
    @KafkaListener(containerFactory = Constants.KAFKA_SLAVE, topics = "kafka.master.slave")
    public void handler(String content) {
        System.out.println(content);
    }

}
