package com.lookstarry.doermail.search.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("doermail-product")
public interface ProductFeignService {

}
