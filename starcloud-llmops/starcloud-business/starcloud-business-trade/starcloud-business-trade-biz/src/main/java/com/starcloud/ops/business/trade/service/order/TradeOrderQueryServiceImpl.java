package com.starcloud.ops.business.trade.service.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.pay.core.enums.channel.PayChannelEnum;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderPageReqVO;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderSummaryRespVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderPageReqVO;
import com.starcloud.ops.business.trade.dal.dataobject.delivery.DeliveryExpressDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.dal.mysql.order.TradeOrderItemMapper;
import com.starcloud.ops.business.trade.dal.mysql.order.TradeOrderMapper;
import com.starcloud.ops.business.trade.dal.redis.RedisKeyConstants;
import com.starcloud.ops.business.trade.enums.order.TradeOrderRefundStatusEnum;
import com.starcloud.ops.business.trade.enums.order.TradeOrderStatusEnum;
import com.starcloud.ops.business.trade.framework.delivery.core.client.ExpressClientFactory;
import com.starcloud.ops.business.trade.framework.delivery.core.client.dto.ExpressTrackQueryReqDTO;
import com.starcloud.ops.business.trade.framework.delivery.core.client.dto.ExpressTrackRespDTO;
import com.starcloud.ops.business.trade.service.delivery.DeliveryExpressService;
import com.starcloud.ops.business.trade.service.order.handler.TradeOrderHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static cn.hutool.core.date.DatePattern.CHINESE_DATE_TIME_PATTERN;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static com.starcloud.ops.business.trade.enums.ErrorCodeConstants.EXPRESS_NOT_EXISTS;
import static com.starcloud.ops.business.trade.enums.ErrorCodeConstants.ORDER_NOT_FOUND;

