package com.calmkin.filter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")          //添加注解  方便boot扫描到,设置拦截器名称和拦截路径
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;   //强转是为了能够获取访问的uri
        System.out.println(request.getRequestURI());
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
