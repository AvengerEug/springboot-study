package com.eugene.sumarry.springbootstudy.config;

import com.eugene.sumarry.springbootstudy.filter.AppContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public FilterRegistrationBean appContextFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AppContextFilter());

        registration.addUrlPatterns("/*");
        registration.setName("appContextFilter");

        return registration;
    }
}
