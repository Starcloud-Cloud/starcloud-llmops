package com.starcloud.ops.business.trade.service.sign;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.core.KeyValue;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.pay.api.sign.PaySignApi;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignRespDTO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.starcloud.ops.business.product.api.spu.dto.SubscribeConfigDTO;
import com.starcloud.ops.business.trade.controller.admin.sign.vo.AppTradeSignCreateReqVO;
import com.starcloud.ops.business.trade.controller.admin.sign.vo.AppTradeSignSettlementReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderSettlementRespVO;
import com.starcloud.ops.business.trade.convert.order.TradeOrderConvert;
import com.starcloud.ops.business.trade.convert.sign.TradeSignConvert;
import com.starcloud.ops.business.trade.dal.dataobject.cart.CartDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignItemDO;
import com.starcloud.ops.business.trade.dal.mysql.sign.TradeSignItemMapper;
import com.starcloud.ops.business.trade.dal.mysql.sign.TradeSignMapper;
import com.starcloud.ops.business.trade.dal.redis.no.TradeNoRedisDAO;
import com.starcloud.ops.business.trade.enums.order.TradeOrderRefundStatusEnum;
import com.starcloud.ops.business.trade.enums.sign.TradeSignStatusEnum;
import com.starcloud.ops.business.trade.framework.order.config.TradeOrderProperties;
import com.starcloud.ops.business.trade.service.cart.CartService;
import com.starcloud.ops.business.trade.service.price.TradePriceService;
import com.starcloud.ops.business.trade.service.price.bo.TradePriceCalculateReqBO;
import com.starcloud.ops.business.trade.service.price.bo.TradePriceCalculateRespBO;
import com.starcloud.ops.business.trade.service.rights.TradeRightsService;
import com.starcloud.ops.business.trade.service.rights.bo.TradeRightsCalculateRespBO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.getSumValue;
import static cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum.DAY;
import static com.starcloud.ops.business.trade.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class TradeSignUpdateServiceImpl implements TradeSignUpdateService {


    @Resource
    private TradeSignMapper tradeSignMapper;

    @Resource
    private TradeSignItemMapper tradeSignItemMapper;

    @Resource
    private CartService cartService;
    @Resource
    private TradePriceService tradePriceService;

    @Resource
    private TradeRightsService tradeRightsService;


    @Resource
    private TradeNoRedisDAO tradeNoRedisDAO;


    @Resource
    private PaySignApi paySignApi;

    @Resource
    private TradeOrderProperties tradeOrderProperties;


    @Resource
    private TradeSignQueryService tradeSignQueryService;


    @Override
    public AppTradeOrderSettlementRespVO settlementSign(Long userId, AppTradeSignSettlementReqVO settlementReqVO) {
        // 1. 计算价格
        TradePriceCalculateRespBO calculateRespBO = calculatePrice(userId, settlementReqVO);
        // 2. 拼接返回
        return TradeOrderConvert.INSTANCE.convert(calculateRespBO, null);
    }

    @Override
    public TradeSignDO createSign(Long userId, String userIp, AppTradeSignCreateReqVO createReqVO, Integer terminal) {
        // 1.1 价格计算
        TradePriceCalculateRespBO calculateRespBO = calculatePrice(userId, createReqVO);

        TradeRightsCalculateRespBO calculateRightsRespBO = calculateRights(userId, createReqVO);
        // 1.2 构建订单
        TradeSignDO tradeSignDO = buildTradeSign(userId, userIp, createReqVO, calculateRespBO, terminal);
        // 1.3 设置订单权益组
        tradeSignDO.setGiveRights(calculateRightsRespBO.getGiveRights());
        // 1.3 设置订单权益组
        tradeSignDO.setSignConfigs(getProductSignConfig(userId, createReqVO));

        List<TradeSignItemDO> signItemDOS = buildTradeSignItems(tradeSignDO, calculateRespBO);

        tradeSignDO.setPayTime(buildSignPayTime(LocalDate.now().plusDays(4),TimeRangeTypeEnum.getByType(tradeSignDO.getSignConfigs().getPeriodType())));

        // 2. 保存记录
        tradeSignMapper.insert(tradeSignDO);
        signItemDOS.forEach(orderItem -> orderItem.setSignId(tradeSignDO.getId()));
        tradeSignItemMapper.insertBatch(signItemDOS);

        // 3. 签约创建后的逻辑
        afterCreateTradeOrder(tradeSignDO, signItemDOS, createReqVO);
        return tradeSignDO;
    }

    private SubscribeConfigDTO getProductSignConfig(Long userId, AppTradeSignCreateReqVO createReqVO) {

        // 1. 如果来自购物车，则获得购物车的商品
        List<CartDO> cartList = cartService.getCartList(userId,
                convertSet(createReqVO.getItems(), AppTradeSignSettlementReqVO.Item::getCartId));

        // 2. 计算价格
        TradePriceCalculateReqBO calculateReqBO = TradeSignConvert.INSTANCE.convert(userId, createReqVO, cartList);
        calculateReqBO.getItems().forEach(item -> Assert.isTrue(item.getSelected(), // 防御性编程，保证都是选中的
                "商品({}) 未设置为选中", item.getSkuId()));
        return tradeRightsService.calculateSignConfigs(calculateReqBO);

    }

    @Override
    public void updateSignStatus(Long id, Long paySignId, Boolean closeSign) {

        KeyValue<TradeSignDO, PaySignRespDTO> orderResult = validateSignAble(id, paySignId, closeSign);

        TradeSignDO order = orderResult.getKey();
        PaySignRespDTO payOrder = orderResult.getValue();

        // // 2. 更新 TradeOrderDO 状态为已支付，等待发货
        TradeSignDO signDO = new TradeSignDO()
                .setPayChannelCode(payOrder.getChannelCode());
        if (closeSign){
            signDO.setStatus(TradeSignStatusEnum.CANCELED.getStatus())
                    .setCancelTime(LocalDateTime.now());
        }else {
            signDO.setStatus(TradeSignStatusEnum.SIGNING.getStatus())
                    .setFinishTime(LocalDateTime.now());
        }
        int updateCount = 0;
           updateCount = tradeSignMapper.updateByIdAndStatus(id, order.getStatus(),
                   signDO );


        if (updateCount == 0) {
           throw exception(ORDER_UPDATE_PAID_STATUS_NOT_UNPAID);
        }
        // tradeSignQueryService.getSign()
        //
        //
        // // 1. 校验并获得交易订单（可支付）
        // KeyValue<TradeOrderDO, PayOrderRespDTO> orderResult = validateSignAble(id, payOrderId);

        //
        // // 2. 更新 TradeOrderDO 状态为已支付，等待发货
        // int updateCount = 0;
        //
        //    updateCount = tradeSignMapper.updateByIdAndStatus(id, order.getStatus(),
        //            new TradeSignDO().setStatus(TradeSignStatusEnum.SIGNING.getStatus())
        //                    .setFinishTime(LocalDateTime.now())
        //                    .setPayChannelCode(payOrder.getChannelCode()));
        //
        //
        // if (updateCount == 0) {
        //    throw exception(ORDER_UPDATE_PAID_STATUS_NOT_UNPAID);
        // }
        //
        // // 3. 执行 TradeOrderHandler 的后置处理
        // List<TradeOrderItemDO> orderItems = tradeOrderItemMapper.selectListByOrderId(id);
        // tradeOrderHandlers.forEach(handler -> handler.afterPayOrder(order, orderItems));
        //
        // // 4. 记录订单日志
        // Integer afterStatus = TradeOrderStatusEnum.UNDELIVERED.getStatus();
        // if (Objects.equals(DeliveryTypeEnum.AUTO.getType(), order.getDeliveryType())) {
        //    afterStatus = TradeOrderStatusEnum.COMPLETED.getStatus();
        // }
        // TradeOrderLogUtils.setOrderInfo(order.getId(), order.getStatus(), afterStatus);
        // TradeOrderLogUtils.setUserInfo(order.getUserId(), UserTypeEnum.ADMIN.getValue());
        //
        // sendPaySuccessMsg(order.getUserId(), orderItems.get(0).getSpuName(), order.getTotalPrice(), order.getDiscountPrice(), order.getPayPrice(), LocalDateTime.now());

    }

    /**
     * 计算订单价格
     *
     * @param userId          用户编号
     * @param settlementReqVO 结算信息
     * @return 订单价格
     */
    private TradePriceCalculateRespBO calculatePrice(Long userId, AppTradeSignSettlementReqVO settlementReqVO) {
        // 1. 如果来自购物车，则获得购物车的商品
        List<CartDO> cartList = cartService.getCartList(userId,
                convertSet(settlementReqVO.getItems(), AppTradeSignSettlementReqVO.Item::getCartId));

        // 2. 计算价格
        TradePriceCalculateReqBO calculateReqBO = TradeSignConvert.INSTANCE.convert(userId, settlementReqVO, cartList);
        calculateReqBO.getItems().forEach(item -> Assert.isTrue(item.getSelected(), // 防御性编程，保证都是选中的
                "商品({}) 未设置为选中", item.getSkuId()));
        return tradePriceService.calculateSignPrice(calculateReqBO);
    }

    /**
     * 计算权益
     *
     * @param userId          用户编号
     * @param settlementReqVO 结算信息
     * @return 订单价格
     */
    private TradeRightsCalculateRespBO calculateRights(Long userId, AppTradeSignCreateReqVO settlementReqVO) {
        // 1. 如果来自购物车，则获得购物车的商品
        List<CartDO> cartList = cartService.getCartList(userId,
                convertSet(settlementReqVO.getItems(), AppTradeSignCreateReqVO.Item::getCartId));

        // 2. 计算价格
        TradePriceCalculateReqBO calculateReqBO = TradeSignConvert.INSTANCE.convert(userId, settlementReqVO, cartList);
        calculateReqBO.getItems().forEach(item -> Assert.isTrue(item.getSelected(), // 防御性编程，保证都是选中的
                "商品({}) 未设置为选中", item.getSkuId()));
        return tradeRightsService.calculateRights(calculateReqBO);
    }


    private TradeSignDO buildTradeSign(Long userId, String clientIp, AppTradeSignCreateReqVO createReqVO,
                                       TradePriceCalculateRespBO calculateRespBO, Integer terminal) {
        TradeSignDO tradeSignDO = TradeSignConvert.INSTANCE.convert(userId, clientIp, createReqVO, calculateRespBO);
        tradeSignDO.setType(calculateRespBO.getType());
        tradeSignDO.setNo(tradeNoRedisDAO.generate(TradeNoRedisDAO.TRADE_SIGN_NO_PREFIX));
        tradeSignDO.setStatus(TradeSignStatusEnum.UN_SIGN.getStatus());
        tradeSignDO.setProductCount(getSumValue(calculateRespBO.getItems(), TradePriceCalculateRespBO.OrderItem::getCount, Integer::sum));
        tradeSignDO.setTerminal(terminal);
        // 支付 + 退款信息
        tradeSignDO.setAdjustPrice(0).setPaySignStatus(false);
        tradeSignDO.setRefundStatus(TradeOrderRefundStatusEnum.NONE.getStatus()).setRefundPrice(0);
//        if (Objects.equals(createReqVO.getDeliveryType(), DeliveryTypeEnum.EXPRESS.getType())) {
//            MemberAddressRespDTO address = addressApi.getAddress(createReqVO.getAddressId(), userId);
//            Assert.notNull(address, "地址({}) 不能为空", createReqVO.getAddressId()); // 价格计算时，已经计算
//            order.setReceiverName(address.getName()).setReceiverMobile(address.getMobile())
//                    .setReceiverAreaId(address.getAreaId()).setReceiverDetailAddress(address.getDetailAddress());
//        } else if (Objects.equals(createReqVO.getDeliveryType(), DeliveryTypeEnum.PICK_UP.getType())) {
//            order.setReceiverName(createReqVO.getReceiverName()).setReceiverMobile(createReqVO.getReceiverMobile());
//            order.setPickUpVerifyCode(RandomUtil.randomNumbers(8)); // 随机一个核销码，长度为 8 位
//        }
        return tradeSignDO;
    }


    /**
     * 订单创建后，执行后置逻辑
     * <p>
     * 例如说：优惠劵的扣减、积分的扣减、支付单的创建等等
     *
     * @param signDO           订单
     * @param tradeSignItemDOS 订单项
     * @param createReqVO      创建订单请求
     */
    private void afterCreateTradeOrder(TradeSignDO signDO, List<TradeSignItemDO> tradeSignItemDOS,
                                       AppTradeSignCreateReqVO createReqVO) {
        // 1. 删除购物车商品
        Set<Long> cartIds = convertSet(createReqVO.getItems(), AppTradeSignCreateReqVO.Item::getCartId);
        if (CollUtil.isNotEmpty(cartIds)) {
            cartService.deleteCart(signDO.getUserId(), cartIds);
        }
        // 2. 生成预签约
        createPaySign(signDO, tradeSignItemDOS);

    }

    private void createPaySign(TradeSignDO signDO, List<TradeSignItemDO> tradeSignItemDOS) {

        // 创建支付单，用于后续的支付
        PaySignCreateReqDTO payOrderCreateReqDTO = TradeSignConvert.INSTANCE.convert(
                signDO, tradeSignItemDOS, tradeOrderProperties);
        Long paySignId = paySignApi.createSign(payOrderCreateReqDTO);

        // 更新到交易单上
        tradeSignMapper.updateById(new TradeSignDO().setId(signDO.getId()).setPaySignId(paySignId));
        signDO.setPaySignId(paySignId);

    }

    private List<TradeSignItemDO> buildTradeSignItems(TradeSignDO tradeSignDO,
                                                      TradePriceCalculateRespBO calculateRespBO) {
        return TradeSignConvert.INSTANCE.convertList(tradeSignDO, calculateRespBO);
    }

    private LocalDate buildSignPayTime(LocalDate date ,TimeRangeTypeEnum timeRange) {
        // 判断日期是否为每个月的28号以后
        if (date.getDayOfMonth() > 28) {
            switch (timeRange) {
                case DAY:
                    // 如果是，则将日期设置为下个月的1号
                    return date.plusDays(7);
                case MONTH:
                    // 如果是，则将日期设置为下个月的1号
                    return date.plusMonths(1).withDayOfMonth(1);
            }
            // 如果是，则将日期设置为下个月的1号
            return date.plusMonths(1).withDayOfMonth(1);
        }
        return date;
    }


    @NotNull
    private TradeSignDO validateSignExists(Long id) {
        // 校验订单是否存在
        TradeSignDO signDO = tradeSignMapper.selectById(id);
        if (signDO == null) {
            throw exception(SIGN_NOT_FOUND);
        }
        return signDO;
    }

    private KeyValue<TradeSignDO, PaySignRespDTO> validateSignAble(Long id, Long paySignId, Boolean isClose) {
        // 校验订单是否存在
        TradeSignDO signDO = validateSignExists(id);
        if (!isClose) {

            // 校验订单未签约
            if (!TradeSignStatusEnum.isUnpaid(signDO.getStatus()) || signDO.getPaySignStatus()) {
                log.error("[validateOrderPaid][order({}) 不处于待支付状态，请进行处理！order 数据是：{}]",
                        id, JsonUtils.toJsonString(signDO));
                throw exception(ORDER_UPDATE_PAID_STATUS_NOT_UNPAID);
            }
            // 校验支付订单匹配
            if (ObjectUtil.notEqual(signDO.getPaySignId(), paySignId)) { // 支付单号
                log.error("[validateOrderPaid][order({}) 支付单不匹配({})，请进行处理！order 数据是：{}]",
                        id, paySignId, JsonUtils.toJsonString(signDO));
                throw exception(ORDER_UPDATE_PAID_FAIL_PAY_ORDER_ID_ERROR);
            }
        }


        // 校验支付单是否存在
        PaySignRespDTO paySignRespDTO = paySignApi.getSign(paySignId);
        if (paySignRespDTO == null) {
            log.error("[validateOrderPaid][order({}) payOrder({}) 不存在，请进行处理！]", id, paySignId);
            throw exception(SIGN_NOT_FOUND);
        }
        // // 校验支付单已支付
        // if (!PayOrderStatusEnum.isSuccess(paySignRespDTO.getStatus())) {
        //     log.error("[validateOrderPaid][order({}) payOrder({}) 未支付，请进行处理！payOrder 数据是：{}]",
        //             id, paySignId, JsonUtils.toJsonString(paySignRespDTO));
        //     throw exception(ORDER_UPDATE_PAID_FAIL_PAY_ORDER_STATUS_NOT_SUCCESS);
        // }
        // // 校验支付金额一致
        // if (ObjectUtil.notEqual(paySignRespDTO.getPrice(), signDO.getPayPrice())) {
        //     log.error("[validateOrderPaid][order({}) payOrder({}) 支付金额不匹配，请进行处理！order 数据是：{}，payOrder 数据是：{}]",
        //             id, paySignId, JsonUtils.toJsonString(signDO), JsonUtils.toJsonString(paySignRespDTO));
        //     throw exception(ORDER_UPDATE_PAID_FAIL_PAY_PRICE_NOT_MATCH);
        // }
        // // 校验支付订单匹配（二次）
        // if (ObjectUtil.notEqual(paySignRespDTO.getMerchantOrderId(), id.toString())) {
        //     log.error("[validateOrderPaid][order({}) 支付单不匹配({})，请进行处理！payOrder 数据是：{}]",
        //             id, paySignId, JsonUtils.toJsonString(paySignRespDTO));
        //     throw exception(ORDER_UPDATE_PAID_FAIL_PAY_ORDER_ID_ERROR);
        // }
        return new KeyValue<>(signDO, paySignRespDTO);
    }
}