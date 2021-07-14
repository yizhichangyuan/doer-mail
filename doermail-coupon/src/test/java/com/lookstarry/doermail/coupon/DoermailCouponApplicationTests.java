package com.lookstarry.doermail.coupon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DoermailCouponApplicationTests {

    @Test
    public void contextLoads() {
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(1);
        LocalDate localDate2 = now.plusDays(2);

        System.out.println(localDate);

        LocalTime min = LocalTime.MIN;
        LocalTime max = LocalTime.MAX;

        LocalDateTime start = LocalDateTime.of(now, min);
        LocalDateTime end = LocalDateTime.of(now, max);
        System.out.println(start);
        System.out.println(end);

        String format = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(format);
    }

}
