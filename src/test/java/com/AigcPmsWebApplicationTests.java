package com;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class AigcPmsWebApplicationTests {

    @Test
    void contextLoads() {
        // 格式化时间为指定格式
        String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH点mm"));
        System.out.println(formattedTime);
    }

}
