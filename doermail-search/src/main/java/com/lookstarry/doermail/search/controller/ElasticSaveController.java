package com.lookstarry.doermail.search.controller;

import com.lookstarry.common.constant.BizCodeEnum;
import com.lookstarry.common.to.SkuEsModel;
import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.search.entity.SkuEsSaveModel;
import com.lookstarry.doermail.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @PackageName:com.lookstarry.doermail.search.controller
 * @NAME:ElasticSaveController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/16 23:18
 */
@Slf4j
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;

    // 上架商品
    @PostMapping("/product")
    public R productStatusUP(@RequestBody List<SkuEsModel> skuEsModels){
        List<SkuEsSaveModel> skuEsSaveModels = skuEsModels.stream().map((skuEsModel -> {
            SkuEsSaveModel skuEsSaveModel = new SkuEsSaveModel();
            BeanUtils.copyProperties(skuEsModel, skuEsSaveModel);
            return skuEsSaveModel;
        })).collect(Collectors.toList());
        boolean b = false;

        try{
            b = productSaveService.productStatusUP(skuEsSaveModels);
        }catch(Exception e){
            log.error("ElasticSaveController商品上架发生错误，{}", e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }

        if(!b){
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }else{
            return R.ok();
        }
    }
}
