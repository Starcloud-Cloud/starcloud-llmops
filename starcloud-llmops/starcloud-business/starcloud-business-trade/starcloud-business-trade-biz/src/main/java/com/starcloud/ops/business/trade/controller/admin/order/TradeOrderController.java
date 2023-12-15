package com.starcloud.ops.business.trade.controller.admin.order;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;

import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.trade.controller.admin.order.vo.*;
import com.starcloud.ops.business.trade.controller.app.order.vo.*;
import com.starcloud.ops.business.trade.controller.app.order.vo.item.AppTradeOrderItemCommentCreateReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.item.AppTradeOrderItemRespVO;
import com.starcloud.ops.business.trade.convert.order.TradeOrderConvert;
import com.starcloud.ops.business.trade.dal.dataobject.delivery.DeliveryExpressDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderLogDO;
import com.starcloud.ops.business.trade.enums.order.TradeOrderStatusEnum;
import com.starcloud.ops.business.trade.framework.order.config.TradeOrderProperties;
import com.starcloud.ops.business.trade.service.delivery.DeliveryExpressService;
import com.starcloud.ops.business.trade.service.order.TradeOrderLogService;
import com.starcloud.ops.business.trade.service.order.TradeOrderQueryService;
import com.starcloud.ops.business.trade.service.order.TradeOrderUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 交易订单")
@RestController
@RequestMapping("/llm/trade/order")
@Validated
@Slf4j
public class TradeOrderController {

    @Resource
    private TradeOrderUpdateService tradeOrderUpdateService;
    @Resource
    private TradeOrderQueryService tradeOrderQueryService;

    @Resource
    private DeliveryExpressService deliveryExpressService;
    @Resource
    private TradeOrderLogService tradeOrderLogService;

    @Resource
    private TradeOrderProperties tradeOrderProperties;

    @Resource
    private AdminUserService adminUserService;

    @GetMapping("/page")
    @Operation(summary = "获得交易订单分页")
    @PreAuthorize("@ss.hasPermission('trade:order:query')")
    public CommonResult<PageResult<TradeOrderPageItemRespVO>> getOrderPage(TradeOrderPageReqVO reqVO) {
        // 查询订单
        PageResult<TradeOrderDO> pageResult = tradeOrderQueryService.getOrderPage(reqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty());
        }

