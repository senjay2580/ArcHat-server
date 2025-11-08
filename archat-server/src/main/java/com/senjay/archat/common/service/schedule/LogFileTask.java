package com.senjay.archat.common.service.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
// TODO 定时备份日志文件
public class LogFileTask {
//    @Scheduled(fixedRate = 5000)
    public void logFileTask() {
//        System.out.println("hello" + " " + LocalDateTime.now());
    }
}
