package com.eugene.sumarry.richclient.service;

import com.eugene.sumarry.richclient.client.domain.CacheUserInfoDO;
import com.eugene.sumarry.richclient.service.biz.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author muyang
 * @create 2024/10/25 10:52
 */
@RestController
@RequestMapping("/user")
public class UserControl {

    @Autowired
    private UserService userService;


    @GetMapping("/list")
    public List<CacheUserInfoDO> list(String key) {
        return userService.list(key);
    }


    @GetMapping("/update")
    public void insertOrUpdate(String key) {
        userService.insertOrUpdate(key);
    }


}
