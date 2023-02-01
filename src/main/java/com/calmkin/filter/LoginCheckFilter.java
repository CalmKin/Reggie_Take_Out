package com.calmkin.filter;


import com.alibaba.fastjson.JSON;
import com.calmkin.common.BaseContext;
import com.calmkin.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")          //添加注解  方便boot扫描到,设置拦截器名称和拦截路径
public class LoginCheckFilter implements Filter {
    //用于路径比较的工具,因为我们是用通配符来进行路径比较，而不是直接进行字符串比较
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

//        long id = Thread.currentThread().getId();
//        log.info("当前线程ID为{}",id);


        HttpServletRequest request = (HttpServletRequest) servletRequest;   //强转是为了能够获取访问的uri
        HttpServletResponse response = (HttpServletResponse) servletResponse;
       // 过滤器具体的处理逻辑如下：
       // 1、获取本次请求的URI
        String requestURI = request.getRequestURI();
       // 2、判断本次请求是否需要处理,定义哪些不需要拦截的请求
        //首先登录和登出的页面不需要拦截
        // 然后就是后端的页面，我们不用拦截，因为用户看到后端页面其实无所谓，关键是不能看到页面上的数据，这些数据是通过ajax发送请求之后从响应中取出来的
        //所以我们只拦截针对controller请求，不拦截页面请求

        String [] unnecessary = new String[]{
            "/employee/login",      //注意这里不能写死成login.html，因为不止需要放行login.html文件，还有login.html里面引用的所有文件
             "/employee/logout",
            "/backend/**",
            "/front/**"
        };

       // 3、如果不需要处理，则直接放行
        if(check(unnecessary,requestURI))
        {
            filterChain.doFilter(servletRequest,servletResponse);
            //打印日志
//            System.out.println("不需要处理，则直接放行");

            return;
        }

       // 4、判断登录状态，如果已登录，则直接放行,是否登录，直接在请求中查看是否存在响应的session即可
        if(request.getSession().getAttribute("employee")!=null)
        {
            BaseContext.setID((Long) request.getSession().getAttribute("employee"));

            filterChain.doFilter(servletRequest,servletResponse);
            //打印日志
//            System.out.println("已登录,直接放行===>"+request.getSession().getAttribute("employee"));
            return;
        }


//        System.out.println("用户未登录");

       // 5、如果未登录则返回未登录结果,因为这里是过滤器，返回值不是R类型，所以必须要用输出流的方式向前端返回响应的结果,之前原生servlet进行响应的时候也是直接用输出流的
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 根据比较请求的uri和数组中的字符串进行匹配，如果请求的uri在数组里面，表明不需要拦截
     * @param strings
     * @param request
     * @return
     */
    public boolean check(String[] strings,String request)
    {
        for (String string : strings) {
            if( PATH_MATCHER.match(string,request) )    //注意这里不能直接进行字符串比较
            {
                return true;
            }
        }
        return false;
    }
}
