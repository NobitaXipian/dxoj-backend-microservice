package com.xipian.dxojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.xipian.dxojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.xipian")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xipian.dxojbackendserviceclient.service"})
public class DxojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DxojBackendUserServiceApplication.class, args);
    }

}
