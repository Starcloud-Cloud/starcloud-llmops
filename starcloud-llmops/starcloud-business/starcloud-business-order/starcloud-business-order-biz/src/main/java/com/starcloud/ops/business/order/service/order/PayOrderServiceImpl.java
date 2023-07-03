package com.starcloud.ops.business.order.service.order;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.pay.config.PayProperties;
import cn.iocoder.yudao.framework.pay.core.client.PayClient;
import cn.iocoder.yudao.framework.pay.core.client.PayClientFactory;
import cn.iocoder.yudao.framework.pay.core.client.dto.notify.PayNotifyReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.notify.PayOrderNotifyRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import com.starcloud.ops.business.limits.enums.SetMealInfoEnum;
import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import com.starcloud.ops.business.order.controller.admin.order.vo.*;
import com.starcloud.ops.business.order.convert.order.PayOrderConvert;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayChannelDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderExtensionDO;
import com.starcloud.ops.business.order.dal.mysql.order.PayOrderExtensionMapper;
import com.starcloud.ops.business.order.dal.mysql.order.PayOrderMapper;
import com.starcloud.ops.business.order.enums.ErrorCodeConstants;
import com.starcloud.ops.business.order.enums.notify.PayNotifyTypeEnum;
import com.starcloud.ops.business.order.enums.order.PayOrderNotifyStatusEnum;
import com.starcloud.ops.business.order.enums.order.PayOrderStatusEnum;
import com.starcloud.ops.business.order.enums.refund.PayRefundTypeEnum;
import com.starcloud.ops.business.order.service.merchant.PayAppService;
import com.starcloud.ops.business.order.service.merchant.PayChannelService;
import com.starcloud.ops.business.order.service.notify.PayNotifyService;
import com.starcloud.ops.business.order.service.notify.dto.PayNotifyTaskCreateReqDTO;
import com.starcloud.ops.business.order.util.PaySeqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;

/**
 * 支付订单 Service 实现类
 *
 * @author aquan
 */
@Service
@Validated
@Slf4j
public class PayOrderServiceImpl implements PayOrderService {


    @Resource
    private PayProperties payProperties;

    @Resource
    private PayClientFactory payClientFactory;

    @Resource
    private PayOrderMapper orderMapper;
    @Resource
    private PayOrderExtensionMapper orderExtensionMapper;

    @Resource
    private PayAppService appService;
    @Resource
    private PayChannelService channelService;
    @Resource
    private PayNotifyService notifyService;


    private static final Long PAY_APP_ID = 7L;
    private static final String CHANNEL_CODE = "alipay_pc";


