/**
  * Copyright 2021 bejson.com 
  */
package com.lookstarry.doermail.product.vo.spuSave;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Auto-generated: 2021-04-10 17:54:13
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MemberPrice {

    private Long id;
    /**
     * 会员等级名称
     */
    private String name;
    /**
     * 会员价
     */
    private BigDecimal price;

}