        // 查询用户信息
        Set<Long> userIds = CollUtil.unionDistinct(convertList(pageResult.getList(), TradeOrderDO::getUserId),
                convertList(pageResult.getList(), TradeOrderDO::getBrokerageUserId, Objects::nonNull));
        Map<Long, AdminUserDO> userMap = adminUserService.getUserMap(userIds);
        // 查询订单项
        List<TradeOrderItemDO> orderItems = tradeOrderQueryService.getOrderItemListByOrderId(
                convertSet(pageResult.getList(), TradeOrderDO::getId));
        // 最终组合
        return success(TradeOrderConvert.INSTANCE.convertPage(pageResult, orderItems, userMap));
    }

    @GetMapping("/summary")
    @Operation(summary = "获得交易订单统计")
    @PreAuthorize("@ss.hasPermission('trade:order:query')")
    public CommonResult<TradeOrderSummaryRespVO> getOrderSummary(TradeOrderPageReqVO reqVO) {
        return success(tradeOrderQueryService.getOrderSummary(reqVO));
    }

    @GetMapping("/get-detail")
    @Operation(summary = "获得交易订单详情")
    @Parameter(name = "id", description = "订单编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('trade:order:query')")
    public CommonResult<TradeOrderDetailRespVO> getOrderDetail(@RequestParam("id") Long id) {
        // 查询订单
        TradeOrderDO order = tradeOrderQueryService.getOrder(id);
        if (order == null) {
            return success(null);
        }
        // 查询订单项
        List<TradeOrderItemDO> orderItems = tradeOrderQueryService.getOrderItemListByOrderId(id);

        // 拼接数据
        AdminUserDO user = adminUserService.getUser(order.getUserId());
        AdminUserDO brokerageUser = order.getBrokerageUserId() != null ?
                adminUserService.getUser(order.getBrokerageUserId()) : null;
        List<TradeOrderLogDO> orderLogs = tradeOrderLogService.getOrderLogListByOrderId(id);
        return success(TradeOrderConvert.INSTANCE.convert(order, orderItems, orderLogs, user, brokerageUser));
    }

    @GetMapping("/get-express-track-list")
    @Operation(summary = "获得交易订单的物流轨迹")
    @Parameter(name = "id", description = "交易订单编号")
    @PreAuthorize("@ss.hasPermission('trade:order:query')")
    public CommonResult<List<?>> getOrderExpressTrackList(@RequestParam("id") Long id) {
        return success(TradeOrderConvert.INSTANCE.convertList02(
                tradeOrderQueryService.getExpressTrackList(id)));
    }

    @PutMapping("/delivery")
    @Operation(summary = "订单发货")
    @PreAuthorize("@ss.hasPermission('trade:order:update')")
    public CommonResult<Boolean> deliveryOrder(@RequestBody TradeOrderDeliveryReqVO deliveryReqVO) {
        tradeOrderUpdateService.deliveryOrder(deliveryReqVO);
        return success(true);
    }

    @PutMapping("/update-remark")
    @Operation(summary = "订单备注")
    @PreAuthorize("@ss.hasPermission('trade:order:update')")
    public CommonResult<Boolean> updateOrderRemark(@RequestBody TradeOrderRemarkReqVO reqVO) {
        tradeOrderUpdateService.updateOrderRemark(reqVO);
        return success(true);
    }

    @PutMapping("/update-price")
    @Operation(summary = "订单调价")
    @PreAuthorize("@ss.hasPermission('trade:order:update')")
    public CommonResult<Boolean> updateOrderPrice(@RequestBody TradeOrderUpdatePriceReqVO reqVO) {
        tradeOrderUpdateService.updateOrderPrice(reqVO);
        return success(true);
    }

    @PutMapping("/update-address")
    @Operation(summary = "修改订单收货地址")
    @PreAuthorize("@ss.hasPermission('trade:order:update')")
    public CommonResult<Boolean> updateOrderAddress(@RequestBody TradeOrderUpdateAddressReqVO reqVO) {
        tradeOrderUpdateService.updateOrderAddress(reqVO);
        return success(true);
    }

    @PutMapping("/pick-up-by-id")
    @Operation(summary = "订单核销")
    @Parameter(name = "id", description = "交易订单编号")
    @PreAuthorize("@ss.hasPermission('trade:order:pick-up')")
    public CommonResult<Boolean> pickUpOrderById(@RequestParam("id") Long id) {
        tradeOrderUpdateService.pickUpOrderByAdmin(id);
        return success(true);
    }

    @PutMapping("/pick-up-by-verify-code")
    @Operation(summary = "订单核销")
    @Parameter(name = "pickUpVerifyCode", description = "自提核销码")
    @PreAuthorize("@ss.hasPermission('trade:order:pick-up')")
    public CommonResult<Boolean> pickUpOrderByVerifyCode(@RequestParam("pickUpVerifyCode") String pickUpVerifyCode) {
        tradeOrderUpdateService.pickUpOrderByAdmin(pickUpVerifyCode);
        return success(true);
    }

    @GetMapping("/get-by-pick-up-verify-code")
    @Operation(summary = "查询核销码对应的订单")
    @Parameter(name = "pickUpVerifyCode", description = "自提核销码")
    @PreAuthorize("@ss.hasPermission('trade:order:query')")
    public CommonResult<TradeOrderDetailRespVO> getByPickUpVerifyCode(@RequestParam("pickUpVerifyCode") String pickUpVerifyCode) {
        TradeOrderDO tradeOrder = tradeOrderUpdateService.getByPickUpVerifyCode(pickUpVerifyCode);
        return success(TradeOrderConvert.INSTANCE.convert2(tradeOrder, null));
    }

    //========================APP======ADMIN=====USER=========================

    @GetMapping("/u/settlement")
    @Operation(summary = "第一步-获得订单结算信息")
    @PreAuthenticated
    public CommonResult<AppTradeOrderSettlementRespVO> settlementOrder(@Valid AppTradeOrderSettlementReqVO settlementReqVO) {
        return success(tradeOrderUpdateService.settlementOrder(getLoginUserId(), settlementReqVO));
    }

    @PostMapping("/u/create")
    @Operation(summary = "创建订单")
    @PreAuthenticated
    public CommonResult<AppTradeOrderCreateRespVO> createOrder(@Valid @RequestBody AppTradeOrderCreateReqVO createReqVO,
                                                               @RequestHeader Integer terminal) {
        TradeOrderDO order = tradeOrderUpdateService.createOrder(getLoginUserId(), getClientIP(), createReqVO, terminal);
        return success(new AppTradeOrderCreateRespVO().setId(order.getId()).setPayOrderId(order.getPayOrderId()));
    }

    @PostMapping("/u/update-paid")
    @Operation(summary = "用户-更新订单为已支付") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
    @PermitAll // 无需登录，安全由 PayDemoOrderService 内部校验实现
    @OperateLog(enable = false) // 禁用操作日志，因为没有操作人
    public CommonResult<Boolean> updateOrderPaid(@RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
        tradeOrderUpdateService.updateOrderPaid(Long.valueOf(notifyReqDTO.getMerchantOrderId()),
                notifyReqDTO.getPayOrderId());
        return success(true);
    }

    @GetMapping("/u/get-detail")
    @Operation(summary = "用户-获得交易订单")
    @Parameter(name = "id", description = "交易订单编号")
    public CommonResult<AppTradeOrderDetailRespVO> getOrder(@RequestParam("id") Long id) {
        // 查询订单
        TradeOrderDO order = tradeOrderQueryService.getOrder(getLoginUserId(), id);
        if (order == null) {
            return success(null);
        }

        // 查询订单项
        List<TradeOrderItemDO> orderItems = tradeOrderQueryService.getOrderItemListByOrderId(order.getId());
        // 查询物流公司
        DeliveryExpressDO express = order.getLogisticsId() != null && order.getLogisticsId() > 0 ?
                deliveryExpressService.getDeliveryExpress(order.getLogisticsId()) : null;
        // 最终组合
        return success(TradeOrderConvert.INSTANCE.convert02(order, orderItems, tradeOrderProperties, express));
    }

