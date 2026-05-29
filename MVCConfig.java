package com.easy.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//利用Spring配置类的核心能力，扩展SpringMVC的静态资源映射规则，解耦了配置和业务代码，让配置自动生效。
//统一管理 Bean、扩展框架功能、解耦业务与配置
@Configuration
public class MVCConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:D:/image/");
    }
}