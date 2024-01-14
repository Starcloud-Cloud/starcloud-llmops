package com.starcloud.ops.business.trade.controller.admin.sign;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.product.api.sku.ProductSkuApi;
import com.starcloud.ops.business.product.api.sku.dto.ProductSkuRespDTO;
import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.promotion.api.promocode.PromoCodeApi;
import com.starcloud.ops.business.trade.controller.admin.sign.vo.AppTradeSignCreateReqVO;
import com.starcloud.ops.business.trade.controller.admin.sign.vo.AppTradeSignSettlementReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.*;
import com.starcloud.ops.business.trade.controller.app.order.vo.item.AppTradeOrderItemCommentCreateReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.item.AppTradeOrderItemRespVO;
import com.starcloud.ops.business.trade.convert.order.TradeOrderConvert;
import com.starcloud.ops.business.trade.dal.dataobject.delivery.DeliveryExpressDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;
import com.starcloud.ops.business.trade.enums.order.TradeOrderStatusEnum;
import com.starcloud.ops.business.trade.framework.order.config.TradeOrderProperties;
import com.starcloud.ops.business.trade.service.delivery.DeliveryExpressService;
import com.starcloud.ops.business.trade.service.order.TradeOrderLogService;
import com.starcloud.ops.business.trade.service.sign.TradeSignQueryService;
import com.starcloud.ops.business.trade.service.sign.TradeSignUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.trade.enums.ErrorCodeConstants.ORDER_CREATE_FAIL_USER_LIMIT;
import static com.starcloud.ops.business.trade.enums.ErrorCodeConstants.ORDER_NOT_FOUND;

@Tag(name = "管理后台 - 交易订单")
@RestController
@RequestMapping("/llm/trade/sign")
@Validated
@Slf4j
public class TradeSignController {

    @Resource
    private TradeSignUpdateService tradeSignUpdateService;
    @Resource
    private TradeSignQueryService tradeSignQueryService;

    @Resource
    private ProductSkuApi productSkuApi;

    @Resource
    private ProductSpuApi productSpuApi;

    @Resource
    private DeliveryExpressService deliveryExpressService;
    @Resource
    private TradeOrderLogService tradeOrderLogService;

    @Resource
    private TradeOrderProperties tradeOrderProperties;

    @Resource
    private AdminUserService adminUserService;


    @Resource
    private PromoCodeApi promoCodeApi;




    //========================APP======ADMIN=====USER=========================

    @PostMapping("/u/settlement")
    @Operation(summary = "系统会员-第一步-获得签约结算信息")
    @PreAuthenticated
    public CommonResult<AppTradeOrderSettlementRespVO> settlementOrder(@Valid @RequestBody AppTradeSignSettlementReqVO settlementReqVO) {
        settlementReqVO.getItems().stream().forEach(item -> {
            validateProductLimit(item.getSkuId());
        });
        return success(tradeSignUpdateService.settlementSign(getLoginUserId(), settlementReqVO));
    }

    @PostMapping("/u/create")
    @Operation(summary = "系统会员-创建签约")
    @PreAuthenticated
    public CommonResult<AppTradeOrderCreateRespVO> createSign(@Valid @RequestBody AppTradeSignCreateReqVO createReqVO) {
        Long userId = getLoginUserId();
        createReqVO.getItems().stream().forEach(item -> {
            validateProductLimit(item.getSkuId());
        });
        TradeSignDO sign = tradeSignUpdateService.createSign(userId, getClientIP(), createReqVO, createReqVO.getTerminal());
        return success(new AppTradeOrderCreateRespVO().setId(sign.getId()).setPayOrderId(sign.getPaySignId()));
    }

