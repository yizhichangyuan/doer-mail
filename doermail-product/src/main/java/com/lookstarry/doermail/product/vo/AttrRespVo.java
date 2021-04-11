package com.lookstarry.doermail.product.vo;

import com.lookstarry.doermail.product.entity.AttrEntity;
import lombok.Data;

/**
 * @PackageName:com.lookstarry.doermail.product.vo
 * @NAME:AttrRespVo
 * @Description: 页面响应Attr的Vo，对应着查询Attr的Vo
 * @author: yizhichangyuan
 * @date:2021/4/9 10:23
 */
@Data
public class AttrRespVo extends AttrEntity {
    private String groupName;

    private String catelogName;

    /**
     * 分类完整路径
     */
    private Long[] catelogPath;

    private Long attrGroupId;
}
