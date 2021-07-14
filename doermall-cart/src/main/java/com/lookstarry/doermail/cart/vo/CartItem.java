package com.lookstarry.doermail.cart.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @PackageName:com.doermall.cart.vo
 * @NAME:CartItem
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/16 12:46
 */
public class CartItem {
    private Long skuId;
    private Boolean checked = true; // 是否选中
    private String title;
    private String defaultImage;
    private List<String> saleAttr;  // 销售属性
    private BigDecimal unitPrice;  // 单价
    private Integer count;
    private BigDecimal totalPrice; // 总价

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    public List<String> getSaleAttr() {
        return saleAttr;
    }

    public void setSaleAttr(List<String> saleAttr) {
        this.saleAttr = saleAttr;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 计算总价
     * @return
     */
    public BigDecimal getTotalPrice() {
        return this.unitPrice.multiply(new BigDecimal(this.count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
