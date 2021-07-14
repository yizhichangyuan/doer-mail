package com.lookstarry.doermail.order.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.lookstarry.common.constant.BizCodeEnum;
import com.lookstarry.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @PackageName:com.lookstarry.doermall.seckill.config
 * @NAME:SentinelConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/6 14:38
 */
@Configuration
public class SentinelConfig {
    public SentinelConfig(){
        WebCallbackManager.setUrlBlockHandler(
                new UrlBlockHandler() {
                    @Override
                    public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
                        R error = R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMessage());

                        // 防止页面中文乱码，设置characterEncoding为UTF-8，格式为JSON
                        httpServletResponse.setCharacterEncoding("UTF-8");
                        httpServletResponse.setContentType("application/json");
                        httpServletResponse.getWriter().write(JSON.toJSONString(error));
                    }
                }
        );
    }
}
