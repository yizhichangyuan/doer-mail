package com.lookstarry.doermail.search.service.impl;

import com.lookstarry.doermail.search.dao.SkuEsSaveModelRepository;
import com.lookstarry.doermail.search.entity.SkuEsSaveModel;
import com.lookstarry.doermail.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.search.service.impl
 * @NAME:ProductSaveServiceImpl
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/16 23:26
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private SkuEsSaveModelRepository skuEsSaveModelRepository;

    @Override
    public Boolean productStatusUP(List<SkuEsSaveModel> skuEsModels) {
        try{
            skuEsSaveModelRepository.saveAll(skuEsModels);
            log.info("ES上架成功");
            return true;
        }catch(Exception e){
            log.error("ES保存错误，错误原因：{}", e.getMessage());
            throw e;
        }
    }
}
