package com.eugene.sumarry.springbootstudy.plugin;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResponseBodyWrapFactoryBean implements InitializingBean {

    @Resource
    private RequestMappingHandlerAdapter adapter;

    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = adapter.getReturnValueHandlers();
        if (returnValueHandlers == null) {
            returnValueHandlers = new ArrayList<>(0);
        }

        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>(returnValueHandlers);
        decorateHandlers(handlers);
        adapter.setReturnValueHandlers(handlers);
    }


    private void decorateHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler instanceof RequestResponseBodyMethodProcessor) {
                handlers.set(handlers.indexOf(handler), new ResponseBodyWrapHandler(handler));
                break;
            }
        }
    }

    private static class ResponseBodyWrapHandler implements HandlerMethodReturnValueHandler {

        private final HandlerMethodReturnValueHandler delegate;

        ResponseBodyWrapHandler(HandlerMethodReturnValueHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return delegate.supportsReturnType(returnType);
        }

        @Override
        public void handleReturnValue(Object returnValue,
                                      MethodParameter returnType,
                                      ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest) throws Exception {

            Map map = new HashMap<>();
            map.put("path", "/test");
            map.put("data", returnValue);
            map.put("code", 200);
            delegate.handleReturnValue(map, returnType, mavContainer, webRequest);
        }
    }
}