# spring集成redis模块

## 一、集成redisson

- [x] 集成redisson-spring-boot-starter

- [x] 使用aop + 布隆过滤器完成**缓存穿透**功能

- [ ] 模拟缓存击穿，项目启动后，把热点key(整个商品信息)放入redis中，并设置10s后过期，随后掐好时间点使用JMeter模拟5000个请求进行压测，看会不会把db打死

- [ ] 根据spring启动环境连接对应的redis模式(单机、集群)

- [ ] 使用nginx + 应用集群部署实现分布式锁(单机、集群)，并做压测

  