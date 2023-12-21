package com.starcloud.ops.business.trade.api.order;

import com.starcloud.ops.business.trade.api.order.dto.TradeOrderRespDTO;
import com.starcloud.ops.business.trade.convert.order.TradeOrderConvert;
import com.starcloud.ops.business.trade.service.order.TradeOrderQueryService;
import com.starcloud.ops.business.trade.service.order.TradeOrderUpdateService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 订单 API 接口实现类
 *
 * @author HUIHUI
 */
@Service
@Validated
public class TradeOrderApiImpl implements TradeOrderApi {

    @Resource
    private TradeOrderUpdateService tradeOrderUpdateService;
    @Resource
    private TradeOrderQueryService tradeOrderQueryService;

    @Override
    public List<TradeOrderRespDTO> getOrderList(Collection<Long> ids) {
        return TradeOrderConvert.INSTANCE.convertList04(tradeOrderQueryService.getOrderList(ids));
    }

    @Override
    public TradeOrderRespDTO getOrder(Long id) {
        return TradeOrderConvert.INSTANCE.convert(tradeOrderQueryService.getOrder(id));
    }

    @Override
    public void cancelPaidOrder(Long userId, Long orderId) {
        tradeOrderUpdateService.cancelPaidOrder(userId, orderId);
    }

}
