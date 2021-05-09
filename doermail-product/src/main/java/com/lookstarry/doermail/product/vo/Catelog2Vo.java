package com.lookstarry.doermail.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.product.vo
 * @NAME:Catelog2Vo
 * @Description: 2级分类Vo
 * @author: yizhichangyuan
 * @date:2021/4/17 21:29
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {
    private String catalogId; // 1级父类id

    private List<Catelog3Vo> catalog3List; // 三级子分类

    private String id;

    private String name;

    /**
     * 三级分类Vo
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catelog3Vo{
        private String catalog2Id; // 父分类，2级分类id

        private String id;

        private String name;

    }
}
