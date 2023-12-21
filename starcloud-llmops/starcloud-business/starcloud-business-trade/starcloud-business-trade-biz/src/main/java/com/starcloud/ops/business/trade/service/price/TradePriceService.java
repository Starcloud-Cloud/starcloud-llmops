package com.starcloud.ops.business.trade.service.price;

import com.starcloud.ops.business.trade.service.price.bo.TradePriceCalculateReqBO;
import com.starcloud.ops.business.trade.service.price.bo.TradePriceCalculateRespBO;

import javax.validation.Valid;

/**
 * 价格计算 Service 接口
 *
 * @author 芋道源码
 */
public interface TradePriceService {

    /**
     * 价格计算
     *
     * @param calculateReqDTO 计算信息
     * @return 计算结果
     */
    TradePriceCalculateRespBO calculatePrice(@Valid TradePriceCalculateReqBO calculateReqDTO);

}
