package com.lookstarry.doermail.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.lookstarry.common.constant.BizCodeEnum;
import com.lookstarry.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @PackageName:com.lookstarry.doermall.seckill.config
 * @NAME:SentinelConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/6 14:38
 */
@Configuration
public class SentinelGatewayConfig {
    public SentinelGatewayConfig(){
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler(){
               // 网关限流了请求，就会调用此回调 Mono/Flux Spring5出现的新特性 响应式编程
               @Override
               public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                   R error = R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMessage());
                   String errJson = JSON.toJSONString(error);
                   Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(errJson), String.class);
                   return body;
               }
           }
        );
    }
}
