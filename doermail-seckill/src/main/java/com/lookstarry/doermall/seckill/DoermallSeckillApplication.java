package com.lookstarry.doermall.seckill;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

//@EnableRabbit //只发送不监听消息不需要该注解
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DoermallSeckillApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoermallSeckillApplication.class, args);
	}

}
