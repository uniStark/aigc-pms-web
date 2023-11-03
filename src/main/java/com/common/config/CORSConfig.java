package com.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("http://localhost:3000") // 允许的源
//                .allowedMethods("POST", "GET")
                .allowedMethods("*")
                .allowedHeaders("Content-Type") // 允许的请求头
                .allowCredentials(true) // 允许发送身份验证信息
                .maxAge(3600); // 预检请求的有效期，以秒为单位
    }


}
