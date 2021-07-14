package com.lookstarry.common.exception;

/**
 * @PackageName:com.lookstarry.doermail.ware.exception
 * @NAME:NoStockException
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/22 23:42
 */
public class NoStockException extends RuntimeException{
    private Long skuId;

    public NoStockException(Long skuId){
        super("商品id：" + skuId + ";没有足够的库存了");
    }

    public NoStockException(){
        super("商品" + "没有足够的库存了");
    }

    public Long getSkuId() {
        return skuId;
    }
}