    @Override
    public PayOrderDO getOrder(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public PageResult<PayOrderDO> getOrderPage(PayOrderPageReqVO pageReqVO) {
        return orderMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PayOrderDO> getOrderList(PayOrderExportReqVO exportReqVO) {
        return orderMapper.selectList(exportReqVO);
    }

    // TODO @艿艿：需要优化。不确定这个方法的作用
    @Override
    public List<PayOrderDO> getOrderSubjectList(Collection<Long> idList) {
        return orderMapper.findByIdListQueryOrderSubject(idList);
    }

    @Override
    public Long createPayOrder(PayOrderCreateReqDTO reqDTO) {
        // 校验 支付应用App
        PayAppDO app = appService.validPayApp(reqDTO.getAppId());

        // 查询对应的支付交易单是否已经存在。如果是，则直接返回
        PayOrderDO order = orderMapper.selectByAppIdAndMerchantOrderId(
                reqDTO.getAppId(), reqDTO.getMerchantOrderId());
        if (order != null) {
            log.warn("[createPayOrder][appId({}) merchantOrderId({}) 已经存在对应的支付单({})]", order.getAppId(),
                    order.getMerchantOrderId(), toJsonString(order)); // 理论来说，不会出现这个情况
            return order.getId();
        }

        // 创建支付交易单
        order = PayOrderConvert.INSTANCE.convert(reqDTO)
                .setMerchantId(app.getMerchantId()).setAppId(app.getId());
        // 商户相关字段
        order.setNotifyUrl(app.getPayNotifyUrl())
                .setNotifyStatus(PayOrderNotifyStatusEnum.NO.getStatus());
        // 订单相关字段
        order.setStatus(PayOrderStatusEnum.WAITING.getStatus());
        // 退款相关字段
        // 退款状态枚举是不是有问题
        order.setRefundStatus(PayRefundTypeEnum.NO.getStatus())
                .setRefundTimes(0).setRefundAmount(0L);
        orderMapper.insert(order);
        // 最终返回
        return order.getId();
    }

    @Override
    public PayOrderSubmitRespVO submitPayOrder(PayOrder2ReqVO reqVO, String userIp) {
        log.info("[submitPayOrder][0.支付宝统一下单接收到请求：用户ID({})|产品 code({})｜用户 IP({})]", getLoginUser(), reqVO.getCode(), userIp);
        // 商户订单编号
        String sMerchantOrderId = PaySeqUtils.genMerchantOrderNo();
        // 0.根据商品 code 获取产品参数
        SetMealInfoEnum SetMealInfo = SetMealInfoEnum.getByCode(reqVO.getCode());

        PayOrderCreateReqDTO payOrderCreateReqDTO = new PayOrderCreateReqDTO();

        payOrderCreateReqDTO.setAppId(PAY_APP_ID);

        payOrderCreateReqDTO.setUserIp(userIp);
        payOrderCreateReqDTO.setMerchantOrderId(sMerchantOrderId);
        payOrderCreateReqDTO.setSubject(SetMealInfo.getName());
        payOrderCreateReqDTO.setBody(SetMealInfo.getDescription());
        payOrderCreateReqDTO.setAmount(SetMealInfo.getPrice());
        // 支付过期时间设置为 1 天
        payOrderCreateReqDTO.setExpireTime(LocalDateTime.now().plusHours(1));
        // 创建订单
        Long payOrderId = createPayOrder(payOrderCreateReqDTO);
        // 1. 获得 PayOrderDO ，并校验其是否存在
        PayOrderDO order = validatePayOrderCanSubmit(payOrderId);
        // 1.2 校验支付渠道是否有效
        PayChannelDO channel = validatePayChannelCanSubmit(order.getAppId(), CHANNEL_CODE);
        PayClient client = payClientFactory.getPayClient(channel.getId());

        PayOrderSubmitReqVO payOrderSubmitReqVO = new PayOrderSubmitReqVO();
        payOrderSubmitReqVO.setId(payOrderId);
        payOrderSubmitReqVO.setChannelCode(CHANNEL_CODE);

        // 2. 插入 PayOrderExtensionDO
        PayOrderExtensionDO orderExtension = PayOrderConvert.INSTANCE.convert(payOrderSubmitReqVO, userIp)
                .setOrderId(order.getId())
                .setNo(generateOrderExtensionNo())
                .setChannelId(channel.getId())
                .setChannelCode(channel.getCode())
                .setStatus(PayOrderStatusEnum.WAITING.getStatus());
        orderExtensionMapper.insert(orderExtension);

        // 3. 调用三方接口
        PayOrderUnifiedReqDTO unifiedOrderReqDTO = PayOrderConvert.INSTANCE.convert2(payOrderSubmitReqVO)
                // 商户相关的字段
                .setMerchantOrderId(orderExtension.getNo()) // 注意，此处使用的是 PayOrderExtensionDO.no 属性！
                .setSubject(order.getSubject())
                .setBody(order.getBody())
                .setNotifyUrl(genChannelPayNotifyUrl(channel))
                .setReturnUrl(genChannelReturnUrl(channel))
                // 订单相关字段
                // .setAmount(new BigDecimal(order.getAmount()).movePointLeft(2))
                .setAmount(order.getAmount())
                .setExpireTime(order.getExpireTime());
        PayOrderUnifiedRespDTO unifiedOrderRespDTO = client.unifiedOrder(unifiedOrderReqDTO);

        // 返回成功
        return PayOrderConvert.INSTANCE.convert(unifiedOrderRespDTO);
    }

    @Deprecated
    public Long createPayOrder(SetMealInfoEnum product,String userIP) {

        PayOrderDO order = new PayOrderDO();
        // ========订单相关字段=========
        // 生成订单编号
        String sMerchantOrderId = PaySeqUtils.genMerchantOrderNo();
        // 设置订单编号
        order.setMerchantOrderId(sMerchantOrderId);
        // 订单相关状态
        order.setAmount(product.getPrice());
        // 通知商户支付结果的回调状态
        order.setNotifyStatus(PayOrderNotifyStatusEnum.NO.getStatus());
        // 订单相关状态
        order.setStatus(PayOrderStatusEnum.WAITING.getStatus());

        // ========商品相关字段=========
        // 设置商品名称
        order.setSubject(product.getName());
        // 设置商品描述
        order.setBody(product.getDescription());

        // ========退款相关字段=========
        // 创建支付的订单的退款状态枚举
        order.setRefundStatus(PayRefundTypeEnum.NO.getStatus());
        order.setRefundTimes(0).setRefundAmount(0L);
        order.setRefundAmount(0L);
        orderMapper.insert(order);
        // 最终返回
        return order.getId();
    }


    private PayOrderDO validatePayOrderCanSubmit(Long id) {
        PayOrderDO order = orderMapper.selectById(id);
        if (order == null) { // 是否存在
            throw exception(ErrorCodeConstants.PAY_ORDER_NOT_FOUND);
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        return order;
    }

    private PayChannelDO validatePayChannelCanSubmit(Long appId, String channelCode) {
        // 校验 App
        appService.validPayApp(appId);

        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(appId, channelCode);
        // 校验支付客户端是否正确初始化
        PayClient client = payClientFactory.getPayClient(channel.getId());
        if (client == null) {
            log.error("[validatePayChannelCanSubmit][渠道编号({}) 找不到对应的支付客户端]", channel.getId());
            throw exception(ErrorCodeConstants.PAY_CHANNEL_CLIENT_NOT_FOUND);
        }
        return channel;
    }

    /**
     * 根据支付渠道的编码，生成支付渠道的返回地址
     *
     * @param channel 支付渠道
     * @return 支付成功返回的地址。 配置地址 + "/" + channel id
     */
    private String genChannelReturnUrl(PayChannelDO channel) {
        return payProperties.getReturnUrl() + "/" + channel.getId();
    }

    /**
     * 根据支付渠道的编码，生成支付渠道的回调地址
     *
     * @param channel 支付渠道
     * @return 支付渠道的回调地址  配置地址 + "/" + channel id
     */
    private String genChannelPayNotifyUrl(PayChannelDO channel) {
        return payProperties.getCallbackUrl() + "/" + channel.getId();
    }

    private String generateOrderExtensionNo() {
//    wx
//    2014
//    10
//    27
//    20
//    09
//    39
//    5522657
//    a690389285100
        // 目前的算法
        // 时间序列，年月日时分秒 14 位
        // 纯随机，6 位 TODO 芋艿：此处估计是会有问题的，后续在调整
        return DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + // 时间序列
                RandomUtil.randomInt(100000, 999999) // 随机。为什么是这个范围，因为偷懒
                ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyPayOrder(Long channelId, PayOrderNotifyRespDTO notify, PayNotifyReqDTO rawNotify) {
        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(channelId);
        TenantUtils.execute(channel.getTenantId(), () -> {
            // 1. 更新 PayOrderExtensionDO 支付成功
            PayOrderExtensionDO orderExtension = updatePayOrderExtensionSuccess(notify.getOrderExtensionNo(),
                    rawNotify);
            // 2. 更新 PayOrderDO 支付成功
            PayOrderDO order = updatePayOrderSuccess(channel, orderExtension, notify);

            // 3. 插入支付通知记录
            notifyService.createPayNotifyTask(PayNotifyTaskCreateReqDTO.builder()
                    .type(PayNotifyTypeEnum.ORDER.getType()).dataId(order.getId()).build());
        });
    }

    /**
     * 用户获得订单记录
     * 分页
     *
     * @param userId   分页查询
     * @param tenantId 分页查询
     * @return 支付订单
     * 分页
     */
    @Override
    public PageResult<AppPayOrderDetailsRespVO> getAppOrderPage(PayOrderAppPageReqVO pageReqVO,Long userId, Long tenantId) {

        PageResult<PayOrderDO> payOrderDOPageResult = orderMapper.selectAppPage(pageReqVO, userId, tenantId);

        return PayOrderConvert.INSTANCE.convertAppPage(payOrderDOPageResult);

    }

    /**
     * 更新 PayOrderExtensionDO 支付成功
     *
     * @param no        支付订单号（支付模块）
     * @param rawNotify 通知数据
     * @return PayOrderExtensionDO 对象
     */
    private PayOrderExtensionDO updatePayOrderExtensionSuccess(String no, PayNotifyReqDTO rawNotify) {
        // 1.1 查询 PayOrderExtensionDO
        PayOrderExtensionDO orderExtension = orderExtensionMapper.selectByNo(no);
        if (orderExtension == null) {
            throw exception(ErrorCodeConstants.PAY_ORDER_EXTENSION_NOT_FOUND);
        }
        if (ObjectUtil.notEqual(orderExtension.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }
        // 1.2 更新 PayOrderExtensionDO
        int updateCounts = orderExtensionMapper.updateByIdAndStatus(orderExtension.getId(),
                PayOrderStatusEnum.WAITING.getStatus(), PayOrderExtensionDO.builder().id(orderExtension.getId())
                        .status(PayOrderStatusEnum.SUCCESS.getStatus())
                        .channelNotifyData(toJsonString(rawNotify)).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }
        log.info("[updatePayOrderSuccess][支付拓展单({}) 更新为已支付]", orderExtension.getId());
        return orderExtension;
    }

    /**
     * 更新 PayOrderDO 支付成功
     *
     * @param channel        支付渠道
     * @param orderExtension 支付拓展单
     * @param notify         通知回调
     * @return PayOrderDO 对象
     */
    private PayOrderDO updatePayOrderSuccess(PayChannelDO channel, PayOrderExtensionDO orderExtension,
                                             PayOrderNotifyRespDTO notify) {
        // 2.1 判断 PayOrderDO 是否处于待支付
        PayOrderDO order = orderMapper.selectById(orderExtension.getOrderId());
        if (order == null) {
            throw exception(ErrorCodeConstants.PAY_ORDER_NOT_FOUND);
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        // 2.2 更新 PayOrderDO
        int updateCounts = orderMapper.updateByIdAndStatus(order.getId(), PayOrderStatusEnum.WAITING.getStatus(),
                PayOrderDO.builder().status(PayOrderStatusEnum.SUCCESS.getStatus())
                        .channelId(channel.getId()).channelCode(channel.getCode())
                        .successTime(notify.getSuccessTime()).successExtensionId(orderExtension.getId())
                        .channelOrderNo(notify.getChannelOrderNo()).channelUserId(notify.getChannelUserId())
                        .notifyTime(LocalDateTime.now()).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        log.info("[updatePayOrderSuccess][支付订单({}) 更新为已支付]", order.getId());
        return order;
    }

}
