package com.lookstarry.doermail.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.ware.vo
 * @NAME:MergeVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/11 22:47
 */
@Data
public class MergeVo {
    private Long purchaseId;

    private List<Long> items;
}
