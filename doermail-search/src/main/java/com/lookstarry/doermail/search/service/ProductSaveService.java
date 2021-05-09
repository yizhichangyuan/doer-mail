package com.lookstarry.doermail.search.service;

import com.lookstarry.doermail.search.entity.SkuEsSaveModel;

import java.util.List;

public interface ProductSaveService {
    Boolean productStatusUP(List<SkuEsSaveModel> skuEsModels);
}
