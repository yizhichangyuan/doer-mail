package com.lookstarry.doermail.product.feign;

import com.lookstarry.common.to.SkuEsModel;
import com.lookstarry.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("doermail-search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    R productStatusUP(@RequestBody List<SkuEsModel> skuEsModels);
}
