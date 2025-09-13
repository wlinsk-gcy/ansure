package com.wlinsk.ansure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.wlinsk.ansure.mapper")
public class AnsureApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnsureApplication.class, args);
    }

}
