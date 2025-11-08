package com.senjay.archat.ThirdPartyApiTest;

import com.senjay.archat.ArchatApplication;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
//Spring Boot 会从当前测试类 所在的包 一直向上找，寻找带有 @SpringBootApplication 注解的启动类作为上下文入口。
@SpringBootTest(classes = ArchatApplication.class)
class PublicTestControllerTest {

    @BeforeEach
    public void init() {
        System.out.println("先执行我！");
    }

    @Test
    void testMq() {
        System.out.println(1);
    }
    @Test
    void testAiChat() {
        System.out.println(2);
    }




}