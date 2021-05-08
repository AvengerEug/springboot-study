package com.eugene.sumarry.clickhouse;

import com.alibaba.fastjson.JSON;
import com.eugene.sumarry.clickhouse.curd.dao.KafkaEngineDao;
import com.eugene.sumarry.clickhouse.curd.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 测试kafka存储引擎
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClickhouseKafkaEngineTest {

    @Autowired
    private KafkaEngineDao kafkaEngineDao;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaHost;

    @Test
    public void test() throws IOException {
        if (checkIsNotExists("kafka_engine_test")) {
            kafkaEngineDao.createKafkaEngineTable("kafka_engine_test", "sales-queue", kafkaHost);
            kafkaEngineDao.createTable("kafka_table");
            kafkaEngineDao.createView("kafka_table", "kafka_engine_test");
        }

        System.in.read();
    }

    private boolean checkIsNotExists(String table) {
        return !kafkaEngineDao.showTables().contains(table);
    }

    @Test
    public void sendMsg() {
       Map<String, Object> map = new HashMap<>();
       map.put("id", 1);
       map.put("code", "eugene");
       map.put("name", "avengerEug");

        kafkaTemplate.send("sales-queue", JSON.toJSONString(map));
    }

}
