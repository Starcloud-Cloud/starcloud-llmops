package com.starcloud.ops.business.trade.service.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.module.pay.api.order.PayOrderApi;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.system.api.permission.RoleApi;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.api.tenant.TenantApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderPageReqVO;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderSummaryRespVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderPageReqVO;
import com.starcloud.ops.business.trade.dal.dataobject.delivery.DeliveryExpressDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;
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
import com.starcloud.ops.business.trade.service.sign.TradeSignUpdateService;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum.getChineseName;
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
    private AdminUserService adminUserService;

    @Resource
    private TradeOrderMapper tradeOrderMapper;

    @Resource
    private TradeOrderItemMapper tradeOrderItemMapper;


    @Resource
    private List<TradeOrderHandler> tradeOrderHandlers;


    // ===========Notify==================

    @Resource
    private RoleApi roleApi;

    @Resource
    private TenantApi tenantApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private PayOrderApi payOrderApi;

    @Resource
    private CouponApi couponApi;

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private DingTalkNoticeProperties dingTalkNoticeProperties;

    @Resource
    private TradeSignUpdateService tradeSignUpdateService;

    // =================== 权益 ===================
    @Resource
    private AdminUserRightsService adminUserRightsService;

    @Resource
    private AdminUserLevelService adminUserLevelService;

    // 假设以下两个为定义的常量
    private static final String COUPON_NAME_ERROR = "优惠券获取失败";


    private final String SignNotifyTemplate = ">- 第{}次订单<br/>" + "创建时间：{} <br/>\n" + "    扣款时间：{}<br/>\n" + "    权益有效期：{} <br/>\n" + "    等级有效期：{}<br/>\n" + "    扣款状态：{} <br/>\n";


    private final String EveryDayNotifyTemplate = " >- 会员昵称:{userName}\n" + ">- 商品名称:{productName}\n" + ">- 商品属性:{productType}\n" + ">- 购买时长:{purchaseDuration}\n" + ">- 商品原价:{totalPrice} 元\n" + ">- 优惠金额:{discountPrice} 元\n" + ">- 优惠券名称:{couponName}\n" + ">- 支付金额:{payPrice} 元\n" + ">- 订单创建时间:{createTime}\n" + ">- 用户支付时间:{payTime}\n" + ">- 支付回调时间:{payNotifyTime}\n" + ">- 支付渠道:{payChannelCode}\n" + ">***\n" + ">- 会员等级:{userLevelName}\n" + ">- 会员角色:{userRole}\n" + ">- 魔法豆:{magicBeanCount}\n" + ">- 图  片:{magicImage}\n" + ">- 矩阵豆:{matrixBean}\n" + ">- 权益有效期:<br/>{userRightTimeRange}\n" + ">- 等级有效期:<br/>{userLevelTimeRange}\n" + ">- 数据校验结果:{checkResult}\n" + ">***\n" + ">- 是否签约:{isSign}\n" + ">- 履约次数:{successCount}\n" + ">- 首次签约时间:{firstSignTime}\n" + ">- 下次预计扣款时间:{nextPayData}\n" + ">***\n" + "{signTradeOrderDetail}\n" + ">***\n";


    // =================== Order ===================

    @Override
    public TradeOrderDO getOrder(Long id) {
        return tradeOrderMapper.selectById(id);
    }

    @Override
    public TradeOrderDO getOrder(Long userId, Long id) {
        TradeOrderDO order = tradeOrderMapper.selectById(id);
        if (order != null && ObjectUtil.notEqual(order.getUserId(), userId)) {
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
    @Cacheable(cacheNames = RedisKeyConstants.EXPRESS_TRACK, key = "#code + '-' + #logisticsNo + '-' + #receiverMobile", condition = "#result != null")
    public List<ExpressTrackRespDTO> getExpressTrackList(String code, String logisticsNo, String receiverMobile) {
        // 查询物流轨迹
        return expressClientFactory.getDefaultExpressClient().getExpressTrackList(new ExpressTrackQueryReqDTO().setExpressCode(code).setLogisticsNo(logisticsNo).setPhone(receiverMobile));
    }


    // =================== Order Item ===================

    @Override
    public TradeOrderItemDO getOrderItem(Long userId, Long itemId) {
        TradeOrderItemDO orderItem = tradeOrderItemMapper.selectById(itemId);
        if (orderItem != null && ObjectUtil.notEqual(orderItem.getUserId(), userId)) {
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
        StringBuilder msg = new StringBuilder();

        // 获取当前运行环境
        String environmentName = dingTalkNoticeProperties.getName().equals("Formal") ? "正式" : "测试";
        // 获取当前环境
        tradeOrderDOS.forEach(tradeOrderDO -> {
            List<TradeOrderItemDO> tradeOrderItemDOS = tradeOrderItemMapper.selectListByOrderId(tradeOrderDO.getId());
            Map<String, Object> msgParams = buildTradeNotifyMsg(tradeOrderDO, tradeOrderItemDOS);
            msg.append(StrUtil.format(EveryDayNotifyTemplate, msgParams));
        });
        // msg.append(">##### {from} 发布 [支付提醒](https://www.mofaai.com.cn/)");
        smsSendApi.sendSingleSmsToAdmin(new SmsSendSingleToUserReqDTO().setUserId(1L).setMobile("17835411844").setTemplateCode("TRADE_NOTIFY").setTemplateParams(MapUtil.<String, Object>builder().put("params", msg).put("environmentName", environmentName).build()));
        return tradeOrderDOS.size();
    }


    @Override
    public Map<String, Object> buildTradeNotifyMsg(TradeOrderDO tradeOrderDO, List<TradeOrderItemDO> orderItems) {
        log.info("【开始构建订单通知参数，订单为:{},{}】", tradeOrderDO, orderItems);

        // 优化
        // 1.避免 templateParams的 key 为空
        // 2.每个参数都设置默认值 如果获取失败 使用默认值代替

        // 设置通知信息参数
        HashMap<String, Object> templateParams = MapUtil.newHashMap();
        try {
            PayOrderRespDTO payOrderRespDTO = payOrderApi.getOrder(tradeOrderDO.getPayOrderId());
            // 获取当前用户基本信息
            AdminUserRespDTO userRespDTO = adminUserApi.getUser(tradeOrderDO.getUserId());
            // 获取当前运行环境
            String environmentName = getEnvironmentName();
            // 获取当前优惠券
            String couponName = "无";
            if (Objects.nonNull(tradeOrderDO.getCouponId())) {
                couponName = getCouponName(tradeOrderDO);
            }

            // 如果是签约订单 则更新下次扣款时间 并且获取履约次数
            int count = 0;
            boolean signTag = false;
            // 设置历史订单信息

            StringBuilder signTradeOrderDetail = new StringBuilder();

            String firstSignTime = "无";
            String nextPayData = "无";
            // =======用户签约订单获取=======
            if (Objects.nonNull(tradeOrderDO.getTradeSignId())) {
                try {
                    signTag = true;
                    TradeSignDO tradeSignDO = tradeSignUpdateService.updatePayTime(tradeOrderDO.getTradeSignId());
                    // 获取当前签约成功次数
                    List<TradeOrderDO> signPayTradeList = getSelf().getSignPayTradeList(tradeOrderDO.getTradeSignId());
                    count = signPayTradeList.size();

                    firstSignTime = LocalDateTimeUtil.formatNormal(tradeSignDO.getFinishTime());
                    nextPayData = LocalDateTimeUtil.formatNormal(tradeSignDO.getPayTime());

                    signPayTradeList.forEach(forEachWithIndex((item, index) -> {
                        // 通过业务 获取权益记录
                        AdminUserRightsDO rights = adminUserRightsService.getRecordByBiz(AdminUserRightsBizTypeEnum.ORDER_GIVE.getType(), item.getId(), item.getUserId());
                        String userRangeTimeRange = StrUtil.format("{}至{}", LocalDateTimeUtil.formatNormal(rights.getValidStartTime()), LocalDateTimeUtil.formatNormal(rights.getValidEndTime()));

                        // 通过业务 获取等级记录
                        AdminUserLevelDO level = adminUserLevelService.getRecordByBiz(AdminUserRightsBizTypeEnum.ORDER_GIVE.getType(), item.getId(), item.getUserId());
                        String userLevelTimeRange = StrUtil.format("{}至{}", LocalDateTimeUtil.formatNormal(level.getValidStartTime()), LocalDateTimeUtil.formatNormal(level.getValidEndTime()));

                        signTradeOrderDetail.append(StrUtil.format(SignNotifyTemplate, signPayTradeList.size() - index + 1, item.getCreateTime(), item.getPayTime(), userRangeTimeRange, userLevelTimeRange, item.getPayStatus() ? "完成✅" : "错误❌"));
                    }));
                } catch (RuntimeException e) {
                    log.error("签约明细获取失败");
                    signTradeOrderDetail.append("签约明细获取失败");
                }
            }

            AdminUserRightsAndLevelCommonDTO commonDTO = tradeOrderDO.getGiveRights().get(0);

            AdminUserRightsDO rights = null;
            String userRangeTimeRange = "无";
            // 获取当前增加的权益
            if (commonDTO.getRightsBasicDTO().getOperateDTO().getIsAdd()) {
                // 通过业务 获取权益记录
                rights = adminUserRightsService.getRecordByBiz(AdminUserRightsBizTypeEnum.ORDER_GIVE.getType(), tradeOrderDO.getId(), tradeOrderDO.getUserId());
                userRangeTimeRange = StrUtil.format("{}至{}", LocalDateTimeUtil.formatNormal(rights.getValidStartTime()), LocalDateTimeUtil.formatNormal(rights.getValidEndTime()));
            }

            // =======用户当前订单的等级获取=======
            AdminUserLevelDO level = null;
            String userLevelName = "无";
            String userLevelTimeRange = "无";
            // 获取当前增加的用户等级
            try {
                if (commonDTO.getLevelBasicDTO().getOperateDTO().getIsAdd()) {
                    // 通过业务 获取等级记录
                    level = adminUserLevelService.getRecordByBiz(AdminUserRightsBizTypeEnum.ORDER_GIVE.getType(), tradeOrderDO.getId(), tradeOrderDO.getUserId());
                    userLevelName = level.getLevelName();
                    userLevelTimeRange = StrUtil.format("{}至{}", LocalDateTimeUtil.formatNormal(level.getValidStartTime()), LocalDateTimeUtil.formatNormal(level.getValidEndTime()));
                }
            } catch (Exception e) {
                level = null;
                userLevelName = "获取异常";
                userLevelTimeRange = "获取异常";
            }

            // =======获取当前会员所有角色 =======
            String userRoleName = "";
            try {
                List<String> roleNameList = roleApi.getRoleNameList(tradeOrderDO.getUserId());

                if (!roleNameList.isEmpty()) {
                    userRoleName = CollUtil.join(roleNameList, ",");
                }
            } catch (RuntimeException e) {
                userRoleName = "用户角色异常";
            }


            // 开始权益和等级有效期检测
            Boolean checkResult = false;
            if (Objects.nonNull(rights) && Objects.nonNull(level)) {
                // 获取当前权益检查结果
                checkResult = adminUserLevelService.checkLevelAndRights(level, rights);
            }


            // 当前运行环境
            templateParams.put("environmentName", environmentName);
            // 用户昵称
            templateParams.put("userName", Optional.ofNullable(userRespDTO.getNickname()));
            // 产品名称
            templateParams.put("productName", orderItems.get(0).getSpuName());
            // 商品属性
            templateParams.put("productType", orderItems.get(0).getProperties().get(0).getValueName());
            // 购买时长
            templateParams.put("purchaseDuration", tradeOrderDO.getGiveRights().get(0).getRightsBasicDTO().getTimesRange().getNums() + getChineseName(tradeOrderDO.getGiveRights().get(0).getRightsBasicDTO().getTimesRange().getRange()));
            // 原价
            templateParams.put("totalPrice", MoneyUtils.fenToYuanStr(tradeOrderDO.getTotalPrice()));
            // 优惠金额
            templateParams.put("discountPrice", MoneyUtils.fenToYuanStr(tradeOrderDO.getCouponPrice() + tradeOrderDO.getDiscountPrice()));
            // 优惠券名称
            templateParams.put("couponName", couponName);
            // 实际支付金额
            templateParams.put("payPrice", MoneyUtils.fenToYuanStr(tradeOrderDO.getPayPrice()));
            // 订单创建时间
            templateParams.put("createTime", LocalDateTimeUtil.formatNormal(tradeOrderDO.getCreateTime()));
            // 支付时间
            templateParams.put("payTime", LocalDateTimeUtil.formatNormal(payOrderRespDTO == null ? LocalDateTime.now() : payOrderRespDTO.getSuccessTime()));
            // 支付回调时间
            templateParams.put("payNotifyTime", LocalDateTimeUtil.formatNormal(tradeOrderDO.getPayTime() == null ? LocalDateTime.now() : tradeOrderDO.getPayTime()));
            // 支付来源
            templateParams.put("payChannelCode", tradeOrderDO.getPayChannelCode() == null ? "未知渠道" : tradeOrderDO.getPayChannelCode().startsWith("alipay") ? "支付宝" : "微信");

            // 会员等级
            templateParams.put("userLevelName", userLevelName);
            // 会员角色
            templateParams.put("userRole", userRoleName);

            // 魔法豆
            templateParams.put("magicBeanCount", tradeOrderDO.getGiveRights().get(0).getRightsBasicDTO().getMagicBean());
            // 图  片
            templateParams.put("magicImage", tradeOrderDO.getGiveRights().get(0).getRightsBasicDTO().getMagicImage());
            // 矩阵豆
            templateParams.put("matrixBean", tradeOrderDO.getGiveRights().get(0).getRightsBasicDTO().getMatrixBean());


            // 权益有效时间段
            templateParams.put("userRightTimeRange", userRangeTimeRange);
            // 等级有效时间段
            templateParams.put("userLevelTimeRange", userLevelTimeRange);
            // 权益与等级时间校验
            templateParams.put("checkResult", Objects.isNull(rights) || Objects.isNull(level) ? "成功✅" : checkResult ? "成功✅" : "失败❌");

            // 是否签约
            templateParams.put("isSign", Objects.nonNull(tradeOrderDO.getTradeSignId()) ? "是✅" : "否⭕");
            // 签约次数
            templateParams.put("successCount", count);
            // 首次签约时间
            templateParams.put("firstSignTime", firstSignTime);
            // 下次预计扣款时间
            templateParams.put("nextPayData", nextPayData);

            if (signTag) {
                templateParams.put("signTradeOrderDetail", signTradeOrderDetail);
            } else {
                templateParams.put("signTradeOrderDetail", "无");
            }
            // 所属系统 魔法 AI / 魔法矩阵
            templateParams.put("from", tenantApi.getTenantById(tradeOrderDO.getTenantId()).getContactName());

            return templateParams;
        } catch (Exception e) {
            log.error("【构建订单通知信息,错误原因为 errMsg{},当前订单为{}】", e.getMessage(), JSONUtil.toJsonStr(tradeOrderDO), e);
            return templateParams;
        }

    }


    private String getEnvironmentName() {
        try {
            // 获取当前运行环境
            return dingTalkNoticeProperties.getName().equals("Formal") ? "正式" : "测试";
        } catch (RuntimeException e) {
            log.error("当前运行环境获取失败", e);
            return "获取环境失败";
        }

    }


    private String getCouponName(TradeOrderDO tradeOrderDO) {
        if (Objects.nonNull(tradeOrderDO.getCouponId())) {
            try {
                return couponApi.getCoupon(tradeOrderDO.getCouponId(), tradeOrderDO.getUserId()).getName();
            } catch (RuntimeException e) {
                log.error("优惠券获取失败", e);
                return COUPON_NAME_ERROR;
            }
        }
        return "无";
    }

    /**
     * 利用BiConsumer实现foreach循环支持index
     *
     * @param biConsumer biConsumer
     * @param <T>        T
     * @return T
     */
    private static <T> Consumer<T> forEachWithIndex(BiConsumer<T, Integer> biConsumer) {
        /*这里说明一下，我们每次传入forEach都是一个重新实例化的Consumer对象，在lambada表达式中我们无法对int进行++操作,
        我们模拟AtomicInteger对象，写个getAndIncrement方法，不能直接使用AtomicInteger哦*/
        class IncrementInt {
            int i = 0;

            public int getAndIncrement() {
                return i++;
            }
        }
        IncrementInt incrementInt = new IncrementInt();
        return t -> biConsumer.accept(t, incrementInt.getAndIncrement());

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
