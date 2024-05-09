package com.starcloud.ops.business.trade.service.order.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.pay.core.enums.channel.PayChannelEnum;
import cn.iocoder.yudao.module.pay.api.order.PayOrderApi;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.system.api.permission.RoleApi;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.api.tenant.TenantApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;
import com.starcloud.ops.business.trade.service.order.TradeOrderQueryService;
import com.starcloud.ops.business.trade.service.sign.TradeSignUpdateService;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.level.AdminUserLevelConfigService;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum.getChineseName;

/**
 * 会员积分、等级的 {@link TradeOrderHandler} 实现类
 *
 * @author owen
 */
@Component
public class TradeNotifyOrderHandler implements TradeOrderHandler {

    private static final Logger log = LoggerFactory.getLogger(TradeAdminUserRightsOrderHandler.class);
    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private CouponApi couponApi;

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private TenantApi tenantApi;

    @Resource
    private RoleApi roleApi;

    @Resource
    private DingTalkNoticeProperties dingTalkNoticeProperties;


    @Resource
    private TradeSignUpdateService tradeSignUpdateService;

    @Resource
    private TradeOrderQueryService tradeOrderQueryService;

    @Resource
    private PayOrderApi payOrderApi;


    // =================== 权益 ===================
    @Resource
    private AdminUserRightsService adminUserRightsService;

    @Resource
    private AdminUserLevelService adminUserLevelService;

    @Resource
    private AdminUserLevelConfigService adminUserLevelConfigService;

    private final String SignNotifyTemplate = ">- 第{}次订单<br/>" +
            "创建时间：{} <br/>\n" +
            "    扣款时间：{}<br/>\n" +
            "    权益有效期：{} <br/>\n" +
            "    等级有效期：{}<br/>\n" +
            "    扣款状态：{} <br/>\n";


    @Override
    public void afterPayOrderLast(TradeOrderDO tradeOrderDO, List<TradeOrderItemDO> orderItems) {

        try {
            log.info("【存在新订单开始准备发送通知消息，参数为:{},{}】", tradeOrderDO, orderItems);
            PayOrderRespDTO payOrderRespDTO = payOrderApi.getOrder(tradeOrderDO.getPayOrderId());
            // 获取当前用户基本信息
            AdminUserRespDTO userRespDTO = adminUserApi.getUser(tradeOrderDO.getUserId());
            // 获取当前运行环境
            String environmentName = dingTalkNoticeProperties.getName().equals("Formal") ? "正式" : "测试";
            // 获取当前优惠券
            String couponName = "无";
            if (Objects.nonNull(tradeOrderDO.getCouponId())) {
                couponName = couponApi.getCoupon(tradeOrderDO.getUserId(), tradeOrderDO.getCouponId()).getName();
            }

            // 如果是签约订单 则更新下次扣款时间 并且获取履约次数
            int count = 0;
            boolean signTag = false;
            // 设置历史订单信息

            StringBuilder signTradeOrderDetail = new StringBuilder();

            String firstSignTime = "无";
            String nextPayData = "无";
            if (Objects.nonNull(tradeOrderDO.getTradeSignId())) {
                signTag = true;
                TradeSignDO tradeSignDO = tradeSignUpdateService.updatePayTime(tradeOrderDO.getTradeSignId());
                // 获取当前签约成功次数
                List<TradeOrderDO> signPayTradeList = tradeOrderQueryService.getSignPayTradeList(tradeOrderDO.getTradeSignId());
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

                    signTradeOrderDetail.append(StrUtil.format(SignNotifyTemplate, index + 1, item.getCreateTime(), item.getPayTime(), userRangeTimeRange, userLevelTimeRange, item.getPayStatus() ? "完成✅" : "错误❌"));
                }));

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

            AdminUserLevelDO level = null;
            String userLevelName = "无";
            String userLevelTimeRange = "无";
            // 获取当前增加的用户等级
            if (commonDTO.getLevelBasicDTO().getOperateDTO().getIsAdd()) {
                // 通过业务 获取等级记录
                level = adminUserLevelService.getRecordByBiz(AdminUserRightsBizTypeEnum.ORDER_GIVE.getType(), tradeOrderDO.getId(), tradeOrderDO.getUserId());
                userLevelName = level.getLevelName();
                userLevelTimeRange = StrUtil.format("{}至{}", LocalDateTimeUtil.formatNormal(level.getValidStartTime()), LocalDateTimeUtil.formatNormal(level.getValidEndTime()));
            }
            // 获取当前会员所有角色
            List<String> roleNameList = roleApi.getRoleNameList(tradeOrderDO.getUserId());
            String userRoleName = CollUtil.join(roleNameList, ",");

            // 开始权益和等级有效期检测
            Boolean checkResult = false;
            if (Objects.nonNull(rights) && Objects.nonNull(level)) {
                // 获取当前权益检查结果
                checkResult = adminUserLevelService.checkLevelAndRights(level, rights);
            }

            // 设置通知信息参数
            HashMap<String, Object> templateParams = MapUtil.newHashMap();
            // 当前运行环境
            templateParams.put("environmentName", environmentName);
            // 用户昵称
            templateParams.put("userName", userRespDTO.getNickname());
            // 产品名称
            templateParams.put("productName", orderItems.get(0).getSpuName());
            // 商品属性
            templateParams.put("productType", orderItems.get(0).getProperties().get(0).getValueName());
            // 购买时长
            templateParams.put("purchaseDuration", tradeOrderDO.getGiveRights().get(0).getRightsBasicDTO().getTimesRange().getNums() + getChineseName(tradeOrderDO.getGiveRights().get(0).getRightsBasicDTO().getTimesRange().getRange()));
            // 原价
            templateParams.put("totalPrice", MoneyUtils.fenToYuanStr(tradeOrderDO.getTotalPrice()));
            // 优惠金额
            templateParams.put("discountPrice", MoneyUtils.fenToYuanStr(tradeOrderDO.getDiscountPrice()));
            // 优惠券名称
            templateParams.put("couponName", couponName);
            // 实际支付金额
            templateParams.put("payPrice", MoneyUtils.fenToYuanStr(tradeOrderDO.getPayPrice()));
            // 订单创建时间
            templateParams.put("createTime", LocalDateTimeUtil.formatNormal(tradeOrderDO.getCreateTime()));
            // 支付时间
            templateParams.put("payTime", LocalDateTimeUtil.formatNormal(payOrderRespDTO.getSuccessTime()));
            // 支付回调时间
            templateParams.put("payNotifyTime", LocalDateTimeUtil.formatNormal(tradeOrderDO.getPayTime() == null ? LocalDateTime.now() : tradeOrderDO.getPayTime()));
            // 支付来源
            templateParams.put("payChannelCode", PayChannelEnum.isAlipay(tradeOrderDO.getPayChannelCode()) ? "支付宝" : "微信");

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


            log.info("准备发送订单通知消息，消息参数为:{}", templateParams);
            // 发送消息通知
            smsSendApi.sendSingleSmsToAdmin(new SmsSendSingleToUserReqDTO().setUserId(1L).setMobile("17835411844").setTemplateCode("DING_TALK_PAY_NOTIFY_01").setTemplateParams(templateParams));
        } catch (Exception e) {
            log.error("订单消息发送失败,错误原因为 errMsg{},当前订单为{}", e.getMessage(), JSONUtil.toJsonStr(tradeOrderDO), e);
        }


    }


    /**
     * 利用BiConsumer实现foreach循环支持index
     *
     * @param biConsumer  biConsumer
     * @param <T> T
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


}
