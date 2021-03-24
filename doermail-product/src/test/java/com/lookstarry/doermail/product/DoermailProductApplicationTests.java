package com.lookstarry.doermail.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lookstarry.doermail.product.entity.BrandEntity;
import com.lookstarry.doermail.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class DoermailProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
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

}
