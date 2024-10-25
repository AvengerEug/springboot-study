package com.eugene.sumarry.richclient.client.biz.impl;

import com.eugene.sumarry.richclient.client.biz.UserServiceRPC;
import com.eugene.sumarry.richclient.client.domain.CacheUserInfoDO;
import com.eugene.sumarry.richclient.client.rpc.anno.RichClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 *  此类假设为dubbo服务引入生成的服务
 *  内部自己实现一个具有rpc能力的功能
 * @author muyang
 * @create 2024/10/25 16:41
 */
@Service
public class UserServiceRPCImpl implements UserServiceRPC {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Autowired
    private RestTemplate restTemplate;

    @RichClient(cacheKey = "user_${0}")
    @Override
    public List<CacheUserInfoDO> list(String key) throws URISyntaxException {

        String url = "http://localhost:8080/user/list?key=123";
        // 使用占位符 {} 和参数map来构建URL
        return restTemplate.getForObject(new URI(url), List.class);
    }
}
