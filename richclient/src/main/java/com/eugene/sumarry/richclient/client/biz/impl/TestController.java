package com.eugene.sumarry.richclient.client.biz.impl;

import com.eugene.sumarry.richclient.client.biz.UserServiceRPC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

/**
 * @author muyang
 * @create 2024/10/25 16:49
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    UserServiceRPC userServiceRPC;

    @RequestMapping("/user/list")
    public Object userList() throws URISyntaxException {
        return userServiceRPC.list("123");
    }

}
