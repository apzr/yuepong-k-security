package com.example.demo.config;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GlobalCorsConfig
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/27 13:50:59
 **/
@Configuration
public class GlobalCorsConfig implements Filter {

      @Override
      public void init(FilterConfig filterConfig) throws ServletException {}

      @Override
      public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                           FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "*");//POST, GET, OPTIONS, DELETE, PUT
        res.setHeader("Access-Control-Max-Age", "1728000");
        res.setHeader("Access-Control-Allow-Headers", "*");//Authorization, Content-Type, Accept, x-requested-with, Cache-Control
        res.setHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(servletRequest, res);
      }

      @Override
      public void destroy() {}

}