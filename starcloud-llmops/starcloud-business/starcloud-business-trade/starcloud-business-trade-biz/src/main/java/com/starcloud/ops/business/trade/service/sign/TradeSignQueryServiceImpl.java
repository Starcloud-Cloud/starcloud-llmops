package com.starcloud.ops.business.trade.service.sign;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderPageReqVO;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderSummaryRespVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderCreateReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderPageReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderSettlementReqVO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignItemDO;
import com.starcloud.ops.business.trade.dal.mysql.sign.TradeSignItemMapper;
import com.starcloud.ops.business.trade.dal.mysql.sign.TradeSignMapper;
import com.starcloud.ops.business.trade.enums.order.TradeOrderItemAfterSaleStatusEnum;
import com.starcloud.ops.business.trade.framework.delivery.core.client.dto.ExpressTrackRespDTO;
import com.starcloud.ops.business.trade.service.order.TradeOrderUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

/**
 * 交易订单【读】 Service 接口
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class TradeSignQueryServiceImpl implements TradeSignQueryService{


    @Resource
    private TradeSignMapper tradeSignMapper;

    @Resource
    private TradeSignItemMapper tradeSignItemMapper;



    @Resource
    private TradeOrderUpdateService tradeOrderUpdateService;


    /**
     * 获得指定编号的交易订单
     *
     * @param id 交易订单编号
     * @return 交易订单
     */
    @Override
    public TradeSignDO getSign(Long id) {
        return tradeSignMapper.selectById(id);
    }

    /**
     * 获得指定用户，指定的交易订单
     *
     * @param userId 用户编号
     * @param id     交易订单编号
     * @return 交易订单
     */
    @Override
    public TradeSignDO getSign(Long userId, Long id) {
        return tradeSignMapper.selectByIdAndUserId(id,userId);
    }

    /**
     * @return
     */
    @Override
    public int executeAutoTradeSignPay() {

        List<TradeSignDO> tradeSignDOS = tradeSignMapper.selectIsSignSuccess();

        if (tradeSignDOS.isEmpty()){
            return 0;
        }

        List<TradeSignDO> waitePaySigns = tradeSignDOS
                .stream()
                .filter(tradeSignDO ->
                        LocalDateTimeUtil
                                .isIn(LocalDate.now().atStartOfDay(), tradeSignDO.getPayTime().minusDays(5L).atStartOfDay(),
                                        LocalDateTimeUtil.endOfDay(tradeSignDO.getPayTime().atStartOfDay())))
                .collect(Collectors.toList());
        if (waitePaySigns.isEmpty()) {
            return 0;
        }

        // 2. 遍历执行扣款
        int count = 0;
        for (TradeSignDO waitePaySign : waitePaySigns) {
            count += autoTradeSignPay(waitePaySign) ? 1 : 0;
        }
        return count;

    }
    public Boolean autoTradeSignPay(TradeSignDO tradeSignDO){
        AppTradeOrderCreateReqVO createReqVO =new AppTradeOrderCreateReqVO();

        List<TradeSignItemDO> tradeSignItemDOS = tradeSignItemMapper.selectListBySignId(tradeSignDO.getPaySignId());

        ArrayList<AppTradeOrderSettlementReqVO.Item> items = new ArrayList<>();
        for (TradeSignItemDO tradeSignItemDO : tradeSignItemDOS) {
            AppTradeOrderSettlementReqVO.Item orderItem = new AppTradeOrderSettlementReqVO.Item();
            orderItem.setSkuId(tradeSignItemDO.getSkuId());
            orderItem.setCount(tradeSignItemDO.getCount());
            items.add(orderItem);
        }
        createReqVO.setItems(items);
        createReqVO.setPointStatus(false);


        tradeOrderUpdateService.createOrder(tradeSignDO.getUserId(),tradeSignDO.getUserIp(),createReqVO,20);


        return true;
    }
}
