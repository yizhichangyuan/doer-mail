package com.lookstarry.doermail.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1、使用Mybatis-Plus逻辑删除（就是让是否其在前端页面展示，不展示就是逻辑删除）
 * 1) 配置全局的逻辑删除规则（可删略）
 * 2) 配置逻辑删除的组件Bean（高版本可省略）
 * 3) 在逻辑删除字段加上@TableLogic注解（如果有不同于全局删除字段，还可以通过value和delval来指定自己的删除字段什么时候删除)
 *
 * 2、JSR303（java规范提案：303提案）
 * 前端发送的数据到后台需要校验后入库，防小人
 * 1)、给Bean需要校验字段添加校验注解 可以在javax.validation.constraints，并定义message错误提示
 * 2)、在Controller方法体中对相应的Bean添加上@Valid，否则光在字段标注了注解是没有用的：public R save(@Valid @RequestBody BrandEntity brand)
 * 效果：校验错误以后会有默认的响应，并显示错误信息message
 * 3)、在方法体校验参数后面紧跟BindingResult，就可以获取到校验的结果
 *
 * 3、统一异常处理
 *  @ControllerAdvice
 *  1）编写异常处理类，使用@ControllerAdvice，basePackage表明需要处理哪些controller的异常
 *  2）使用@ExceptionHandler标注方法专门处理的异常
 *  3）分组校验
 *      1) 给校验注解添加上groups属性表明什么情况才需要使用该校验注解
 *      2）Controller参数校验从@Valid改为@Validated表明该方法使用哪种校验
 *      3) @Validated在没有指定groups分组的情况下，那些校验注解没有groups属性的将会被校验，而那些有groups属性的校验注解则不会被校验
 *  4）自定义校验
 *     1）编写自定义校验注解@interface
 *     2）编写自定义校验器ListValueConstraintValue
 *     3）校验注解关联到自定义校验器validatedBy={ListValueConstraintValue.class}
 *     @Documented
 *     @Constraint(validatedBy = {ListValueConstraintValidator.class【可以指定多个不同的校验器，来适应不同类型的校验】})
 *     @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
 *     @Retention(RetentionPolicy.RUNTIME)
 *
 * 4、模板引擎
 *  1）引入依赖：thymeleaf-starter，关闭缓存
 *  2）静态资源（js/css/img）都放在static文件夹下，就可以按照路径直接访问（路径不需要添加static）
 *  3）html页面都放在templates下，直接访问
 *      原因在于：访问项目的时候，默认会找index
 *
 * 5、整合redisson作为分布式锁等功能框架
 *  1）引入依赖
 *          <dependency>
 *             <groupId>org.redisson</groupId>
 *             <artifactId>redisson</artifactId>
 *             <version>3.15.3</version>
 *         </dependency>
 *  2）配置redisson
 */
@EnableFeignClients(basePackages = "com.lookstarry.doermail.product.feign")
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.lookstarry.doermail.product.dao")
public class DoermailProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoermailProductApplication.class, args);
    }

}
