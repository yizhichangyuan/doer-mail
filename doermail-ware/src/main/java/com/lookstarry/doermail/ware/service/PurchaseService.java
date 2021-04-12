package com.lookstarry.doermail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.doermail.ware.entity.PurchaseEntity;
import com.lookstarry.doermail.ware.vo.MergeVo;
import com.lookstarry.doermail.ware.vo.PurchaseDoneItemVo;
import com.lookstarry.doermail.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 14:03:12
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> map);

    void mergeItems(MergeVo mergeVo);

    void receiveBatchPurchase(List<Long> purchaseIds);

    void done(PurchaseDoneVo purchaseDoneVo);

}

