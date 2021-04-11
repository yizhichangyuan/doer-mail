package com.lookstarry.doermail.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.lookstarry.doermail.product.entity.AttrEntity;
import com.lookstarry.doermail.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.product.vo
 * @NAME:GroupAndAttrVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/10 15:49
 */
@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 该属性分组关联的所有属性
     */
    private List<AttrEntity> attrs;
}
