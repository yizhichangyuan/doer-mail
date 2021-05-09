package com.lookstarry.doermail.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class DoermailMemberApplicationTests {

    @Test
    void contextLoads() {

    }

    public static void main(String[] args) {
//        String s = DigestUtils.md5Hex("123456");
//        System.out.println(s);

        // 盐值需要在数据库中保存，以便下一次校验
//        String s1 = Md5Crypt.md5Crypt("123456".getBytes(), "$1$qqqqqqq");
//        System.out.println(s1);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);
        boolean matches = passwordEncoder.matches("123456", "$2a$10$5gU.GTurJIp7A0HYRdPN.OrFyAfo4v72YnJZsG3QBB7QbI3mhOHUy");
        System.out.println(matches);
    }


}
