package com.lookstarry.common.to;

import lombok.Data;
import org.springframework.data.annotation.Id;
import java.math.BigDecimal;
import java.util.List;

/**
 * @PackageName:com.lookstarry.common.to
 * @NAME:SkuEsModel
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/16 10:07
 */
@Data
public class SkuEsModel {
    /**
     * {
     * 	"mappings": {
     * 		"properties": {
     * 			"skuId": {
     * 				"type": "long"
     *                        },
     * 			"spuId": {
     * 				"type": "keyword"
     *            },
     * 			"skuTitle": {
     * 				"type": "text",
     * 				"analyzer": "ik_smart"
     *            },
     * 			"skuPrice": {
     * 				"type": "keyword"
     *            },
     * 			"skuImg": {
     * 				"type": "keyword",
     * 				"index": false,
     * 				"doc_values": false
     *            },
     * 			"saleCount": {
     * 				"type": "long"
     *            },
     * 			"hasStock ": {
     * 				"type": "boolean"
     *            },
     * 			"hotScore": {
     * 				"type": "long"
     *            },
     * 			"brandId": {
     * 				"type": "long"
     *            },
     * 			"catelogId": {
     * 				"type": "long"
     *            },
     * 			"brandName": {
     * 				"type": "keyword",
     * 				"index": false,
     * 				"doc_values": false
     *            },
     * 			"catelogName": {
     * 				"type": "keyword",
     * 				"index": false,
     * 				"doc_values": false
     *            },
     * 			"attrs": {
     * 				"type": "nested",
     * 				"properties": {
     * 					"attrId": {
     * 						"type": "long"
     *                    },
     * 					"attrName": {
     * 						"type": "keyword",
     * 						"index": false,
     * 						"doc_values": false
     *                    },
     * 					"attrValue": {
     * 						"type": "keyword"
     *                    }
     *                }
     *            }* 		}
     *    }
     * }
     */

    private Long spuId;

    @Id
    private Long skuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private boolean hasStock;

    private Long hotScore;

    private Long brandId;
    private String brandName;
    private String brandImg;

    private Long catelogId;

    private String catelogName;

    private List<Attrs> attrs;

    /**
     * 标记为public是为了第三方工具可以序列化和反序列化
     */
    @Data
    public static class Attrs{
        private Long attrId;

        private String attrName;

        private String attrValue;
    }




}
