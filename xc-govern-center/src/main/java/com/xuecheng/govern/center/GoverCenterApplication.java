package com.xuecheng.govern.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/13 13:52
 */
@EnableEurekaServer//标识是一个Eureka
@SpringBootApplication
public class GoverCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoverCenterApplication.class,args);
    }
}