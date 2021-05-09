package com.lookstarry.doermall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.lookstarry.common.constant.AuthServerConstant;
import com.lookstarry.common.constant.BizCodeEnum;
import com.lookstarry.common.utils.R;
import com.lookstarry.doermall.authserver.feign.AuthFeignService;
import com.lookstarry.doermall.authserver.feign.MemberFeignService;
import com.lookstarry.doermall.authserver.vo.UserLoginVo;
import com.lookstarry.doermall.authserver.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @PackageName:com.lookstarry.doermall.authserver.controller
 * @NAME:AuthController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/7 08:52
 */
@Controller
public class AuthController {
    @Autowired
    private AuthFeignService authFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    private static final Long allowAcquireSmsInterval = 60 * 1000L; // 验证码请求间隔为60秒

    /**
     * 发送一个请求直接跳转到一个页面。
     * SpringMVC viewcontroller: 将请求和页面映射过来
     *
     * @return
     */
    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone){
        // TODO 接口防大并发刷
        if(!in60Seconds(phone)){
            return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
        }

        String code = UUID.randomUUID().toString().substring(0,5); // 随机生成验证码

        // 2、验证码的再次校验问题可以使用redis存储验证码，存key为手机号，value为验证码 + 请求时时间（请求时间是为了防止60s内重复请求验证码）
        String redisKey = AuthServerConstant.PHONE_CODE_PREFIX + phone;
        String redisCode = code + "_" + System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(redisKey, redisCode, 10, TimeUnit.MINUTES); // 设置过期时间为10分钟
        authFeignService.sendSMS(phone, code);
        return R.ok();
    }

    /**
     *  redis缓存验证码，防止同一个phone在60秒内再次发送验证码，
     *  可以使用在key上记录存入时间，再次请求时查看相差时间是否超过60s
     */
    private boolean in60Seconds(String phone){
        String redisKey = AuthServerConstant.PHONE_CODE_PREFIX + phone;
        String code = stringRedisTemplate.opsForValue().get(redisKey);
        // 如果为空，说明用户还未请求过验证码，可以接着请求放行
        if(StringUtils.isEmpty(code)){
            return true;
        }else{
            // 如果验证码存在，查看请求时时间距离当前的时间是否超过60秒
            String lastRequestTime = code.split("_")[1];
            // 小于60秒间隔，不可再请求验证码
            if(System.currentTimeMillis() - Long.parseLong(lastRequestTime) <= allowAcquireSmsInterval){
                return false;
            }
        }
        return true;
    }

    /** TODO Session多台服务器不可共享的问题，重定向携带数据，
     * 是利用session原理，将数据放在session中，只要调到
     * 下一个页面取出这个数据之后，session里面的数据就会删掉
     * RedirectAttributes模拟重定向携带数据
     * @param registerVo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/regist")
    public String register(@Valid UserRegisterVo registerVo, BindingResult result,
                           RedirectAttributes redirectAttributes, HttpSession httpSession){
        if(result.hasErrors()){
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            // Request method 'POST' not supported
            // 用户注册 -> regist[post] -> 转发/register.html（原封不动转发，包括转发方式，路径映射都是get方式访问）-> 出现了post方式发送了请求给register.html -> 报错
            // 校验错误，转发到注册页面
            return "redirect:http://auth.doermall.com/register.html";
        }
        // 数据校验格式通过，校验验证码是否正确
        String code = registerVo.getCode();
        String redisKey = AuthServerConstant.PHONE_CODE_PREFIX + registerVo.getPhone();
        String redisCode = stringRedisTemplate.opsForValue().get(redisKey); // redis中存的数据可能因为超过了10分钟为空，就失效了
        String rightCode = redisCode.split("_")[0];
        if(StringUtils.isNotEmpty(redisCode) && rightCode.equals(code)){
            stringRedisTemplate.delete(redisKey); // 验证通过删除redis中该验证码，令牌机制
            // 验证码验证通过，将用户注册数据发送给member服务，member服务会首先检测该手机号是否注册过
            R r = memberFeignService.memberRegist(registerVo);
            if(r.getCode() == 0){
                // 注册成功
                return "redirect:http://auth.doermall.com/login.html";
            }else{
                String msg = r.getData("msg", new TypeReference<String>() {});
                Map<String, String> errors = new HashMap();
                errors.put("msg", msg);
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.doermall.com/register.html";
            }
        }
        // 验证不通过
        Map<String, String> errors = new HashMap();
        errors.put("code", "验证码校验不通过");
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.doermall.com/register.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo){
        // 远程登陆接口
        return "redirect:http://doermall.com";
    }

}
