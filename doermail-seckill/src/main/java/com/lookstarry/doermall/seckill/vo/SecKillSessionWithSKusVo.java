package com.lookstarry.doermall.seckill.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @PackageName:com.lookstarry.doermall.seckill.vo
 * @NAME:SecKillSessionVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/3 13:47
 */
@Data
public class SecKillSessionWithSKusVo {
    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;

    private List<SeckillSkuRelationEntity> relationSkus;
}