/**
 * 交易订单【读】 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
public class TradeOrderQueryServiceImpl implements TradeOrderQueryService {

    @Resource
    private ExpressClientFactory expressClientFactory;

    @Resource
    private DeliveryExpressService deliveryExpressService;

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private TradeOrderMapper tradeOrderMapper;
    @Resource
    private TradeOrderItemMapper tradeOrderItemMapper;


    @Resource
    private List<TradeOrderHandler> tradeOrderHandlers;


    // =================== Order ===================

    @Override
    public TradeOrderDO getOrder(Long id) {
        return tradeOrderMapper.selectById(id);
    }

    @Override
    public TradeOrderDO getOrder(Long userId, Long id) {
        TradeOrderDO order = tradeOrderMapper.selectById(id);
        if (order != null
                && ObjectUtil.notEqual(order.getUserId(), userId)) {
            return null;
        }
        return order;
    }

    @Override
    public TradeOrderDO getOrderByUserIdAndStatusAndCombination(Long userId, Long combinationActivityId, Integer status) {
        return tradeOrderMapper.selectByUserIdAndCombinationActivityIdAndStatus(userId, combinationActivityId, status);
    }

    @Override
    public List<TradeOrderDO> getOrderList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return tradeOrderMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<TradeOrderDO> getOrderPage(TradeOrderPageReqVO reqVO) {
        // 根据用户查询条件构建用户编号列表
        Set<Long> userIds = buildQueryConditionUserIds(reqVO);
        if (userIds == null) { // 没查询到用户，说明肯定也没他的订单
            return PageResult.empty();
        }
        // 分页查询
        return tradeOrderMapper.selectPage(reqVO, userIds);
    }

    private Set<Long> buildQueryConditionUserIds(TradeOrderPageReqVO reqVO) {
        // 获得 userId 相关的查询
        Set<Long> userIds = new HashSet<>();
        if (StrUtil.isNotEmpty(reqVO.getUserMobile())) {
            AdminUserDO user = adminUserService.getUserByMobile(reqVO.getUserMobile());
            if (user == null) { // 没查询到用户，说明肯定也没他的订单
                return null;
            }
            userIds.add(user.getId());
        }
        if (StrUtil.isNotEmpty(reqVO.getUserNickname())) {
            List<AdminUserDO> users = adminUserService.getUserListByNickname(reqVO.getUserNickname());
            if (CollUtil.isEmpty(users)) { // 没查询到用户，说明肯定也没他的订单
                return null;
            }
            userIds.addAll(convertSet(users, AdminUserDO::getId));
        }
        return userIds;
    }

    @Override
    public TradeOrderSummaryRespVO getOrderSummary(TradeOrderPageReqVO reqVO) {
        // 根据用户查询条件构建用户编号列表
        Set<Long> userIds = buildQueryConditionUserIds(reqVO);
        if (userIds == null) { // 没查询到用户，说明肯定也没他的订单
            return new TradeOrderSummaryRespVO();
        }
        // 查询每个售后状态对应的数量、金额
        List<Map<String, Object>> list = tradeOrderMapper.selectOrderSummaryGroupByRefundStatus(reqVO, null);

        TradeOrderSummaryRespVO vo = new TradeOrderSummaryRespVO().setAfterSaleCount(0L).setAfterSalePrice(0L);
        for (Map<String, Object> map : list) {
            Long count = MapUtil.getLong(map, "count", 0L);
            Long price = MapUtil.getLong(map, "price", 0L);
            // 未退款的计入订单，部分退款、全部退款计入售后
            if (TradeOrderRefundStatusEnum.NONE.getStatus().equals(MapUtil.getInt(map, "refundStatus"))) {
                vo.setOrderCount(count).setOrderPayPrice(price);
            } else {
                vo.setAfterSaleCount(vo.getAfterSaleCount() + count).setAfterSalePrice(vo.getAfterSalePrice() + price);
            }
        }
        return vo;
    }

    @Override
    public PageResult<TradeOrderDO> getOrderPage(Long userId, AppTradeOrderPageReqVO reqVO) {
        return tradeOrderMapper.selectPage(reqVO, userId);
    }

    @Override
    public Long getOrderCount(Long userId, Integer status, Boolean commentStatus) {
        return tradeOrderMapper.selectCountByUserIdAndStatus(userId, status, commentStatus);
    }

    @Override
    public List<ExpressTrackRespDTO> getExpressTrackList(Long id, Long userId) {
        // 查询订单
        TradeOrderDO order = tradeOrderMapper.selectByIdAndUserId(id, userId);
        if (order == null) {
            throw exception(ORDER_NOT_FOUND);
        }
        // 查询物流
        return getExpressTrackList(order);
    }

    @Override
    public List<ExpressTrackRespDTO> getExpressTrackList(Long id) {
        // 查询订单
        TradeOrderDO order = tradeOrderMapper.selectById(id);
        if (order == null) {
            throw exception(ORDER_NOT_FOUND);
        }
        // 查询物流
        return getExpressTrackList(order);
    }

    @Override
    public int getSeckillProductCount(Long userId, Long activityId) {
        // 获得订单列表
        List<TradeOrderDO> orders = tradeOrderMapper.selectListByUserIdAndSeckillActivityId(userId, activityId);
        orders.removeIf(order -> TradeOrderStatusEnum.isCanceled(order.getStatus())); // 过滤掉【已取消】的订单
        if (CollUtil.isEmpty(orders)) {
            return 0;
        }
        // 获得订单项列表
        return tradeOrderItemMapper.selectProductSumByOrderId(convertSet(orders, TradeOrderDO::getId));
    }

    /**
     * 获得订单的物流轨迹
     *
     * @param order 订单
     * @return 物流轨迹
     */
    private List<ExpressTrackRespDTO> getExpressTrackList(TradeOrderDO order) {
        if (order.getLogisticsId() == null) {
            return Collections.emptyList();
        }
        // 查询物流公司
        DeliveryExpressDO express = deliveryExpressService.getDeliveryExpress(order.getLogisticsId());
        if (express == null) {
            throw exception(EXPRESS_NOT_EXISTS);
        }
        // 查询物流轨迹
        return getSelf().getExpressTrackList(express.getCode(), order.getLogisticsNo(), order.getReceiverMobile());
    }

    /**
     * 查询物流轨迹
     * 加个 spring 缓存，30 分钟；主要考虑及时性要求不高，但是每次调用需要钱；TODO @艿艿：这个时间不会搞了。。。交给你了哈哈哈
     *
     * @param code           快递公司编码
     * @param logisticsNo    发货快递单号
     * @param receiverMobile 收、寄件人的电话号码
     * @return 物流轨迹
     */
    @Cacheable(cacheNames = RedisKeyConstants.EXPRESS_TRACK, key = "#code + '-' + #logisticsNo + '-' + #receiverMobile",
            condition = "#result != null")
    public List<ExpressTrackRespDTO> getExpressTrackList(String code, String logisticsNo, String receiverMobile) {
        // 查询物流轨迹
        return expressClientFactory.getDefaultExpressClient().getExpressTrackList(
                new ExpressTrackQueryReqDTO().setExpressCode(code).setLogisticsNo(logisticsNo)
                        .setPhone(receiverMobile));
    }


    // =================== Order Item ===================

    @Override
    public TradeOrderItemDO getOrderItem(Long userId, Long itemId) {
        TradeOrderItemDO orderItem = tradeOrderItemMapper.selectById(itemId);
        if (orderItem != null
                && ObjectUtil.notEqual(orderItem.getUserId(), userId)) {
            return null;
        }
        return orderItem;
    }

    @Override
    public TradeOrderItemDO getOrderItem(Long id) {
        return tradeOrderItemMapper.selectById(id);
    }

    @Override
    public List<TradeOrderItemDO> getOrderItemListByOrderId(Collection<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return Collections.emptyList();
        }
        return tradeOrderItemMapper.selectListByOrderId(orderIds);
    }

    /**
     * 【系统】获取签约周期下的订单
     *
     * @param signId      签约ID
     * @param signPayTime 签约预扣款时间
     * @return 物流轨迹数组
     */
    @Override
    public TradeOrderDO getOrderBySignPayTime(Long signId, LocalDate signPayTime) {
        return tradeOrderMapper.selectWithinContractPeriod(signId, LocalDateTimeUtil.endOfDay(signPayTime.atStartOfDay()));
    }

    public List<TradeOrderDO> getSignPayTradeList(Long signId) {
         return tradeOrderMapper.selectSucceedOrderBySignId(signId);
    }

    /**
     * 订单通知-查询指定timeNum 内的订单 发送到钉钉通知
     *
     * @param timeNum 指定天数内
     * @return 订单数
     */
    @Override
    public int orderAutoNotify(Long timeNum) {
        // 查询指定时间内的数据
        List<TradeOrderDO> tradeOrderDOS = tradeOrderMapper.queryTradeOrdersByTime(timeNum);

        if (tradeOrderDOS.isEmpty()) {
           return 0;
        }
        tradeOrderDOS.forEach(tradeOrderDO -> {
            List<TradeOrderItemDO> tradeOrderItemDOS = tradeOrderItemMapper.selectListByOrderId(tradeOrderDO.getId());
            tradeOrderHandlers.forEach(handler -> handler.afterPayOrderLast(tradeOrderDO, tradeOrderItemDOS));
        });
        return tradeOrderDOS.size();
    }

    private String buildMsg(List<TradeOrderDO> tradeOrderDOS) {
        StringBuilder stringBuilder = new StringBuilder();
        for (TradeOrderDO tradeOrderDO : tradeOrderDOS) {
            // 获取当前下单用户
            AdminUserDO user = adminUserService.getUser(tradeOrderDO.getUserId());
            List<TradeOrderItemDO> tradeOrderItemDOS = tradeOrderItemMapper.selectListByOrderId(tradeOrderDO.getId());
            // 拼接为 markdown 表格样式
            stringBuilder.append("| ").append(user.getUsername());
            stringBuilder.append(" |").append(tradeOrderItemDOS.get(0).getSpuName());
            stringBuilder.append(" |").append(tradeOrderItemDOS.get(0).getProperties().get(0).getValueName());
            stringBuilder.append(" |").append(tradeOrderDO.getPayStatus() ? "支付成功" : "未支付");
            stringBuilder.append(" |").append(PayChannelEnum.isAlipay(tradeOrderDO.getPayChannelCode()) ? "支付宝" : "微信");
            stringBuilder.append(" |").append(LocalDateTimeUtil.format(tradeOrderDO.getCreateTime(), CHINESE_DATE_TIME_PATTERN));
            stringBuilder.append(" |").append(LocalDateTimeUtil.format(tradeOrderDO.getPayTime(), CHINESE_DATE_TIME_PATTERN));
            stringBuilder.append(" |").append(LocalDateTimeUtil.between(tradeOrderDO.getCreateTime(), tradeOrderDO.getPayTime(), ChronoUnit.SECONDS)).append("s");
            stringBuilder.append(" |").append(MoneyUtils.fenToYuanStr(tradeOrderDO.getPayPrice()));
            stringBuilder.append(" |").append(tradeOrderDO.getTenantId()).append(" | ");
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @TenantIgnore
    private void sendNotifyMsg(String content) {
        try {
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("params", content);

            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                            .setTemplateCode("TRADE_NOTIFY")
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("订单通知信息发送失败", e);
        }
    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private TradeOrderQueryServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
