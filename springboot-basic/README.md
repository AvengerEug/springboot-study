# Spring Boot学习系列

## 一、启动流程
### 1.1 tomcat启动时间

* 验证当前springboot属于哪种应用：`NONE`、`SERVLET`、`REACTIVE`。在此方法中用来确定当前是属于哪种应用(判断classpath中是否存在对应的标识性的类，eg：如果不包含javax.servlet.Servlet、和org.springframework.web.context.ConfigurableWebApplicationContext则表示当前springboot应用就是一个普通的spring应用)

  ```java
  //org.springframework.boot.SpringApplication#deduceWebApplicationType
  
  private WebApplicationType deduceWebApplicationType() {
      if (ClassUtils.isPresent(REACTIVE_WEB_ENVIRONMENT_CLASS, null)
          && !ClassUtils.isPresent(MVC_WEB_ENVIRONMENT_CLASS, null)
          && !ClassUtils.isPresent(JERSEY_WEB_ENVIRONMENT_CLASS, null)) {
          return WebApplicationType.REACTIVE;
      }
      for (String className : WEB_ENVIRONMENT_CLASSES) {
          if (!ClassUtils.isPresent(className, null)) {
              return WebApplicationType.NONE;
          }
      }
      return WebApplicationType.SERVLET;
  }
  
  ```

