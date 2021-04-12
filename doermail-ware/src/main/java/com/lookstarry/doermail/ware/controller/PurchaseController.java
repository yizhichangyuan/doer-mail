package com.lookstarry.doermail.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.lookstarry.doermail.ware.service.PurchaseDetailService;
import com.lookstarry.doermail.ware.vo.MergeVo;
import com.lookstarry.doermail.ware.vo.PurchaseDoneItemVo;
import com.lookstarry.doermail.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.lookstarry.doermail.ware.entity.PurchaseEntity;
import com.lookstarry.doermail.ware.service.PurchaseService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.R;


/**
 * 采购信息
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 14:03:12
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/done")
    public R finishPurchaseItems(@RequestBody @Validated PurchaseDoneVo purchaseDoneVo){
        purchaseService.done(purchaseDoneVo);
        return R.ok();
    }

    @PostMapping("/received")
    public R doReceived(@RequestBody List<Long> purchaseIds){
        purchaseService.receiveBatchPurchase(purchaseIds);
        return R.ok();
    }

    /**
     * 合并采购需求项到采购单中
     * 如果指定了purchaseId，则合并到该采购单中，前提是该采购单状态为新建或者已分配才可以合并
     * 如果未指定则新建采购单，然后合并到该采购单中
     */
    @PostMapping("/merge")
    public R mergePurchase(@RequestBody MergeVo mergeVo){
        purchaseService.mergeItems(mergeVo);
        return R.ok();
    }

    /**
     * 查询所有采购单状态为新建或者已分配的采购单，这样才可以将新的采购需求添加上去
     * 采购单已领取：可能采购人员已经出发，所以不能再往这里面添加
     */
    @GetMapping("/unreceive/list")
    public R listUnreceiveSheet(@RequestParam Map<String, Object> map){
        PageUtils page = purchaseService.queryPageUnreceivePurchase(map);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchase.setUpdateTime(new Date());
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
