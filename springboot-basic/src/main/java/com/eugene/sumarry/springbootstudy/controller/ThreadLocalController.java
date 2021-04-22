package com.eugene.sumarry.springbootstudy.controller;

import com.eugene.sumarry.springbootstudy.common.AppContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/thread-local")
public class ThreadLocalController {


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
        return map;
    }
}
