package com.imooc.config;

import com.imooc.interceptor.UserTokenInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 实现静态资源的映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")  // 映射swagger2
                .addResourceLocations("file:/Users/zhangchao/develop/files/");  // 映射本地静态资源
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
          registry.addInterceptor(userTokenInterceptor())
                    .addPathPatterns("/hello")
                    .addPathPatterns("/shopcart/*")
                    .addPathPatterns("/address/*")
                    .addPathPatterns("/orders/*")
                    .addPathPatterns("/center/*")
                    .addPathPatterns("/userInfo/*")
                    .addPathPatterns("/myorders/*")
                    .addPathPatterns("/mycomments/*")
                    .excludePathPatterns("/myorders/deliver")
                    .excludePathPatterns("/orders//notifyMerchantOrderPaid")

          ;
          WebMvcConfigurer.super.addInterceptors(registry);
    }
}
