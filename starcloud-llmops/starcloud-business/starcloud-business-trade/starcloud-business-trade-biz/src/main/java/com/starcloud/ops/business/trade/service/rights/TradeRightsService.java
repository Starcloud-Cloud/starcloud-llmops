package com.starcloud.ops.business.trade.service.rights;

import com.starcloud.ops.business.product.api.spu.dto.SubscribeConfigDTO;
import com.starcloud.ops.business.trade.service.price.bo.TradePriceCalculateReqBO;
import com.starcloud.ops.business.trade.service.price.bo.TradePriceCalculateRespBO;
import com.starcloud.ops.business.trade.service.rights.bo.TradeRightsCalculateRespBO;

import javax.validation.Valid;

/**
 * 价格计算 Service 接口
 *
 * @author 芋道源码
 */
public interface TradeRightsService {

    /**
     * 价格计算
     *
     * @param calculateReqDTO 计算信息
     * @return 计算结果
     */
    TradeRightsCalculateRespBO calculateRights(@Valid TradePriceCalculateReqBO calculateReqDTO);


    /**
     * 价格计算
     *
     * @param calculateReqDTO 计算信息
     * @return 计算结果
     */
    SubscribeConfigDTO calculateSignConfigs(@Valid TradePriceCalculateReqBO calculateReqDTO);

}
