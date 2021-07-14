package com.lookstarry.doermail.order.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @PackageName:com.lookstarry.doermall.authserver.config
 * @NAME:DoermallSessionConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/13 15:54
 */
@Configuration
public class DoermallSessionConfig {
    static{
        ParserConfig.getGlobalInstance().addAccept("com.lookstarry.doermail.");
    }

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setCookieName("DOERMALLSESSION");
        defaultCookieSerializer.setDomainName("doermall.com");
        return defaultCookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }
}
