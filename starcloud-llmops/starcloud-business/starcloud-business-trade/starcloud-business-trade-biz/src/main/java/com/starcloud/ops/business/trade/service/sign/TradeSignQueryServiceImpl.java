package com.starcloud.ops.business.trade.service.sign;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.pay.core.enums.order.PayOrderStatusRespEnum;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.pay.api.order.PayOrderApi;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderSubmitReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderSubmitRespDTO;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import com.starcloud.ops.business.product.api.sku.ProductSkuApi;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderCreateReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderSettlementReqVO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignItemDO;
import com.starcloud.ops.business.trade.dal.mysql.sign.TradeSignItemMapper;
import com.starcloud.ops.business.trade.dal.mysql.sign.TradeSignMapper;
import com.starcloud.ops.business.trade.enums.order.TradeOrderStatusEnum;
import com.starcloud.ops.business.trade.service.order.TradeOrderQueryService;
import com.starcloud.ops.business.trade.service.order.TradeOrderUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static cn.hutool.core.date.DatePattern.CHINESE_DATE_TIME_PATTERN;

/**
 * 交易订单【读】 Service 接口
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class TradeSignQueryServiceImpl implements TradeSignQueryService {


    @Resource
    private TradeSignMapper tradeSignMapper;

    @Resource
    private TradeSignItemMapper tradeSignItemMapper;

    @Resource
    private TradeOrderUpdateService tradeOrderUpdateService;

    @Resource
    private TradeOrderQueryService tradeOrderQueryService;

    @Resource
    private PayOrderApi payOrderApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private ProductSkuApi productSkuApi;

    @Resource
    private DingTalkNoticeProperties dingTalkNoticeProperties;

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
        return tradeSignMapper.selectByIdAndUserId(id, userId);
    }

    /**
     *
     */
    @Override
    public int executeAutoTradeSignPay() {
        // 1.0 获取签约中待扣款列表
        List<TradeSignDO> tradeSignDOS = tradeSignMapper.selectDeductibleData();

        // 数据为空 直接返回
        if (tradeSignDOS.isEmpty()) {
            log.info("【签约自动扣款业务】, 【未获取到待自动扣款的签约记录】. TenantId[{}]", TenantContextHolder.getTenantId());
            return 0;
        }

        // 2. 遍历执行扣款
        int count = 0;
        for (TradeSignDO tradeSignDO : tradeSignDOS) {
            try {
                List<TradeSignItemDO> tradeSignItemDOS = tradeSignItemMapper.selectListBySignId(tradeSignDO.getId());
                // 支付前的验证
                productSkuApi.isValidSubscriptionSupported(tradeSignItemDOS.get(0).getSkuId());
                // 执行自动扣款
                count += autoTradeSignPay(tradeSignDO) ? 1 : 0;
            } catch (Exception e) {
                log.error("【签约自动扣款业务,自动扣款失败】错误原因:[errMsg={}],当前数据为[{}]", JSONUtil.toJsonStr(tradeSignDO),e.getMessage(),e);
            }
        }
        return count;

    }

    /**
     * 获取全量签约订单 并且发送钉钉通知
     *
     * @return 签约单数量
     */
    @Override
    public int signAutoNotify() {
        List<TradeSignDO> tradeSignDOS = tradeSignMapper.selectIsSignSuccess();
        String content;
        if (!tradeSignDOS.isEmpty()) {
            // 发送钉钉通知
            content = buildMsg(tradeSignDOS);
        } else {
            content = "无签约记录";
        }
        sendNotifyMsg(content);
        return tradeSignDOS.size();

    }


    private String buildMsg(List<TradeSignDO> tradeSignDOS) {
        StringBuilder stringBuilder = new StringBuilder();
        for (TradeSignDO tradeSignDO : tradeSignDOS) {
            // 获取当前下单用户
            AdminUserRespDTO user = adminUserApi.getUser(tradeSignDO.getUserId());
            List<TradeSignItemDO> tradeSignItemDOS = tradeSignItemMapper.selectListBySignId(tradeSignDO.getId());
            // 拼接为 markdown 表格样式
            stringBuilder.append("| ").append(user.getNickname());
            stringBuilder.append(" | ").append(tradeSignDO.getStatus() == 30 ? "签约成功" : "取消签约");
            stringBuilder.append(" |").append(LocalDateTimeUtil.format(tradeSignDO.getFinishTime(), CHINESE_DATE_TIME_PATTERN));
            stringBuilder.append(" |").append(LocalDateTimeUtil.format(tradeSignDO.getPayTime(), CHINESE_DATE_TIME_PATTERN));
            stringBuilder.append(" | ").append(tradeSignItemDOS.get(0).getSpuName());
            stringBuilder.append(" | ").append(MoneyUtils.fenToYuanStr(tradeSignDO.getSignPrice()));
            stringBuilder.append(" | ").append(tradeSignDO.getTenantId()).append(" | ");
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
                            .setTemplateCode("SIGN_NOTIFY")
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("订单通知信息发送失败", e);
        }
    }

    public Boolean autoTradeSignPay(TradeSignDO tradeSignDO) {

        log.info("[当前签约开始构建签约自动扣款][autoTradeSignPay]: {}", tradeSignDO);
        // 构建交易订单
        TradeOrderDO order = buildTradeOrder(tradeSignDO);
        // 提交交易订单且发起支付
        PayOrderSubmitRespDTO payOrderSubmitRespDTO =
                payOrderApi.submitSignPayOrder(
                        new PayOrderSubmitReqDTO()
                                .setId(order.getPayOrderId())
                                .setChannelCode("alipay_pc")
                                .setDisplayMode("url"), order.getUserIp());

        if (PayOrderStatusRespEnum.isSuccess(payOrderSubmitRespDTO.getStatus())) {
            // tradeOrderUpdateService.updateOrderPaid(order.getId(),order.getPayOrderId());
            log.error("签约支付发起成功");
            return true;
        }
        sendNotifySignPayFailMsg(tradeSignDO, payOrderSubmitRespDTO);
        log.error("签约支付发起失败: {}", payOrderSubmitRespDTO);
        return false;
    }


    @TenantIgnore
    private void sendNotifySignPayFailMsg(TradeSignDO tradeSignDO, PayOrderSubmitRespDTO submitRespDTO) {
        try {
            AdminUserRespDTO user = adminUserApi.getUser(tradeSignDO.getUserId());
            String environmentName = dingTalkNoticeProperties.getName().equals("Test") ? "测试环境" : "正式环境";

            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("environmentName", environmentName);
            templateParams.put("userName", user.getNickname());
            templateParams.put("tradeSignId", tradeSignDO.getId());
            templateParams.put("exception", submitRespDTO.getDisplayContent());
            templateParams.put("notifyTime", LocalDateTimeUtil.formatNormal(LocalDateTime.now()));

            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                            .setTemplateCode("DING_TALK_PAY_NOTIFY_03")
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("系统支付通知信息发送失败", e);
        }

    }

    public TradeOrderDO buildTradeOrder(TradeSignDO tradeSignDO) {
        // 判断当前用户是否存在当前扣款周期内的订单交易记录
        TradeOrderDO order = tradeOrderQueryService.getOrderBySignPayTime(tradeSignDO.getId(), tradeSignDO.getPayTime());
        // 存在 -修改交易订单过期时间
        if (Objects.nonNull(order)) {

            TradeOrderDO tradeOrderDO = (TradeOrderDO) new TradeOrderDO().setId(order.getId())
                    .setStatus(TradeOrderStatusEnum.UNPAID.getStatus())
                    // 重新设置交易过期时间
                    .setCreateTime(LocalDateTimeUtil.now())
                    .setUpdateTime(LocalDateTimeUtil.now());
            tradeOrderUpdateService.updateOrderTimeAndStatus(tradeOrderDO);
            return order;
        }
        // 不存在 -构建签约交易订单
        List<TradeSignItemDO> tradeSignItemDOS = tradeSignItemMapper.selectListBySignId(tradeSignDO.getId());

        ArrayList<AppTradeOrderSettlementReqVO.Item> items = new ArrayList<>();
        for (TradeSignItemDO tradeSignItemDO : tradeSignItemDOS) {
            AppTradeOrderSettlementReqVO.Item orderItem = new AppTradeOrderSettlementReqVO.Item();
            orderItem.setSkuId(tradeSignItemDO.getSkuId());
            orderItem.setCount(tradeSignItemDO.getCount());
            items.add(orderItem);
        }

        AppTradeOrderCreateReqVO createReqVO = new AppTradeOrderCreateReqVO();
        createReqVO.setItems(items);
        createReqVO.setPointStatus(false);
        createReqVO.setDeliveryType(3);
        createReqVO.setTradeSignId(String.valueOf(tradeSignDO.getId()));

        return tradeOrderUpdateService.createSignOrder(tradeSignDO.getUserId(), tradeSignDO.getUserIp(), createReqVO, 20);
    }
}
