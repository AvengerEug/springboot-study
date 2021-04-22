package com.eugene.sumarry.clickhouse.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "clickHouse.jdbc.datasource")
public class ClickHouseDataSourceConfig {

    private String username;

    private String driverName;

    public void setUsername(String username) {
        this.username = username;
    }

    private String password;

    private String url;

    private String type;

    private long maxWait;

    private String validationQuery;

    @Bean
    public DataSource clickhouseDataSource() throws Exception {
        Class classes = Class.forName(type);
        DruidDataSource dataSource = (DruidDataSource) DataSourceBuilder
                .create()
                .driverClassName(driverName)
                .type(classes)
                .url(url)
                .username(username)
                .password(password)
                .build();
        dataSource.setMaxWait(maxWait);
        dataSource.setValidationQuery(validationQuery);
        return dataSource;
    }

    @Bean
    public SqlSessionFactory clickHouseSqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(clickhouseDataSource());
        // 实体 model的 路径 比如 com.order.model
        factory.setTypeAliasesPackage("com.eugene.sumarry.clickhouse.model");
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factory.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
        //开启驼峰命名转换
        factory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return factory.getObject();
    }


}
