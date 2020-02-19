# Spring Boot学习系列

## 一、启动流程
### 1.1 tomcat启动时间

* org.springframework.boot.SpringApplication#refreshContext(ConfigurableApplicationContext context)

  刷新上下文(当前传入的context的类型为**AnnotationConfigServletWebServerApplicationContext**), 内部执行了refresh方法, 如下： 
  
  ```java
  protected void refresh(ApplicationContext applicationContext) {
      Assert.isInstanceOf(AbstractApplicationContext.class, applicationContext);
      ((AbstractApplicationContext) applicationContext).refresh();
  }
  ```
  
  虽然它把传入的类强转成AbstractApplicationContext类型了，但是最终执行的还是AnnotationConfigServletWebServerApplicationContext类的refresh方法. 最终执行父类AbstractApplicationContext的refresh方法。在父类的refresh方法就是在初始化spring上下文环境. 当执行到onRefresh方法时, 因为调用链的起始对象为子类。所以父类和子类的onRefresh方法都会被执行到。springboot在此(AnnotationConfigServletWebServerApplicationContext类的onRefresh方法)对tomcat进行了初始化. 最终在子类ServletWebServerApplicationContext中的createWebServer方法中(org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext#createWebServer)中启动了tomcat

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

   

   



## 四、@Conditionl注解即其他条件注解总结

