package com.senjay.archat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@MapperScan({"com.senjay.archat.common.**.mapper"})
public class ArchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchatApplication.class, args);
    }

}
