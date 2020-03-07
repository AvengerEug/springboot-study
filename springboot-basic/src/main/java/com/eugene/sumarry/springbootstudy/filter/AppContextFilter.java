package com.eugene.sumarry.springbootstudy.filter;

import com.eugene.sumarry.springbootstudy.common.AppContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

public class AppContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        AppContext.initAppContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AppContext.setAttribute("request", request);
        AppContext.setAttribute("response", request);

        try {
            chain.doFilter(request, response);
        } finally {
            AppContext.clearAppContext();
        }
    }

    @Override
    public void destroy() {

    }
}
