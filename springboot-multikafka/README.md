# 使用springboot链接多个kafka实例

* springboot内置的kafkaTemplate默认链接的是配置文件spring.multikafka.master.servers指向的kafka实例，因此使用kafkaTemplate发送消息时，默认是发送到配置文件spring.multikafka.master.servers指向的kafka

* 如果也想使用kafkaTemplate发送消息到配置文件spring.multikafka.slave.servers指向的kafka实例的话，这个时候就要创建两个kafkaTemplate了，并且使用kafkaTemplate的构造器，传入要链接的kafka实例，如下所示：
    ```java
  
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
    ```
    
    在注入kafkaTemplate时，就要指定kafkaTemplate的名字来注入了，否则spring会**存在类型相同的多个bean，但不知道依赖注入哪个bean**的错误。其主要思想就是使用@Primary注解和注入时指定bean名称，以及在创建kafkaTemplate时指定要连接的kafka实例。
    
* 针对@KafkaListener注解，可以指定多个要监听哪个kafka实例，示例如下：

    ```java
    // 使用containerFactory属性指定要连接kafka实例的bean name，其实就是在名字叫Constants.KAFKA_SLAVE的bean中对kafka的链接做了处理
    @KafkaListener(containerFactory = Constants.KAFKA_SLAVE, topics = "kafka.master.slave")
    ```

    