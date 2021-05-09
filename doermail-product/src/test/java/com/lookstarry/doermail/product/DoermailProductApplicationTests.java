package com.lookstarry.doermail.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lookstarry.doermail.product.entity.BrandEntity;
import com.lookstarry.doermail.product.service.BrandService;
import com.lookstarry.doermail.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DoermailProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void redisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void testStringRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 保存
        ops.set("hello", "world_" + UUID.randomUUID().toString());
        // 查询
        String hello = ops.get("hello");
        System.out.println("之前保存的数据时：" + hello);

    }

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(6L);
        brandEntity.setDescript("电竞手机");
//        brandEntity.setName("黑鲨");
//        boolean flag = brandService.save(brandEntity);
//        System.out.println(flag);
//        Boolean flag = brandService.updateById(brandEntity);
//        System.out.println(flag);
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().in("brand_id", new Long[]{1L, 2L, 3L}));
        list.forEach((item) -> {
            System.out.println(item);
        });
    }

//    @Test
//    public void testUpload() throws FileNotFoundException {
////        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
////        String endpoint = "oss-cn-beijing.aliyuncs.com";
////        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
////        String accessKeyId = "LTAI5t75KXjVWjGJhxUsBZxv";
////        String accessKeySecret = "N0ZiNdO6GEJQEKxZMmuHwTq2F8Fwg7";
////
////        // 创建OSSClient实例。
////        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
////
//        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        InputStream inputStream = new FileInputStream("/Users/yizhichangyuan/Downloads/60404dae27434.jpg");
//        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
//        ossClient.putObject("doermail-product", "00745YaMgy1gnbcq5f9amj30ta1a8qa1.jpg", inputStream);
//
//        // 关闭OSSClient
//        ossClient.shutdown();
//        System.out.println("上传成功");
//    }

    @Test
    public void testFindPath(){
        Long[] paths = categoryService.findCatelogPath(225L);
        log.info("完整路径为:{}", Arrays.asList(paths));
    }
}
