package com.eugene.sumarry.springbootstudy;

import com.eugene.sumarry.autoconfigure.byannotation.EnableEugeneByAnnotationProperties;
import com.eugene.sumarry.autoconfigure.byannotation.EugeneByAnnotationProperties;
import com.eugene.sumarry.autoconfigure.byspringfactories.EugeneBySpringFactoriesProperties;
import com.eugene.sumarry.springbootstudy.poflow.anno.EnablePOFlow;
import com.eugene.sumarry.springbootstudy.properties.UserProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ConfigurableApplicationContext;

@EnableEugeneByAnnotationProperties
@SpringBootApplication
@EnablePOFlow
public class SpringbootStudyApplication {

    public static void main(String[] args) {
        // 引入配置到系统环境配置中，log4j2会加载这个类：org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        // 再打印日志前，如果有Thread.currentThread().setName("avengerEug");方式修改当前线程名称并需要打印到日志文件中的需求。则需要添加此属性
        // log4j2默认是使用threadLocal缓存线程名以提高性能
        System.setProperty("AsyncLogger.ThreadNameStrategy", "UNCACHED");

        SpringApplication application = new SpringApplication(SpringbootStudyApplication.class);
        ConfigurableApplicationContext context = application.run(args);

        System.out.println(context.getBean(UserProperties.class));
        System.out.println(context.getBean(EugeneByAnnotationProperties.class));
        System.out.println(context.getBean(EugeneBySpringFactoriesProperties.class));
    }

}
