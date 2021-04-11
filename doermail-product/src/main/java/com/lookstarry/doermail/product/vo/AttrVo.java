package com.lookstarry.doermail.product.vo;

import com.lookstarry.doermail.product.entity.AttrEntity;
import lombok.Data;

/**
 * @PackageName:com.lookstarry.doermail.product.vo
 * @NAME:AttrEntityVo
 * @Description: 页面插入Attr对应的Vo
 * @author: yizhichangyuan
 * @date:2021/4/9 09:45
 */
@Data
public class AttrVo extends AttrEntity {
    private Long attrGroupId;
}