//    @GetMapping("/u/get-express-track-list")
//    @Operation(summary = "获得交易订单的物流轨迹")
//    @Parameter(name = "id", description = "交易订单编号")
//    public CommonResult<List<?>> getOrderExpressTrackList(@RequestParam("id") Long id) {
//        return success(TradeOrderConvert.INSTANCE.convertList02(
//                tradeOrderQueryService.getExpressTrackList(id, getLoginUserId())));
//    }

    @GetMapping("/u/page")
    @Operation(summary = "用户-获得交易订单分页")
    public CommonResult<PageResult<AppTradeOrderPageItemRespVO>> getOrderPage(AppTradeOrderPageReqVO reqVO) {
        // 查询订单
        PageResult<TradeOrderDO> pageResult = tradeOrderQueryService.getOrderPage(getLoginUserId(), reqVO);
        // 查询订单项
        List<TradeOrderItemDO> orderItems = tradeOrderQueryService.getOrderItemListByOrderId(
                convertSet(pageResult.getList(), TradeOrderDO::getId));
        // 最终组合
        return success(TradeOrderConvert.INSTANCE.convertPage02(pageResult, orderItems));
    }

    @GetMapping("/u/get-count")
    @Operation(summary = "用户-获得交易订单数量")
    public CommonResult<Map<String, Long>> getOrderCount() {
        Map<String, Long> orderCount = Maps.newLinkedHashMapWithExpectedSize(5);
        // 全部
        orderCount.put("allCount", tradeOrderQueryService.getOrderCount(getLoginUserId(), null, null));
        // 待付款（未支付）
        orderCount.put("unpaidCount", tradeOrderQueryService.getOrderCount(getLoginUserId(),
                TradeOrderStatusEnum.UNPAID.getStatus(), null));
        // 待发货
        orderCount.put("undeliveredCount", tradeOrderQueryService.getOrderCount(getLoginUserId(),
                TradeOrderStatusEnum.UNDELIVERED.getStatus(), null));
        // 待收货
        orderCount.put("deliveredCount", tradeOrderQueryService.getOrderCount(getLoginUserId(),
                TradeOrderStatusEnum.DELIVERED.getStatus(), null));
        // 待评价
        orderCount.put("uncommentedCount", tradeOrderQueryService.getOrderCount(getLoginUserId(),
                TradeOrderStatusEnum.COMPLETED.getStatus(), false));
        return success(orderCount);
    }

    @PutMapping("/u/receive")
    @Operation(summary = "用户-确认交易订单收货")
    @Parameter(name = "id", description = "交易订单编号")
    public CommonResult<Boolean> receiveOrder(@RequestParam("id") Long id) {
        tradeOrderUpdateService.receiveOrderByMember(getLoginUserId(), id);
        return success(true);
    }

    @DeleteMapping("/u/cancel")
    @Operation(summary = "用户-取消交易订单")
    @Parameter(name = "id", description = "交易订单编号")
    public CommonResult<Boolean> cancelOrder(@RequestParam("id") Long id) {
        tradeOrderUpdateService.cancelOrderByMember(getLoginUserId(), id);
        return success(true);
    }

    @DeleteMapping("/u/delete")
    @Operation(summary = "用户-删除交易订单")
    @Parameter(name = "id", description = "交易订单编号")
    public CommonResult<Boolean> deleteOrder(@RequestParam("id") Long id) {
        tradeOrderUpdateService.deleteOrder(getLoginUserId(), id);
        return success(true);
    }

    // ========== 订单项 ==========

    @GetMapping("/u/item/get")
    @Operation(summary = "用户-获得交易订单项")
    @Parameter(name = "id", description = "交易订单项编号")
    public CommonResult<AppTradeOrderItemRespVO> getOrderItem(@RequestParam("id") Long id) {
        TradeOrderItemDO item = tradeOrderQueryService.getOrderItem(getLoginUserId(), id);
        return success(TradeOrderConvert.INSTANCE.convert03(item));
    }

    @PostMapping("/u/item/create-comment")
    @Operation(summary = "用户-创建交易订单项的评价")
    public CommonResult<Long> createOrderItemComment(@RequestBody AppTradeOrderItemCommentCreateReqVO createReqVO) {
        return success(tradeOrderUpdateService.createOrderItemCommentByMember(getLoginUserId(), createReqVO));
    }


}
