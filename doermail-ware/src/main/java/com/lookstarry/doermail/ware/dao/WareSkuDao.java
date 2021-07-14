package com.lookstarry.doermail.ware.dao;

import com.lookstarry.doermail.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lookstarry.doermail.ware.entity.WareSkuLeftEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * 商品库存
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 14:03:12
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    List<WareSkuLeftEntity> selectLeftStock(@Param("skuIds") List<Long> skuIds);

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId, @Param("needNum") Integer needNum);

    Long lockSkuStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("lockNum") Integer lockNum);

    void unlockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
