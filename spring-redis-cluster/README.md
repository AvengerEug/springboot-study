# spring集成redis模块

## 一、spring-boot-starter-data-redis模块文档
[https://docs.spring.io/spring-data/data-redis/docs/current/reference/html](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html)


## 二、redis-cluster 集群搭建

* 这块比较简单, 后面将写一篇博客来总结**redis-cluster**集群的搭建以及原理

## 三、遇到的问题

* 只导入如下包:
  ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
  ```
  会报下面的错误:
  ```text
    java: 无法访问redis.clients.jedis.JedisPoolConfig
    找不到redis.clients.jedis.JedisPoolConfig的类文件
  ```
  
* 解决方案
  添加如下jar包:
    ```xml
       <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>
    ```
    
### 三、总结
* 浏览器访问localhost:8080/redis/add?key=eugene&value=1111
* 验证key: localhost:8080/redis/get?key=eugene  => 将返回1111
* 使用redis-cli -c -h 192.168.6.131/132 -p 7001/2/3/4/5/6 连接redis, 查看`eugene`这个key存在哪个服务中
  并将对应的服务停掉, 再次访问`localhost:8080/redis/get?key=eugene`, 浏览器若能看到1111
  则证明集群生效了
