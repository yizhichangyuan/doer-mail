package com.lookstarry.doermail.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.lookstarry.common.constant.BizCodeEnum;
import com.lookstarry.common.exception.NoStockException;
import com.lookstarry.common.to.SkuHasStockVo;
import com.lookstarry.doermail.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lookstarry.doermail.ware.entity.WareSkuEntity;
import com.lookstarry.doermail.ware.service.WareSkuService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.R;


/**
 * 商品库存
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 14:03:12
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo){
        try {
            Boolean locKSuccess = wareSkuService.orderLockStock(vo);
            return R.ok();
        }catch(NoStockException e){
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), e.getMessage());
        }
    }

    @PostMapping("/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockVo> skuHasStockVos = wareSkuService.hasStock(skuIds);
        return R.ok().setData(skuHasStockVos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
