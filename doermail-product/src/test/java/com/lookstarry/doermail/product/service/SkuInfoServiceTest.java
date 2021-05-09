package com.lookstarry.doermail.product.service;

import com.lookstarry.doermail.product.vo.SkuItemVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @PackageName:com.lookstarry.doermail.product.service
 * @NAME:SkuInfoServiceTest
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/6 11:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkuInfoServiceTest {
    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Test
    public void testItem() throws ExecutionException, InterruptedException {
        SkuItemVo item = skuInfoService.item(18L);
        System.out.println(item);
    }

    @Test
    public void attrValuesTest(){
        List<SkuItemVo.SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(24L);
        System.out.println(saleAttrsBySpuId);
    }
}
