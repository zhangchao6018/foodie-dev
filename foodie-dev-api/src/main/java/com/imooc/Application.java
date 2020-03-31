package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Description:
 * @Author: zhangchao
 * @Date: 2020-02-03 23:45
 **/

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})  // 排除权限自动装配
@MapperScan(basePackages = "com.imooc.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.imooc", "org.n3r.idworker"})
@EnableRedisHttpSession  //redis 作为spring session
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
