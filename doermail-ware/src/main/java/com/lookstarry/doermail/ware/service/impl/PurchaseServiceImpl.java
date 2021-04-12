package com.lookstarry.doermail.ware.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lookstarry.common.utils.Constant;
import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.ware.entity.PurchaseDetailEntity;
import com.lookstarry.doermail.ware.entity.WareSkuEntity;
import com.lookstarry.doermail.ware.feign.ProductFeignService;
import com.lookstarry.doermail.ware.service.PurchaseDetailService;
import com.lookstarry.doermail.ware.service.WareSkuService;
import com.lookstarry.doermail.ware.vo.MergeVo;
import com.lookstarry.doermail.ware.vo.PurchaseDoneItemVo;
import com.lookstarry.doermail.ware.vo.PurchaseDoneVo;
import com.lookstarry.doermail.ware.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;

import com.lookstarry.doermail.ware.dao.PurchaseDao;
import com.lookstarry.doermail.ware.entity.PurchaseEntity;
import com.lookstarry.doermail.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        int newStatus = Constant.PurchaseSheetStatus.NEW.getValue(); // 新建的采购单
        int distributeStatus = Constant.PurchaseSheetStatus.HAVE_DISTRIBUTE.getValue(); // 已分配的采购单
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<PurchaseEntity>();
        queryWrapper.eq("status", newStatus).or().eq("status", distributeStatus);
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergeItems(MergeVo mergeVo) {
        // 1.首先检查该采购单是否存在以及采购单状态
        List<PurchaseDetailEntity> purchaseDetailList= purchaseDetailService.listByIds(mergeVo.getItems());
        if(purchaseDetailList != null || purchaseDetailList.size() != 0){
            // 2.检查采购需求项的状态，是否已经是为新建的需求，将需求项过滤出要新建或已分配的采购需求（已分配的采购需求可以更新分配到其他采购订单中）
            List<PurchaseDetailEntity> filterDetails = purchaseDetailList.stream().filter((deteil) ->{
                return deteil.getStatus().equals(Constant.PurchaseSheetStatus.NEW.getValue()) ||
                        deteil.getStatus().equals(Constant.PurchaseSheetStatus.HAVE_DISTRIBUTE.getValue());
            }).collect(Collectors.toList());
            if(filterDetails.size() != 0){
                Long purchaseId = mergeVo.getPurchaseId();
                if(mergeVo != null && purchaseId != null){
                    PurchaseEntity purchaseSheet = this.getById(purchaseId);
                    // 采购单存在且采购单状态为新建或者已分配，则直接合并到该采购单中
                    if(purchaseSheet != null &&
                            (purchaseSheet.getStatus().equals(Constant.PurchaseSheetStatus.NEW.getValue())
                                    || purchaseSheet.getStatus().equals(Constant.PurchaseSheetStatus.HAVE_DISTRIBUTE.getValue()))){
                        // 采购需求状态更新为采购表的状态一致
                        List<PurchaseDetailEntity> saveDetails = getPurchaseDetails(filterDetails, purchaseId, purchaseSheet.getStatus());
                        purchaseDetailService.updateBatchById(saveDetails);
                        // 更新采购单时间
                        purchaseSheet.setUpdateTime(new Date());
                        this.updateById(purchaseSheet);
                    }
                }else if(mergeVo != null && purchaseId == null){
                    // 如果采购单不存在，则新建采购单
                    PurchaseEntity purchaseEntity = new PurchaseEntity();
                    purchaseEntity.setCreateTime(new Date());
                    purchaseEntity.setUpdateTime(new Date());
                    purchaseEntity.setStatus(Constant.PurchaseSheetStatus.NEW.getValue());
                    this.save(purchaseEntity);

                    // 更新采购详情，采购需求的状态更新为新建状态
                    List<PurchaseDetailEntity> saveDetails = getPurchaseDetails(filterDetails, purchaseEntity.getId(), purchaseEntity.getStatus());
                    purchaseDetailService.updateBatchById(saveDetails);
                }
            }
        }

    }

    @Transactional
    @Override
    public void receiveBatchPurchase(List<Long> purchaseIds) {
        // 1.首先确保采购单的状态时已分配的采购单且采购单上指定的人是自己
        // todo 确保采购单上的分配的人是自己才可以领取，需要完成登录和权限验证
        if(purchaseIds != null && purchaseIds.size() != 0){
            List<PurchaseEntity> purchaseEntities = this.listByIds(purchaseIds);
            List<PurchaseEntity> filterPurchases = purchaseEntities.stream().filter((item) ->{
                return item.getStatus().equals(Constant.PurchaseSheetStatus.HAVE_DISTRIBUTE.getValue());
            }).map((item) -> {
                item.setUpdateTime(new Date());
                item.setStatus(Constant.PurchaseSheetStatus.PURCHASING.getValue());
                return item;
            }).collect(Collectors.toList());


            if(filterPurchases != null && filterPurchases.size() != 0){
                // 2.更新采购单状态
                this.updateBatchById(filterPurchases);
                // 3.更新采购单需求项状态
                List<Long> filterIds = filterPurchases.stream().map((item) -> item.getId()).collect(Collectors.toList());

                List<PurchaseDetailEntity> detailEntity = purchaseDetailService.
                        list(new QueryWrapper<PurchaseDetailEntity>().in("purchase_id", filterIds));
                List<PurchaseDetailEntity> detailEntities = detailEntity.stream().map((detail) ->{
                    detail.setStatus(Constant.PurchaseSheetStatus.PURCHASING.getValue());
                    return detail;
                }).collect(Collectors.toList());
                purchaseDetailService.updateBatchById(detailEntities);
            }
        }
    }

    /**
     * 更新采购单中采购项的状态
     * @param purchaseDoneVo
     */
    @Transactional
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        // 1.首先检查采购单id是否存在
        Long purchaseId = purchaseDoneVo.getId();
        PurchaseEntity purchaseEntity = this.getById(purchaseId);

        if(purchaseEntity != null && purchaseEntity.getId() != null){
            // 2.更新采购项状态 wms_purchase_detail
            // 2.1) 检查采购项是否属于采购单，查询该采购单的所有采购项
            List<PurchaseDetailEntity> allDetailsInPurchase = purchaseDetailService.list(
                    new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseId));
            // 建立key-value映射：主键-对象本身，用来检查采购项是否存在以及更新操作
            Map<Long, PurchaseDetailEntity> purchaseDetailsIdToDetailMap= allDetailsInPurchase.stream().
                    collect(Collectors.toMap(PurchaseDetailEntity::getId, item->item)); //第二个参数是一个函数，输入为对象本身，输出为对象本身，这里简写
            Set<Long> purchaseAllIds = purchaseDetailsIdToDetailMap.keySet();

            // 2.2) 过滤出属于该采购单中的采购项并更新其状态
            List<PurchaseDoneItemVo> items = purchaseDoneVo.getItems();
            // detailEntities为拿到全部信息改变状态后的采购项
            List<PurchaseDetailEntity> detailEntities = items.stream().filter((item) ->{
                return purchaseAllIds.contains(item.getItemId()); }
            ).map((item) ->{
                PurchaseDetailEntity tempDetails = purchaseDetailsIdToDetailMap.get(item.getItemId());
                tempDetails.setStatus(item.getStatus());
                tempDetails.setReason(item.getReason());
                return tempDetails;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);

            // 3.更新采购单状态：wms_purchase :
            // 【采购单状态更新为完成：采购项全部完成，采购单状态更新为异常：采购项中存在着失败的采购项】
            confirmPurchaseSheetStatus(purchaseEntity, allDetailsInPurchase);
            this.updateById(purchaseEntity);

            // 3.将成功的采购项进行入库 wms_ware_sku：更新或者插入操作。将成功的采购项化成两堆：一堆是商品库存中已有记录的商品则更新数量，一堆为没有记录的商品则插入
            // 3.1 查询库存中已有记录的商品，将成功的采购项按照仓库id划分，方便查找对应仓库中skuId是否已有记录
            // 3.1.1 将所有成功的采购项的仓库id和采购商品id分离出来
            List<PurchaseDetailEntity> doneDetails = detailEntities.stream().filter((item) ->{
                return item.getStatus().equals(Constant.PurchaseSheetStatus.DONE.getValue());
            }).collect(Collectors.toList());
            Set<Long> wareIds = new HashSet<>();
            Set<Long> skuIds = new HashSet<>();
            for(PurchaseDetailEntity doneDetail : doneDetails){
                wareIds.add(doneDetail.getWareId());
                skuIds.add(doneDetail.getSkuId());
            }

            // 查出skuId对应的名称
            // todo 远程查询sku的名字，如果失败，整个事务不回滚，所以这里手动将异常catch住，而不被@Transactional捕获
            // todo 还有什么办法让异常出现以后不回滚呢？见高级篇
            List<SkuInfoVo> skuInfoList = null;
            try {
                R r = productFeignService.listByBatchId(new ArrayList<>(skuIds));
                if (r.getCode() == 0) {
                    List<SkuInfoVo> temp = (List<SkuInfoVo>) r.get("data"); // 此时获得的data底层仍然是个List<Map>结构
                    ObjectMapper objectMapper = new ObjectMapper();
                    // 将Map转为SkuInfoVo
                    skuInfoList = objectMapper.convertValue(temp, new TypeReference<List<SkuInfoVo>>(){});
                }
            }catch (Exception e){
                // 异常什么也不处理
            }

            Map<Long, String> skuIdToSkuName = null;
            if(skuInfoList != null && skuInfoList.size() != 0){
                skuIdToSkuName = skuInfoList.stream().collect(Collectors.toMap(SkuInfoVo::getSkuId, SkuInfoVo::getSkuName));
            }

            // 仓库中已有记录的SKU项，查询条件为笛卡尔积，所以后面还需要过滤
            List<WareSkuEntity> wareSkus = null;
            QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<WareSkuEntity>();
            if(wareIds.size() != 0){
                queryWrapper.in("ware_id",wareIds);
            }
            if(skuIds.size() != 0){
                queryWrapper.in("sku_id", skuIds);
            }
            // 防止两者都为空的时候，全表查询
            if(wareIds.size() == 0 && skuIds.size() == 0){
                queryWrapper.last("limit 1");
            }
            // 防止两者都为空的时候，全表查询
            if(wareIds.size() != 0 || skuIds.size() != 0){
                wareSkus = wareSkuService.list(queryWrapper);
            }

            Map<Long, String> finalSkuIdToSkuName = skuIdToSkuName;
            List<WareSkuEntity> finalWareSkus = wareSkus;
            List<WareSkuEntity> wareSkuEntities = doneDetails.stream().map((detail) -> {
                WareSkuEntity wareSkuEntity = purchaseItemInWare(finalWareSkus, detail.getWareId(), detail.getSkuId());
                // 仓库中存在该sku，则有主键就会更新
                if (wareSkuEntity != null) {
                    Integer newStock = wareSkuEntity.getStock() + detail.getSkuNum();
                    wareSkuEntity.setStock(newStock);
                } else {
                    wareSkuEntity = new WareSkuEntity();
                    Long skuId = detail.getSkuId();
                    wareSkuEntity.setSkuId(skuId);
                    wareSkuEntity.setWareId(detail.getWareId());
                    wareSkuEntity.setStock(detail.getSkuNum());
                    wareSkuEntity.setStockLocked(0);
                    if (finalSkuIdToSkuName != null) {
                        wareSkuEntity.setSkuName(finalSkuIdToSkuName.get(skuId));
                    }
                }
                return wareSkuEntity;
            }).collect(Collectors.toList());
            // 有主键则更新，否则插入
            wareSkuService.saveOrUpdateBatch(wareSkuEntities);
        }
    }

    private void confirmPurchaseSheetStatus(PurchaseEntity purchaseEntity, List<PurchaseDetailEntity> allDetailsInPurchase) {
        Boolean haveDone = false;
        Boolean haveError = false;
        int count = 0;
        for(PurchaseDetailEntity detail : allDetailsInPurchase){
            if(detail.getStatus().equals(Constant.PurchaseSheetStatus.FAIL.getValue())){
                haveError = true;
                break;
            }else if(detail.getStatus().equals(Constant.PurchaseSheetStatus.DONE.getValue())){
                count++;
            }
        }
        if(count == allDetailsInPurchase.size()){
            haveDone = true;
        }
        if(haveError){
            purchaseEntity.setStatus(Constant.PurchaseSheetStatus.FAIL.getValue());
        }else if(haveDone){
            purchaseEntity.setStatus(Constant.PurchaseSheetStatus.DONE.getValue());
        }
        purchaseEntity.setUpdateTime(new Date());
    }

    private WareSkuEntity purchaseItemInWare(List<WareSkuEntity> wareSkus, Long wareId, Long skuId){
        if(wareSkus != null && wareSkus.size() != 0){
            for(WareSkuEntity wareSkuEntity : wareSkus){
                if(wareSkuEntity.getWareId().equals(wareId) && wareSkuEntity.getSkuId().equals(skuId)){
                    return wareSkuEntity;
                }
            }
        }
        return null;
    }

    /**
     * status为更新的状态
     * @param filterDetails
     * @param purchaseId
     * @param status
     * @return
     */
    private List<PurchaseDetailEntity> getPurchaseDetails(List<PurchaseDetailEntity> filterDetails, Long purchaseId, Integer status) {
        return filterDetails.stream().map((item) ->{
                                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                                detailEntity.setPurchaseId(purchaseId);
                                detailEntity.setId(item.getId());
                                detailEntity.setStatus(status);
                                return detailEntity;
                            }).collect(Collectors.toList());
    }

}