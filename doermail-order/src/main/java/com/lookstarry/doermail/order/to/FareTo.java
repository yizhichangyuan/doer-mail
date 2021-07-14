package com.lookstarry.doermail.order.to;

import com.lookstarry.doermail.order.vo.MemberAddressVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackageName:com.lookstarry.doermail.ware.vo
 * @NAME:FareRespVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/16 15:09
 */
@Data
public class FareTo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
