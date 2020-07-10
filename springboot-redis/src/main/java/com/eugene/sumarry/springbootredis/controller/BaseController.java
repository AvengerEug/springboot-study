package com.eugene.sumarry.springbootredis.controller;

import com.eugene.sumarry.springbootredis.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    private static final String DEFAULT_ERROR_MESSAGE = "系统异常";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Message exceptionHandler(Exception e) {
        String errorMessage = DEFAULT_ERROR_MESSAGE;
        logger.error("系统异常", e);
        return Message.error(errorMessage);
    }
}