    @PostMapping("/u/update-paid")
    @Operation(summary = "系统会员-用户-更新订单为已支付") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
    @PermitAll // 无需登录，安全由 PayDemoOrderService 内部校验实现
    @OperateLog(enable = false) // 禁用操作日志，因为没有操作人
    public CommonResult<Boolean> updateOrderPaid(@RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
        tradeSignUpdateService.updateOrderPaid(Long.valueOf(notifyReqDTO.getMerchantOrderId()),
                notifyReqDTO.getPayOrderId());
        return success(true);
    }
//
//    @GetMapping("/u/get-detail")
//    @Operation(summary = "系统会员-用户-获得交易订单")
//    @Parameter(name = "id", description = "交易订单编号")
//    public CommonResult<AppTradeOrderDetailRespVO> getOrder(@RequestParam("id") Long id) {
//        // 查询订单
//        TradeOrderDO order = tradeSignQueryService.getOrder(getLoginUserId(), id);
//        if (order == null) {
//            return success(null);
//        }
//
//        // 查询订单项
//        List<TradeOrderItemDO> orderItems = tradeSignQueryService.getOrderItemListByOrderId(order.getId());
//        // 查询物流公司
//        DeliveryExpressDO express = order.getLogisticsId() != null && order.getLogisticsId() > 0 ?
//                deliveryExpressService.getDeliveryExpress(order.getLogisticsId()) : null;
//        // 最终组合
//        return success(TradeOrderConvert.INSTANCE.convert02(order, orderItems, tradeOrderProperties, express));
//    }
//
////    @GetMapping("/u/get-express-track-list")
////    @Operation(summary = "获得交易订单的物流轨迹")
////    @Parameter(name = "id", description = "交易订单编号")
////    public CommonResult<List<?>> getOrderExpressTrackList(@RequestParam("id") Long id) {
////        return success(TradeOrderConvert.INSTANCE.convertList02(
////                tradeOrderQueryService.getExpressTrackList(id, getLoginUserId())));
////    }
//
//    @GetMapping("/u/page")
//    @Operation(summary = "系统会员-获得交易订单分页")
//    public CommonResult<PageResult<AppTradeOrderPageItemRespVO>> getOrderPage(AppTradeOrderPageReqVO reqVO) {
//        // 查询订单
//        PageResult<TradeOrderDO> pageResult = tradeSignQueryService.getOrderPage(getLoginUserId(), reqVO);
//        // 查询订单项
//        List<TradeOrderItemDO> orderItems = tradeSignQueryService.getOrderItemListByOrderId(
//                convertSet(pageResult.getList(), TradeOrderDO::getId));
//        // 最终组合
//        return success(TradeOrderConvert.INSTANCE.convertPage02(pageResult, orderItems));
//    }
//
//    @GetMapping("/u/get-count")
//    @Operation(summary = "系统会员-获得交易订单数量")
//    public CommonResult<Map<String, Long>> getOrderCount() {
//        Map<String, Long> orderCount = Maps.newLinkedHashMapWithExpectedSize(5);
//        // 全部
//        orderCount.put("allCount", tradeSignQueryService.getOrderCount(getLoginUserId(), null, null));
//        // 待付款（未支付）
//        orderCount.put("unpaidCount", tradeSignQueryService.getOrderCount(getLoginUserId(),
//                TradeOrderStatusEnum.UNPAID.getStatus(), null));
//        // 待发货
//        orderCount.put("undeliveredCount", tradeSignQueryService.getOrderCount(getLoginUserId(),
//                TradeOrderStatusEnum.UNDELIVERED.getStatus(), null));
//        // 待收货
//        orderCount.put("deliveredCount", tradeSignQueryService.getOrderCount(getLoginUserId(),
//                TradeOrderStatusEnum.DELIVERED.getStatus(), null));
//        // 待评价
//        orderCount.put("uncommentedCount", tradeSignQueryService.getOrderCount(getLoginUserId(),
//                TradeOrderStatusEnum.COMPLETED.getStatus(), false));
//        return success(orderCount);
//    }
//
//    @PutMapping("/u/receive")
//    @Operation(summary = "系统会员-确认交易订单收货")
//    @Parameter(name = "id", description = "交易订单编号")
//    public CommonResult<Boolean> receiveOrder(@RequestParam("id") Long id) {
//        tradeSignUpdateService.receiveOrderByMember(getLoginUserId(), id);
//        return success(true);
//    }
//
//    @DeleteMapping("/u/cancel")
//    @Operation(summary = "系统会员-取消交易订单")
//    @Parameter(name = "id", description = "交易订单编号")
//    public CommonResult<Boolean> cancelOrder(@RequestParam("id") Long id) {
//        tradeSignUpdateService.cancelOrderByMember(getLoginUserId(), id);
//        return success(true);
//    }
//
//    @DeleteMapping("/u/delete")
//    @Operation(summary = "系统会员-删除交易订单")
//    @Parameter(name = "id", description = "交易订单编号")
//    public CommonResult<Boolean> deleteOrder(@RequestParam("id") Long id) {
//        tradeSignUpdateService.deleteOrder(getLoginUserId(), id);
//        return success(true);
//    }
//
//    // ========== 订单项 ==========
//
//    @GetMapping("/u/is-success")
//    @Operation(summary = "系统会员-判断订单状态是否支付成功")
//    @Parameter(name = "id", description = "交易订单编号")
//    public CommonResult<Boolean> validateOrderStatus(@RequestParam("id") Long id) {
//        Map<String, Long> orderCount = Maps.newLinkedHashMapWithExpectedSize(5);
//        TradeOrderDO order = tradeSignQueryService.getOrder(getLoginUserId(), id);
//        if (Objects.isNull(order)) {
//            throw exception(ORDER_NOT_FOUND);
//        }
//        if (order.getStatus().equals(TradeOrderStatusEnum.UNPAID.getStatus()) || order.getStatus().equals(TradeOrderStatusEnum.CANCELED.getStatus())) {
//            return success(false);
//        }
//        return success(true);
//    }
//
//    @GetMapping("/u/item/get")
//    @Operation(summary = "系统会员-获得交易订单项")
//    @Parameter(name = "id", description = "交易订单项编号")
//    public CommonResult<AppTradeOrderItemRespVO> getOrderItem(@RequestParam("id") Long id) {
//        TradeOrderItemDO item = tradeSignQueryService.getOrderItem(getLoginUserId(), id);
//        return success(TradeOrderConvert.INSTANCE.convert03(item));
//    }
//
//    @PostMapping("/u/item/create-comment")
//    @Operation(summary = "系统会员-创建交易订单项的评价")
//    public CommonResult<Long> createOrderItemComment(@RequestBody AppTradeOrderItemCommentCreateReqVO createReqVO) {
//        return success(tradeSignUpdateService.createOrderItemCommentByMember(getLoginUserId(), createReqVO));
//    }
//
//
    private void validateProductLimit(Long skuId){
        ProductSkuRespDTO sku = productSkuApi.getSku(skuId);
        if (!Objects.isNull(sku)) {
            ProductSpuRespDTO spu = productSpuApi.getSpu(sku.getSpuId());
            if (!Objects.isNull(spu) && !spu.getRegisterDays().equals(-1)) {
                AdminUserDO user = adminUserService.getUser(getLoginUserId());
                LocalDateTime now = LocalDateTimeUtil.now();
                LocalDateTime sevenDaysAgo = now.minusDays(spu.getRegisterDays());
                if (user.getCreateTime().isBefore(sevenDaysAgo)) {
                    throw exception(ORDER_CREATE_FAIL_USER_LIMIT);
                }

            }
        }
    }
//
//
//    private AppTradeOrderCreateReqVO BuildPromoCodeAndCoupon(AppTradeOrderCreateReqVO createReqVO,Long userId) {
//        if (createReqVO.getPromoCode() == null){
//            return createReqVO;
//        }
//        // 使用兑换码中的权益码  自动领取优惠券
//        Long couponId= promoCodeApi.usePromoCode(createReqVO.getPromoCode(), userId);
//
//        // 设置优惠券
//        createReqVO.setCouponId(couponId);
//        createReqVO.setPromoCode(null);
//        return createReqVO;
//
//    }


}