* org.springframework.boot.SpringApplication#refreshContext(ConfigurableApplicationContext context)

  刷新上下文(当前传入的context的类型为**AnnotationConfigServletWebServerApplicationContext**), 内部执行了refresh方法, 如下： 
  
  ```java
  protected void refresh(ApplicationContext applicationContext) {
      Assert.isInstanceOf(AbstractApplicationContext.class, applicationContext);
      ((AbstractApplicationContext) applicationContext).refresh();
  }
  ```
  
  虽然它把传入的类强转成AbstractApplicationContext类型了，但是最终执行的还是AnnotationConfigServletWebServerApplicationContext类的refresh方法. 最终执行父类AbstractApplicationContext的refresh方法。在父类的refresh方法就是在初始化spring上下文环境. 当执行到onRefresh方法时, 因为调用链的起始对象为子类。springboot在此(AnnotationConfigServletWebServerApplicationContext类的onRefresh方法)对tomcat进行了初始化. 最终在子类ServletWebServerApplicationContext中的createWebServer方法中(org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext#createWebServer)中启动了tomcat

### 1.2 DispatcherServlet类何时被初始化
1. 启动tomcat时需要获取一个类型为**ServletWebServerFactory**的工厂类来创建web server(源码是根据type来获取bean的). 最终获取的类为TomcatServletWebServerFactory。因为此bean是在**CloudFoundryCustomContextPathExample** 类中被添加到spring bean工厂去的(通过@Bean的方式)。最终根据type获取的bean name，此时只有这一个(TomcatServletWebServerFactory)类

2. 在spring初始化TomcatServletWebServerFactory bean并开始处理BeanPostProcessor后置处理器时，在此**ErrorPageRegistrarBeanPostProcessor**后置处理器中为**TomcatServletWebServerFactory**注册了错误页面, 代码如下: 

   ```java
   // ErrorPageRegistrarBeanPostProcessor.java
   // 传入的registry就是当前创建的bean = TomcatServletWebServerFactory
   private void postProcessBeforeInitialization(ErrorPageRegistry registry) {
      // getRegistrars()方法在下面被展示了，最终会往spring容器中去获取类型为ErrorPageRegistrar的bean
      for (ErrorPageRegistrar registrar : getRegistrars()) {
         registrar.registerErrorPages(registry);
      }
   }
   
   private Collection<ErrorPageRegistrar> getRegistrars() {
       if (this.registrars == null) {
           // Look up does not include the parent context
           this.registrars = new ArrayList<>(
               // ErrorPageRegistrar类型的bean目前只有一个内部类: org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration.ErrorPageCustomizer， 所以解析来该实例化它
               this.beanFactory.getBeansOfType(ErrorPageRegistrar.class, false, false).values());
           this.registrars.sort(AnnotationAwareOrderComparator.INSTANCE);
           this.registrars = Collections.unmodifiableList(this.registrars);
       }
       return this.registrars;
   }
   ```

3. 当在实例化**ErrorPageCustomizer**, 发现它的beanDefinition中的factoryBeanName和factoryMethodName属性都存在值，分别为: org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration 和errorPageCustomizer。表示这个bean是在类名为ErrorMvcAutoConfiguration的类中的errorPageCustomizer方法被创建的。 所以要想调用errorPageCustomizer方法，则要先实例化ErrorMvcAutoConfiguration类。所以接下来要开始创建ErrorMvcAutoConfiguration这个bean。在创建这个bean时，因为只提供了一个有参构造方法，所以会自动装配。有参构造参数如下:

   ```java
   // 最终开始自动装配三个类:
   // serverProperties, dispatcherServletPath, errorViewResolvers
   public ErrorMvcAutoConfiguration(ServerProperties serverProperties, DispatcherServletPath dispatcherServletPath,
         ObjectProvider<ErrorViewResolver> errorViewResolvers) {
      this.serverProperties = serverProperties;
      this.dispatcherServletPath = dispatcherServletPath;
      this.errorViewResolvers = errorViewResolvers.orderedStream().collect(Collectors.toList());
   }
   ```

4. 在创建dispatcherServletPath这个类型的bean时(这个类: DispatcherServletRegistrationBean, 但名字为dispatcherServletRegistration)。同时，这个bean也是以@Bean的方式创建的(在name为dispatcherServletRegistration的beanDefinition中，它的factoryName为org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration$DispatcherServletRegistrationConfiguration。factoryMethodName为dispatcherServletRegistration)。 同上，要执行dispatcherServletRegistration方法，必须先创建**DispatcherServletRegistrationConfiguration**这个bean。而这个bean只提供了一个构造方法，所以spring在创建它时会自动填充进去需要依赖的属性dispatcherServlet，构造方法如下：

   ```java
   // 构造方法依赖了dispatchserServlet，所以最终会从spring容器中去找DispatcherServlet
   public DispatcherServletRegistrationBean(DispatcherServlet servlet, String path) {
      super(servlet);
      Assert.notNull(path, "Path must not be null");
      this.path = path;
      super.addUrlMappings(getServletUrlMapping());
   }
   ```

5. 开始创建DispatcherServlet, 因为此bean是在org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration.DispatcherServletConfiguration内部类中维护的。因为该方法在DispatcherServletAutoConfiguration类的内部类DispatcherServletConfiguration中，所以待这两个bean都创建好了后，则开始创建dispatcherServlet

6. 最终完成DispatcherServlet类的创建，但它存在**DispatcherServletRegistrationBean** 这个bean中

### 1.3 DispatcherServlet类如何绑定spring环境中

* 因为在初始化DispatcherServlet bean时会加载到**DispatcherServletAutoConfiguration**类, 此类中也维护了许多bean, 其中就包含了一个叫dispatcherServletRegistration. 类型为DispatcherServletRegistrationBean的bean

* 当启动tomcat时, tomcat会调用实现了**ServletContextInitializer**接口的类的onStartup方法. 最终在RegistrationBean类中的onStartup方法中对servlet进行了注册(注册到了ServletContext中, 调用到子类DynamicRegistrationBean的register方法, 完成了servlet与容器的绑定)。

* 这里会有个疑问：就是此时我拿到的只有servletContext对象，并拿不到DispatcherServlet对象呀，tomcat的这个onStart方法也没有把spring 上下文对象给依赖进去，否则可以通过spring 上下文对象来获取bean。 那么是怎么拿到DispatcherServlet这个对象的呢？？？？

  上面说了，**DispatcherServletRegistrationBean**是在**DispatcherServletAutoConfiguration**这个类中被初始化的，它里面封装了DispatcherServlet。恰巧，DispatcherServletRegistrationBean这个bean也实现了ServletContextInitializer接口，所以可以获取到它维护的DispatcherServlet，最终直接绑定到servlet 上下文中去了

## 二、自动装配

### 2.1 实现步骤

1. 添加实体配置类

   ```java
   @Component
   @ConfigurationProperties("sys")
   public class UserProperties {
   
       private String name;
       private List<User> userList = new ArrayList<>();
   
       public String getName() {
           return name;
       }
   
       public void setName(String name) {
           this.name = name;
       }
   
       public List<User> getUserList() {
           return userList;
       }
   
       public void setUserList(List<User> userList) {
           this.userList = userList;
       }
   
       @Override
       public String toString() {
           return "UserProperties{" +
                   "name='" + name + '\'' +
                   ", userList=" + userList +
                   '}';
       }
   }
   ```

2. 添加@ConfigurationProperties(prefix = "sys")注解，标明此类为配置类，并配置的前缀叫sys，如下:

   ```yml
   sys:
     name: "springboot-study"
     userList:
       - userId: 1
         userName: "eugene1"
       - userId: 2
         userName: "eugene2"
   ```

3. 添加@Component注解，为了将此类加入到spring容器中去

4. 最终此类被自动配置进去了

### 2.2 存在的问题

* idea中没法提示自己自定义的配置类，具体原因待确认。按照[官网的推荐方法: https://docs.spring.io/spring-boot/docs/2.0.4.RELEASE/reference/html/configuration-metadata.html#configuration-metadata-annotation-processor](https://docs.spring.io/spring-boot/docs/2.0.4.RELEASE/reference/html/configuration-metadata.html#configuration-metadata-annotation-processor)也无作用，我认为在springboot中这么设置也没有啥用，因为依赖了spring-boot-parent模块，在此模块中它依赖了**spring-boot-auto-configuration**模块

## 三、实现自定义auto-configure模块

1. 通过注解(by-annotation)的方式引入自定义auto-configure模块(eugene-spring-boot-autoconfigure-by-annotation)

   * EugeneByAnnotationProperties.java

     ```java
     @ConfigurationProperties(prefix = "eugene-by-annotation")
     public class EugeneByAnnotationProperties {
     
         private String name;
         private Integer age;
         private Boolean isMan;
     
         public String getName() {
             return name;
         }
     
         public void setName(String name) {
             this.name = name;
         }
     
         public Integer getAge() {
             return age;
         }
     
         public void setAge(Integer age) {
             this.age = age;
         }
     
         public Boolean getMan() {
             return isMan;
         }
     
         public void setMan(Boolean man) {
             isMan = man;
         }
     
         @Override
         public String toString() {
             return "EugeneByAnnotationProperties{" +
                     "name='" + name + '\'' +
                     ", age=" + age +
                     ", isMan=" + isMan +
                     '}';
         }
     }
     ```

   * EnableEugeneByAnnotationProperties.java\

     ```java
     @Target(ElementType.TYPE)
     @Retention(RetentionPolicy.RUNTIME)
     @Import(EugeneByAnnotationProperties.class)
     public @interface EnableEugeneByAnnotationProperties {
     }
     ```

2. 通过spring.factorires文件的方式引入自定义auto-configure模块(eugene.spring-boot-autoconfigure-by-springfactories)

   * 在resources文件夹下添加该文件`META-INF/spring.factories`, 并在spring.factories文件中添加如下内容: 

     ```properties
     # Auto Configure
     org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
     com.eugene.sumarry.autoconfigure.byspringfactories.EugeneBySpringFactoriesProperties
     ```

   * EugeneBySpringFactoriesProperties.java文件

     ```java
     @EnableConfigurationProperties
     @ConfigurationProperties(prefix = "eugene-by-spring-factories")
     public class EugeneBySpringFactoriesProperties {
     
         private String name;
         private Integer age;
         private Boolean isMan;
     
         public String getName() {
             return name;
         }
     
         public void setName(String name) {
             this.name = name;
         }
     
         public Integer getAge() {
             return age;
         }
     
         public void setAge(Integer age) {
             this.age = age;
         }
     
         public Boolean getMan() {
             return isMan;
         }
     
         public void setMan(Boolean man) {
             isMan = man;
         }
     
         @Override
         public String toString() {
             return "EugeneBySpringFactoriesProperties{" +
                     "name='" + name + '\'' +
                     ", age=" + age +
                     ", isMan=" + isMan +
                     '}';
         }
     }
     ```

3. 各自模块的pom.xml文件中添加如下依赖:

   ```xml
   <dependency>
       <groupId>org.springframework</groupId>
       <artifactId>spring-context</artifactId>
       <version>5.0.9.RELEASE</version>
       <scope>provided</scope>
   </dependency>
   
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-autoconfigure</artifactId>
       <version>2.0.4.RELEASE</version>
       <scope>provided</scope>
   </dependency>
   
   <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-configuration-processor -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-configuration-processor</artifactId>
       <version>2.0.4.RELEASE</version>
       <scope>provided</scope>
   </dependency>
   ```

4. 其他模块依赖这两个模块，并在yml文件进行配置, 如下图: 

   ![auto-config](https://github.com/AvengerEug/springboot-study/blob/develop/springboot-basic/yml-autoconfig.png)

5. 遇到的问题:

   * **Q:idea中没有上图的提示**
   * `A:我是将自动装配的那两个模块install到本地maven仓库后就提示了(idea还需要装spring assistant插件)。。`然后[官网推荐(点此查看)](https://docs.spring.io/spring-boot/docs/2.0.4.RELEASE/reference/html/configuration-metadata.html#configuration-metadata-annotation-processor)`是要添加**spring-boot-configuration-processor**模块`
   * **Q: pom文件不知道添加哪些依赖**
   * `A: 因为要添加自动装配，所以必须要依赖spring-boot-autoconfigure模块，其次要想在idea中的yml文件中有提示，则需要添加spring-boot-configuration-processor模块。 最后因为要有spring的功能，比如将这个配置类添加到spring容器中，所以还需要添加spring-context模块`
   * **Q: 为什么每个依赖的scope都要加provided**
   * `A: 这个主要看项目而定，因为我的测试项目中有springboot的环境，而springboot环境中默认存在上述的几个依赖，为了不让jar包冲突，所以就将scope设置成provided了。即默认项目中已经存在这些jar包了`
   
6. 原理

   * by-annotation原理: **其实很简单，就是将加了@ConfigurationProperties注解的配置类添加到spring容器中即可。但是呢，springboot项目启动时，@SpringBootApplication注解默认是扫描当前启动类所在的包及其子包的。作为第三方jar包，因为存在包名不一致的问题，所以使用传统的@Component注解会行不通。所以我们可以用spring的一些扩展点来添加第三方jar包的配置类。可是哪一个扩展点是可以将第三方的类导入到当前spring环境中呢？1. 使用后置处理器BeanPostProcessor？ => 这种方式不可行，因为要修改集成方的代码，集成方工作量大，要是我遇到这种第三方的jar包，那我会吐槽的！ 但是呢，好像必须要让集成方做点什么才能把jar包集成进去，比如spring提供的aop，我们使用@EnableAspectJAutoProxy注解就可以动态开启aop功能。利用这个思路，最终我们定位到了spring的@Import注解，它可以将一个类添加到spring环境中去。所以最终，我设计了@EnableEugeneByAnnotationProperties注解，最终将配置类添加到了spring容器中去**
   * by-spring-factories原理: **具体原理跟上述一直，总而言之就是将被加了@ConfigurationProperties注解的配置类添加到spring容器中去。在springboot中，存在spring.factories文件，在里面配置一些类也会被加到spring容器中去，所以最终产生了这种方式来添加第三方bean至spring容器**

## 四、@Conditionl注解即其他条件注解总结

|           类型            |                         含义                          |                             例子                             |
| :-----------------------: | :---------------------------------------------------: | :----------------------------------------------------------: |
|    @ConditionalOnBean     |          表示当某个bean存在后才处理当前bean           | 比如mybati的自动配置类: MybatisAutoConfiguration. 该类添加了@ConditionalOnBean(DataSource.class)注解，即表示当有数据源这个bean时才处理MybatisAutoConfiguration这个bean |
|    @ConditionalOnClass    | 表示当某个类存在(在classpath中找得到)时才处理当前bean | 比如mybati的自动配置类: MybatisAutoConfiguration。该类添加了@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })。表示当classpath中有这两个类时菜处理这个bean |
| @ConditionalOnMissingBean |          表示当spring环境中无某个类时才处理           | @ConditionalOnMissingBean注解中若没有传class进去，那么则默认认为无当前bean时才处理。eg:@Bean @ConditionalOnMissingBean public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory){....}. 则表示，spring容器中无SqlSessionTemplate这个类型的bean时才执行这个方法 |

## 五、springboot-basic项目的日志特性

* 参考log4j2.xml文件，一共有两个loggers，分别为bizInfo和root。当我们使用如下代码打印日志时：

  ```java
  private static final Logger LOGGER = LoggerFactory.getLogger("bizInfo");
  ```

  会使用名称为bizInfo的logger，以及logger关联的append：biz.log。最终发现，logger和append设置的都是warn级别的日志：

  ```xml
  <!-- append: -->
  <RollingFile name="biz.log" fileName="${sys:user.home}/springboot-basic/logs/biz.log"
                       filePattern="${sys:user.home}/springboot-basic/logs/$${date:yyyy-MM}/biz-%d{yyyy-MM-dd}-%i.log"
                       append="true">
      <!-- 
        level：表示只针对此级别
                      onMatch="ACCEPT" 表示匹配该级别即以上
                      onMatch="DENY"   表示不匹配该级别即以上
  
        onMismatch="ACCEPT" 表示匹配该级别即以下
        onMismatch="DENY" 表示不匹配该级别即以下
                  通常我们都是用level指定级别，因此onMatch和onMismatch都可以不填
                  -->
      <ThresholdFilter level="warn" onMatch="ACCEPT" onMismatch="DENY"/>
  </RollingFile>
  
  <!-- logger -->
  <logger name="bizInfo" level="warn" additivity="false">
      <appender-ref ref="biz.log"/>
  </logger>
  ```

  最终发现日志文件在：**${sys:user.home}/springboot-basic/logs/biz.log** 下。而${sys:user.home}代表啥意思呢？可以在linux系统或git bash环境下：执行如下命令

  ```shell
  echo ~
  # 输出结果：/c/Users/avengerEug
  ```

* 当我们使用其他方式获取logger对象时：

  ```java
  private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalController.class);
  ```

  则会默认使用root的logger配置来打印日志

  ```xml
  <root level="info">
      <appender-ref ref="Console" />
      <appender-ref ref="application.log"/>
  </root>
  ```

  最终会打印到Console和application.log的append中。其中Console配置的是控制台的输出，application.log配置的是**${sys:user.home}/springboot-basic/logs/application.log**日志

* 当我们需要使用log4j2的异步功能时，需要执行如下操作：

  1. 添加对应的异步二方包（log4j底层是依赖这个二方包）：

     ```xml
     <dependency>
         <groupId>com.lmax</groupId>
         <artifactId>disruptor</artifactId>
         <version>3.3.4</version>
     </dependency>
     ```

  2. 添加系统变量，有很多方式添加：

     1. 通过log4j2的配置文件添加。方式：在classpath下添加`log4j2.component.properties`
     2. 在启动java jar包时，添加jvm变量
     3. 在代码中使用System.setProperty接口添加变量

     具体有哪些提供变量呢？参考官方：[Log4j – Log4j 2 Lock-free Asynchronous Loggers for Low-Latency Logging - Apache Log4j 2](http://home.apache.org/~rpopma/log4j/2.6/manual/async.html)   使用关键字：`System Properties to configure all asynchronous loggers` 查找表格。

     最重要的变量为（以System.setProperty接口添加变量为例）：

     ```java
     // 此变量表示要开启异步打印日志
     System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
     ```

  3. 如何验证是否为异步？一开始我是想通过日志中的线程名来判断，再经过一番折腾后，发现这种方式不行。因为，异步打印日志前，log4j2会把当前业务线程传递到log4j2的异步线程中去，最后是以业务线程为准。那我们要怎么去验证？只能使用最原始的方式：debug查看logger对象![image-20231223145105112](.\image-20231223145105112.png)

