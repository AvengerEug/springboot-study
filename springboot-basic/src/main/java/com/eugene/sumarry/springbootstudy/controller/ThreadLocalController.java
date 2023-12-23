package com.eugene.sumarry.springbootstudy.controller;

import com.alibaba.fastjson.JSONObject;
import com.eugene.sumarry.springbootstudy.common.AppContext;
import com.eugene.sumarry.springbootstudy.runafterstarted.MyCommandLinerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/thread-local")
public class ThreadLocalController {

    private static final Logger LOGGER = LoggerFactory.getLogger("bizInfo");



    /**
     * 从threadLocal获取传递的index参数
     * @return
     */
    @GetMapping("/index")
    public Map<String, Object> index() {
        Map<String, Object> map = new HashMap<>();


        HttpServletRequest request = (HttpServletRequest) AppContext.getAttribute("request");

        map.put("index", request.getParameter("index"));

        return map;
    }

    @GetMapping("/hello")
    public Map<String, Boolean> hello() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("1", true);
        map.put("2", false);

        int x = 2;
        Random random = new Random();
        int randomValue = random.nextInt(10);
        // 如果随机生成的数是偶数，则模拟报错
        if (randomValue / x == 0) {
            LOGGER.warn("thread-local/hello|15|参数错误|");
            throw new IllegalArgumentException("参数错误");
        }

        LOGGER.warn("thread-local/hello|0||{}", JSONObject.toJSONString(map));
        return map;
    }
}
