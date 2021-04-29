package com.eugene.sumarry.multikafka.config;

import com.eugene.sumarry.multikafka.constants.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.multikafka")
public class KafkaPropertiesAutoConfiguration {

    private Master master;

    private Slave slave;

    public static class Master {
        private String servers = "localhost:9092";

        private String groupId = "master-consumer-group";

        public String getServers() {
            return servers;
        }

        public void setServers(String servers) {
            this.servers = servers;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }

    public static class Slave {
        private String servers = "localhost:9092";

        private String groupId = "slave-consumer-group";

        public String getServers() {
            return servers;
        }

        public void setServers(String servers) {
            this.servers = servers;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public Slave getSlave() {
        return slave;
    }

    public void setSlave(Slave slave) {
        this.slave = slave;
    }

    /**
     * 连接master kafka实例
     */
    @Bean(Constants.KAFKA_MASTER)
    @Primary
    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> masterKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(masterDefaultKafkaContainerFactoryConfig()));
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    @Primary
    private Map<String, Object> masterDefaultKafkaContainerFactoryConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, master.servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, master.groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return props;
    }


    @Bean
    @Primary
    private ProducerFactory masterDefaultKafkaProducerFactory() {
        return new DefaultKafkaProducerFactory(masterDefaultKafkaContainerFactoryConfig());
    }

    @Bean("masterKafkaTemplate")
    @Primary
    private KafkaTemplate kafkaTemplate() {
        return new KafkaTemplate(masterDefaultKafkaProducerFactory());
    }

    /**
     * ######################## 上面是kafka master实例相关的配置 #######################
     */

    @Bean
    private Map<String, Object> slaveDefaultKafkaContainerFactoryConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, slave.servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, master.groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return props;
    }


    @Bean
    private ProducerFactory slaveDefaultKafkaProducerFactory() {
        return new DefaultKafkaProducerFactory(slaveDefaultKafkaContainerFactoryConfig());
    }

    @Bean("slaveKafkaTemplate")
    private KafkaTemplate slaveKafkaTemplate() {
        return new KafkaTemplate(slaveDefaultKafkaProducerFactory());
    }


    @Bean(Constants.KAFKA_SLAVE)
    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> slaveKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, slave.servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, slave.groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

}
