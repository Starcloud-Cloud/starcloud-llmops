package com.starcloud.ops.business.trade.service.order.handler;

import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.starcloud.ops.business.product.api.spu.dto.GiveRightsDTO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.user.api.level.AdminUserLevelApi;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.level.AdminUserLevelBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员积分、等级的 {@link TradeOrderHandler} 实现类
 *
 * @author owen
 */
@Component
public class TradeAdminUserRightsOrderHandler implements TradeOrderHandler {

    @Resource
    private AdminUserRightsApi adminUserRightsApi;
    @Resource
    private AdminUserLevelApi adminUserLevelApi;

    @Override
    public void afterPayOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {

        if (order.getGiveRights().size() == 0) {
            return;
        }

        // 设置会员等级
        for (GiveRightsDTO giveRight : order.getGiveRights()) {
            LocalDateTime validStartTime = LocalDateTime.now();
            LocalDateTime validEndTime;
            TimeRangeTypeEnum timeRangeTypeEnum = TimeRangeTypeEnum.getByType(giveRight.getTimeRange());
            switch (timeRangeTypeEnum) {
                case DAY:
                    validEndTime = validStartTime.plusDays(giveRight.getTimeNums());
                    break;
                case WEEK:
                    validEndTime = validStartTime.plusWeeks(giveRight.getTimeNums());
                    break;
                case MONTH:
                    validEndTime = validStartTime.plusMonths(giveRight.getTimeNums());
                    break;
                case YEAR:
                    validEndTime = validStartTime.plusYears(giveRight.getTimeNums());
                    break;
                default:
                    throw new RuntimeException("产品权益信息设置异常，请联系管理员");

            }

            adminUserLevelApi.addAdminUserLevel(
                    order.getUserId(),
                    giveRight.getLevelId(),
                    giveRight.getTimeNums(),
                    giveRight.getTimeRange(),
                    AdminUserLevelBizTypeEnum.ORDER_GIVE.getType(),
                    String.valueOf(order.getId()));
            // 设置会员权益
            adminUserRightsApi.addRights(
                    order.getUserId(),
                    giveRight.getGiveMagicBean(),
                    giveRight.getGiveImage(), validStartTime, validEndTime,
                    AdminUserRightsBizTypeEnum.ORDER_GIVE.getType(),
                    String.valueOf(order.getId()));
        }


    }

//    @Override
//    public void afterCancelOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
//        // 售后的订单项，已经在 afterCancelOrderItem 回滚库存，所以这里不需要重复回滚
//        orderItems = filterOrderItemListByNoneAfterSale(orderItems);
//        if (CollUtil.isEmpty(orderItems)) {
//            return;
//        }
//
//        // 增加（回滚）用户积分（订单抵扣）
//        Integer usePoint = getSumValue(orderItems, TradeOrderItemDO::getUsePoint, Integer::sum);
//        addPoint(order.getUserId(), usePoint, AdminUserRightsBizTypeEnum.ORDER_USE_CANCEL,
//                order.getId());
//
//        // 如下的返还，需要经过支持，也就是经历 afterPayOrder 流程
//        if (!order.getPayStatus()) {
//            return;
//        }
//        // 扣减（回滚）积分（订单赠送）
//        Integer givePoint = getSumValue(orderItems, TradeOrderItemDO::getGivePoint, Integer::sum);
//        reducePoint(order.getUserId(), givePoint, MemberPointBizTypeEnum.ORDER_GIVE_CANCEL,
//                order.getId());
//        // 扣减（回滚）用户经验
//        int payPrice = order.getPayPrice() - order.getRefundPrice();
//        adminUserLevelApi.addExperience(order.getUserId(), payPrice,
//                MemberExperienceBizTypeEnum.ORDER_GIVE_CANCEL.getType(), String.valueOf(order.getId()));
//    }
//
//    @Override
//    public void afterCancelOrderItem(TradeOrderDO order, TradeOrderItemDO orderItem) {
//        // 扣减（回滚）积分（订单赠送）
//        reducePoint(order.getUserId(), orderItem.getGivePoint(), MemberPointBizTypeEnum.ORDER_GIVE_CANCEL_ITEM,
//                orderItem.getId());
//        // 增加（回滚）积分（订单抵扣）
//        addPoint(order.getUserId(), orderItem.getUsePoint(), MemberPointBizTypeEnum.ORDER_USE_CANCEL_ITEM,
//                orderItem.getId());
//
//        // 扣减（回滚）用户经验
//        AfterSaleDO afterSale = afterSaleService.getAfterSale(orderItem.getAfterSaleId());
//        adminUserLevelApi.reduceExperience(order.getUserId(), afterSale.getRefundPrice(),
//                MemberExperienceBizTypeEnum.ORDER_GIVE_CANCEL_ITEM.getType(), String.valueOf(orderItem.getId()));
//    }
//
//    /**
//     * 添加用户积分
//     * <p>
//     * 目前是支付成功后，就会创建积分记录。
//     * <p>
//     * 业内还有两种做法，可以根据自己的业务调整：
//     * 1. 确认收货后，才创建积分记录
//     * 2. 支付 or 下单成功时，创建积分记录（冻结），确认收货解冻或者 n 天后解冻
//     *
//     * @param userId  用户编号
//     * @param magicBean   增加积分数量
//     * @param bizType 业务编号
//     * @param bizId   业务编号
//     */
//    protected void addPoint(Long userId, Integer magicBean, MemberPointBizTypeEnum bizType, Long bizId) {
//        if (magicBean != null && magicBean > 0) {
//            adminUserRightsApi.addRights(userId, magicBean, bizType.getType(), String.valueOf(bizId));
//        }
//    }
//
//    protected void reducePoint(Long userId, Integer magicBean, MemberPointBizTypeEnum bizType, Long bizId) {
//        if (magicBean != null && magicBean > 0) {
//            adminUserRightsApi.reduceRights(userId, magicBean, bizType.getType(), String.valueOf(bizId));
//        }
//    }

}