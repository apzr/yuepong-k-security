package com.example.demo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 *
 * @author qozi
 * @since 2020/8/28 9:36
 **/
@Configuration
public class CorsConfiguration {

    /**
     * 跨域支持
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        // #允许向该服务器提交请求的URI，*表示全部允许
        config.addAllowedOrigin(org.springframework.web.cors.CorsConfiguration.ALL);
        // 允许cookies跨域
        config.setAllowCredentials(true);
        // #允许访问的头信息,*表示全部
        config.addAllowedHeader(org.springframework.web.cors.CorsConfiguration.ALL);
        // 允许提交请求的方法，*表示全部允许
        config.addAllowedMethod(org.springframework.web.cors.CorsConfiguration.ALL);
        source.registerCorsConfiguration("/**", config);
        registration.setFilter(new CorsFilter(source));
        registration.addUrlPatterns("/*");
        registration.setName("corsFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

}
