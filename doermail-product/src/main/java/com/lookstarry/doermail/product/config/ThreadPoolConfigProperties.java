package com.lookstarry.doermail.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @PackageName:com.lookstarry.doermail.product.config
 * @NAME:ThreadPoolConfiguraion
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/6 20:18
 */
@ConfigurationProperties(prefix = "doermall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer keepAliveTime;
}
