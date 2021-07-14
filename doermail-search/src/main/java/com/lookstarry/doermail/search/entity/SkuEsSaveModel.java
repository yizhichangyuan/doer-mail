package com.lookstarry.doermail.search.entity;

import com.lookstarry.common.to.es.SkuEsModel;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @PackageName:com.lookstarry.doermail.search
 * @NAME:SkuEsSaveModel
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/16 23:29
 */
@Data
@Document(indexName="product", type="info")
public class SkuEsSaveModel extends SkuEsModel{
////    @Field(type=FieldType.Long)
//    private Long spuId;
//
//    @Id
//    private Long skuId;
//
////    @Field(type=FieldType.Text)
//    private String skuTitle;
//
////    @Field(type=FieldType.Keyword)
//    private BigDecimal skuPrice;
//
////    @Field(type=FieldType.Keyword)
//    private String skuImg;
//
////    @Field(type=FieldType.Long)
//    private Long saleCount;
//
////    @Field(type=FieldType.Boolean)
//    private boolean hasStock;
//
////    @Field(type=FieldType.Long)
//    private Long hotScore;
//
////    @Field(type=FieldType.Long)
//    private Long brandId;
//
////    @Field(type=FieldType.Long)
//    private Long catelogId;
//
////    @Field(type=FieldType.Keyword)
//    private String brandName;
//
//    private String brandImg;
//
////    @Field(type=FieldType.Keyword)
//    private String catelogName;
//
////    @Field(type=FieldType.Nested)
//    private List<Attrs> attrs;
//
//    /**
//     * 标记为public是为了第三方工具可以序列化和反序列化
//     */
//    @Data
//    public static class Attrs{
////        @Field(type=FieldType.Long)
//        private Long attrId;
//
////        @Field(type=FieldType.Keyword)
//        private String attrName;
//
////        @Field(type=FieldType.Keyword)
//        private String attrValue;
//    }
}
