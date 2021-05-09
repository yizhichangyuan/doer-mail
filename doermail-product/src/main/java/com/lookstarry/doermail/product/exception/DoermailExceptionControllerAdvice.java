package com.lookstarry.doermail.product.exception;

import com.lookstarry.common.constant.BizCodeEnum;
import com.lookstarry.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @PackageName:com.lookstarry.doermail.product.exception
 * @NAME:DoermailExceptionController
 * @Description: 集中处理controller的所有异常，包括数据校验抛出的
 * @author: yizhichangyuan
 * @date:2021/3/28 12:49
 */
@Slf4j // lomback提供
//@ControllerAdvice(basePackages = "com.lookstarry.doermail.product.controller") //basePackages表明处理哪些controller的异常
@RestControllerAdvice(basePackages = "com.lookstarry.doermail.product.controller")
public class DoermailExceptionControllerAdvice {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题：{}，异常类型：{}", e.getMessage(), e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> map = new HashMap<>();
        bindingResult.getFieldErrors().forEach(item -> {
            map.put(item.getField(), item.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMessage()).put("data", map);
    }

    // 如果异常没有被其他方法捕捉到，则进入该方法，Throwable是所有Exception和Error的基类
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMessage()).put("data", throwable.getMessage());
    }
}
