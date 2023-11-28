package cn.iocoder.yudao.module.pay.service.sign;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.pay.core.client.PayClient;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.enums.order.PayOrderStatusRespEnum;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.PaySignExportReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.PaySignPageReqVO;
import cn.iocoder.yudao.module.pay.convert.order.PayOrderConvert;
import cn.iocoder.yudao.module.pay.convert.sign.PaySignConvert;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.channel.PayChannelDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignExtensionDO;
import cn.iocoder.yudao.module.pay.dal.mysql.sign.PaySignExtensionMapper;
import cn.iocoder.yudao.module.pay.dal.mysql.sign.PaySignMapper;
import cn.iocoder.yudao.module.pay.dal.redis.no.SignNoRedisDAO;
import cn.iocoder.yudao.module.pay.enums.notify.PayNotifyTypeEnum;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.enums.sign.PaySignStatusEnum;
import cn.iocoder.yudao.module.pay.framework.pay.config.PayProperties;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import cn.iocoder.yudao.module.pay.service.channel.PayChannelService;
import cn.iocoder.yudao.module.pay.service.notify.PayNotifyService;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString;
import static cn.iocoder.yudao.module.pay.enums.ErrorCodeConstants.*;

/**
 * 支付订单 Service 实现类
 *
 * @author aquan
 */
@Service
@Validated
@Slf4j
public class PaySignServiceImpl implements PaySignService {

    @Resource
    private PayProperties payProperties;

    @Resource
    private PaySignMapper signMapper;
    @Resource
    private PaySignExtensionMapper signExtensionMapper;
    @Resource
    private SignNoRedisDAO noRedisDAO;

    @Resource
    private PayAppService appService;
    @Resource
    private PayChannelService channelService;
    @Resource
    private PayNotifyService notifyService;

    @Override
    public PaySignDO getSign(Long id) {
        return signMapper.selectById(id);
    }

    @Override
    public PaySignDO getSign(Long appId, String merchantSignId) {
        return signMapper.selectByAppIdAndMerchantSignId(appId, merchantSignId);
    }

    @Override
    public Long getSignCountByAppId(Long appId) {
        return signMapper.selectCountByAppId(appId);
    }

    @Override
    public PageResult<PaySignDO> getSignPage(PaySignPageReqVO pageReqVO) {
        return signMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PaySignDO> getSignList(PaySignExportReqVO exportReqVO) {
        return signMapper.selectList(exportReqVO);
    }

    @Override
    public Long createSign(PaySignCreateReqDTO reqDTO) {
        // 校验 App
        PayAppDO app = appService.validPayApp(reqDTO.getAppId());

        // 查询对应的支付交易单是否已经存在。如果是，则直接返回
        PaySignDO signDO = signMapper.selectByAppIdAndMerchantSignId(
                reqDTO.getAppId(), reqDTO.getMerchantSignId());
        if (signDO != null) {
            log.warn("[createSign][appId({}) merchantSignId({}) 已经存在对应的支付单({})]", signDO.getAppId(),
                    signDO.getMerchantSignId(), toJsonString(signDO)); // 理论来说，不会出现这个情况
            return signDO.getId();
        }

        // 创建支付交易单
        signDO = PaySignConvert.INSTANCE.convert(reqDTO)
                .setAppId(app.getId())
                // 商户相关字段
                .setNotifyUrl(app.getOrderNotifyUrl())
                // 订单相关字段
                .setStatus(PaySignStatusEnum.WAITING.getStatus());
        signMapper.insert(signDO);
        return signDO.getId();
    }

    @Override // 注意，这里不能添加事务注解，避免调用支付渠道失败时，将 PayOrderExtensionDO 回滚了
    public PayOrderSubmitRespVO submitSign(PayOrderSubmitReqVO reqVO, String userIp) {
        // 1.1 获得 PayOrderDO ，并校验其是否存在
        PaySignDO signDO = validateOrderCanSubmit(reqVO.getId());
        // 1.32 校验支付渠道是否有效
        PayChannelDO channel = validateChannelCanSubmit(signDO.getAppId(), reqVO.getChannelCode());
        PayClient client = channelService.getPayClient(channel.getId());

        // 2. 插入 PayOrderExtensionDO
        String no = noRedisDAO.generate(payProperties.getOrderNoPrefix());
        PaySignExtensionDO signExtension = PaySignConvert.INSTANCE.convert(reqVO, userIp)
                .setSignId(signDO.getId()).setNo(no)
                .setChannelId(channel.getId()).setChannelCode(channel.getCode())
                .setStatus(PayOrderStatusEnum.WAITING.getStatus());
        signExtensionMapper.insert(signExtension);

        // 3. 调用三方接口
        PayOrderUnifiedReqDTO unifiedOrderReqDTO = PayOrderConvert.INSTANCE.convert2(reqVO, userIp)
                // 商户相关的字段
                .setOutTradeNo(signExtension.getNo()) // 注意，此处使用的是 PayOrderExtensionDO.no 属性！
                .setSubject(signDO.getSubject()).setBody(signDO.getBody())
                .setNotifyUrl(genChannelOrderNotifyUrl(channel))
                .setReturnUrl(reqVO.getReturnUrl())
                // 订单相关字段
                .setPrice(signDO.getPrice())
                .setExpireTime(signDO.getExpireTime());
        PayOrderRespDTO unifiedOrderResp = client.unifiedOrder(unifiedOrderReqDTO);

        // 4. 如果调用直接支付成功，则直接更新支付单状态为成功。例如说：付款码支付，免密支付时，就直接验证支付成功
        if (unifiedOrderResp != null) {
            getSelf().notifyOrder(channel, unifiedOrderResp);
            // 如有渠道错误码，则抛出业务异常，提示用户
            if (StrUtil.isNotEmpty(unifiedOrderResp.getChannelErrorCode())) {
                throw exception(PAY_ORDER_SUBMIT_CHANNEL_ERROR, unifiedOrderResp.getChannelErrorCode(),
                        unifiedOrderResp.getChannelErrorMsg());
            }
            // 此处需要读取最新的状态
            signDO = signMapper.selectById(signDO.getId());
        }
        return PaySignConvert.INSTANCE.convert(signDO, unifiedOrderResp);
    }

    private PaySignDO validateOrderCanSubmit(Long id) {
        PaySignDO signDO = signMapper.selectById(id);
        if (signDO == null) { // 是否存在
            throw exception(PAY_ORDER_NOT_FOUND);
        }
        if (PayOrderStatusEnum.isSuccess(signDO.getStatus())) { // 校验状态，发现已支付
            throw exception(PAY_ORDER_STATUS_IS_SUCCESS);
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(signDO.getStatus())) { // 校验状态，必须是待支付
            throw exception(PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        if (LocalDateTimeUtils.beforeNow(signDO.getExpireTime())) { // 校验是否过期
            throw exception(PAY_ORDER_IS_EXPIRED);
        }

        // 【重要】校验是否支付拓展单已支付，只是没有回调、或者数据不正常
        validateOrderActuallyPaid(id);
        return signDO;
    }

    /**
     * 校验支付订单实际已支付
     *
     * @param id 支付编号
     */
    @VisibleForTesting
    void validateOrderActuallyPaid(Long id) {
        List<PaySignExtensionDO> signExtensions = signExtensionMapper.selectListBySignId(id);
        signExtensions.forEach(signExtension -> {
            // 情况一：校验数据库中的 orderExtension 是不是已支付
            if (PayOrderStatusEnum.isSuccess(signExtension.getStatus())) {
                log.warn("[validateOrderCanSubmit][order({}) 的 extension({}) 已支付，可能是数据不一致]",
                        id, signExtension.getId());
                throw exception(PAY_ORDER_EXTENSION_IS_PAID);
            }
            // 情况二：调用三方接口，查询支付单状态，是不是已支付
            PayClient payClient = channelService.getPayClient(signExtension.getChannelId());
            if (payClient == null) {
                log.error("[validateOrderCanSubmit][渠道编号({}) 找不到对应的支付客户端]", signExtension.getChannelId());
                return;
            }
            PayOrderRespDTO respDTO = payClient.getSign(signExtension.getNo());
            if (respDTO != null && PayOrderStatusRespEnum.isSuccess(respDTO.getStatus())) {
                log.warn("[validateOrderCanSubmit][order({}) 的 PayOrderRespDTO({}) 已支付，可能是回调延迟]",
                        id, toJsonString(respDTO));
                throw exception(PAY_ORDER_EXTENSION_IS_PAID);
            }
        });
    }

    private PayChannelDO validateChannelCanSubmit(Long appId, String channelCode) {
        // 校验 App
        appService.validPayApp(appId);
        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(appId, channelCode);
        PayClient client = channelService.getPayClient(channel.getId());
        if (client == null) {
            log.error("[validatePayChannelCanSubmit][渠道编号({}) 找不到对应的支付客户端]", channel.getId());
            throw exception(CHANNEL_NOT_FOUND);
        }
        return channel;
    }

    /**
     * 根据支付渠道的编码，生成支付渠道的回调地址
     *
     * @param channel 支付渠道
     * @return 支付渠道的回调地址  配置地址 + "/" + channel id
     */
    private String genChannelOrderNotifyUrl(PayChannelDO channel) {
        return payProperties.getOrderNotifyUrl() + "/" + channel.getId();
    }

    @Override
    public void notifySign(Long channelId, PayOrderRespDTO notify) {
        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(channelId);
        // 更新支付订单为已支付
        TenantUtils.execute(channel.getTenantId(), () -> getSelf().notifyOrder(channel, notify));
    }

    /**
     * 通知并更新订单的支付结果
     *
     * @param channel 支付渠道
     * @param notify  通知
     */
    @Transactional(rollbackFor = Exception.class)
    // 注意，如果是方法内调用该方法，需要通过 getSelf().notifyPayOrder(channel, notify) 调用，否则事务不生效
    public void notifyOrder(PayChannelDO channel, PayOrderRespDTO notify) {
        // 情况一：支付成功的回调
        if (PayOrderStatusRespEnum.isSuccess(notify.getStatus())) {
            notifyOrderSuccess(channel, notify);
            return;
        }
        // 情况二：支付失败的回调
        if (PayOrderStatusRespEnum.isClosed(notify.getStatus())) {
            notifyOrderClosed(channel, notify);
        }
        // 情况三：WAITING：无需处理
        // 情况四：REFUND：通过退款回调处理
    }

    private void notifyOrderSuccess(PayChannelDO channel, PayOrderRespDTO notify) {
        // 1. 更新 PayOrderExtensionDO 支付成功
        PaySignExtensionDO signExtension = updateSignSuccess(notify);
        // 2. 更新 PayOrderDO 支付成功
        Boolean paid = updateSignSuccess(channel, signExtension, notify);
        if (paid) { // 如果之前已经成功回调，则直接返回，不用重复记录支付通知记录；例如说：支付平台重复回调
            return;
        }

        // 3. 插入支付通知记录
        notifyService.createPayNotifyTask(PayNotifyTypeEnum.SIGN.getType(),
                signExtension.getSignId());
    }

    /**
     * 更新 PayOrderExtensionDO 支付成功
     *
     * @param notify 通知
     * @return PayOrderExtensionDO 对象
     */
    private PaySignExtensionDO updateSignSuccess(PayOrderRespDTO notify) {
        // 1. 查询 PayOrderExtensionDO
        PaySignExtensionDO signExtension = signExtensionMapper.selectByNo(notify.getOutTradeNo());
        if (signExtension == null) {
            throw exception(PAY_ORDER_EXTENSION_NOT_FOUND);
        }
        if (PayOrderStatusEnum.isSuccess(signExtension.getStatus())) { // 如果已经是成功，直接返回，不用重复更新
            log.info("[updateOrderExtensionSuccess][orderExtension({}) 已经是已支付，无需更新]", signExtension.getId());
            return signExtension;
        }
        if (ObjectUtil.notEqual(signExtension.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
            throw exception(PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }

        // 2. 更新 PayOrderExtensionDO
        int updateCounts = signExtensionMapper.updateByIdAndStatus(signExtension.getId(), signExtension.getStatus(),
                PaySignExtensionDO.builder().status(PayOrderStatusEnum.SUCCESS.getStatus()).channelNotifyData(toJsonString(notify)).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }
        log.info("[updateOrderExtensionSuccess][orderExtension({}) 更新为已支付]", signExtension.getId());
        return signExtension;
    }

    /**
     * 更新 PaySignDO 支付成功
     *
     * @param channel        支付渠道
     * @param signExtension 支付拓展单
     * @param notify         通知回调
     * @return 是否之前已经成功回调
     */
    private Boolean updateSignSuccess(PayChannelDO channel, PaySignExtensionDO signExtension,
                                       PayOrderRespDTO notify) {
        // 1. 判断 PaySignDO 是否处于待支付
        PaySignDO signDO = signMapper.selectById(signExtension.getSignId());
        if (signDO == null) {
            throw exception(PAY_ORDER_NOT_FOUND);
        }
        if (PayOrderStatusEnum.isSuccess(signDO.getStatus()) // 如果已经是成功，直接返回，不用重复更新
                && Objects.equals(signDO.getExtensionId(), signExtension.getId())) {
            log.info("[updateOrderExtensionSuccess][order({}) 已经是已支付，无需更新]", signDO.getId());
            return true;
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(signDO.getStatus())) { // 校验状态，必须是待支付
            throw exception(PAY_ORDER_STATUS_IS_NOT_WAITING);
        }

        // 2. 更新 PayOrderDO
        int updateCounts = signMapper.updateByIdAndStatus(signDO.getId(), PayOrderStatusEnum.WAITING.getStatus(),
                PaySignDO.builder().status(PayOrderStatusEnum.SUCCESS.getStatus())
                        .channelId(channel.getId()).channelCode(channel.getCode())
                        .extensionId(signExtension.getId())
                        .no(signExtension.getNo())
                        .channelOrderNo(notify.getChannelOrderNo())
                        .channelUserId(notify.getChannelUserId())
                        .channelFeeRate(channel.getFeeRate())
                        .channelFeePrice(MoneyUtils.calculateRatePrice(signDO.getPrice(), channel.getFeeRate()))
                        .build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        log.info("[updateOrderExtensionSuccess][order({}) 更新为已支付]", signDO.getId());
        return false;
    }

    private void notifyOrderClosed(PayChannelDO channel, PayOrderRespDTO notify) {
        updateOrderExtensionClosed(channel, notify);
    }

    private void updateOrderExtensionClosed(PayChannelDO channel, PayOrderRespDTO notify) {
        // 1. 查询 PaySignExtensionDO
        PaySignExtensionDO signExtension = signExtensionMapper.selectByNo(notify.getOutTradeNo());
        if (signExtension == null) {
            throw exception(PAY_ORDER_EXTENSION_NOT_FOUND);
        }
        if (PayOrderStatusEnum.isClosed(signExtension.getStatus())) { // 如果已经是关闭，直接返回，不用重复更新
            log.info("[updateOrderExtensionClosed][orderExtension({}) 已经是支付关闭，无需更新]", signExtension.getId());
            return;
        }
        // 一般出现先是支付成功，然后支付关闭，都是全部退款导致关闭的场景。这个情况，我们不更新支付拓展单，只通过退款流程，更新支付单
        if (PayOrderStatusEnum.isSuccess(signExtension.getStatus())) {
            log.info("[updateOrderExtensionClosed][orderExtension({}) 是已支付，无需更新为支付关闭]", signExtension.getId());
            return;
        }
        if (ObjectUtil.notEqual(signExtension.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
            throw exception(PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }

        // 2. 更新 PayOrderExtensionDO
        int updateCounts = signExtensionMapper.updateByIdAndStatus(signExtension.getId(), signExtension.getStatus(),
                PaySignExtensionDO.builder().status(PaySignStatusEnum.CLOSED.getStatus()).channelNotifyData(toJsonString(notify))
                        .channelErrorCode(notify.getChannelErrorCode()).channelErrorMsg(notify.getChannelErrorMsg()).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }
        log.info("[updateOrderExtensionClosed][orderExtension({}) 更新为支付关闭]", signExtension.getId());
    }

//    @Override
//    public void updateSignRefundPrice(Long id, Integer incrRefundPrice) {
//        PaySignDO order = signMapper.selectById(id);
//        if (order == null) {
//            throw exception(PAY_ORDER_NOT_FOUND);
//        }
//        if (!PayOrderStatusEnum.isSuccessOrRefund(order.getStatus())) {
//            throw exception(PAY_ORDER_REFUND_FAIL_STATUS_ERROR);
//        }
//        if (order.getRefundPrice() + incrRefundPrice > order.getPrice()) {
//            throw exception(REFUND_PRICE_EXCEED);
//        }
//
//        // 更新订单
//        PayOrderDO updateObj = new PayOrderDO()
//                .setRefundPrice(order.getRefundPrice() + incrRefundPrice)
//                .setStatus(PayOrderStatusEnum.REFUND.getStatus());
//        int updateCount = signMapper.updateByIdAndStatus(id, order.getStatus(), updateObj);
//        if (updateCount == 0) {
//            throw exception(PAY_ORDER_REFUND_FAIL_STATUS_ERROR);
//        }
//    }

    @Override
    public void updatePaySignPrice(Long id, Integer payPrice) {
        PaySignDO order = signMapper.selectById(id);
        if (order == null) {
            throw exception(PAY_ORDER_NOT_FOUND);
        }
        if (ObjectUtil.notEqual(PayOrderStatusEnum.WAITING.getStatus(), order.getStatus())) {
            throw exception(PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        if (ObjectUtil.equal(order.getPrice(), payPrice)) {
            return;
        }

        // TODO 芋艿：应该 new 出来更新
        order.setPrice(payPrice);
        signMapper.updateById(order);
    }

    @Override
    public PaySignExtensionDO getSignExtension(Long id) {
        return signExtensionMapper.selectById(id);
    }

    @Override
    public PaySignExtensionDO getSignExtensionByNo(String no) {
        return signExtensionMapper.selectByNo(no);
    }

    @Override
    public int syncSign(LocalDateTime minCreateTime) {
        // 1. 查询指定创建时间内的待支付订单
        List<PaySignExtensionDO> signExtensions = signExtensionMapper.selectListByStatusAndCreateTimeGe(
                PayOrderStatusEnum.WAITING.getStatus(), minCreateTime);
        if (CollUtil.isEmpty(signExtensions)) {
            return 0;
        }
        // 2. 遍历执行
        int count = 0;
        for (PaySignExtensionDO signExtension : signExtensions) {
            count += syncSign(signExtension) ? 1 : 0;
        }
        return count;
    }

    /**
     * 同步单个支付拓展单
     *
     * @param signExtension 支付拓展单
     * @return 是否已支付
     */
    private boolean syncSign(PaySignExtensionDO signExtension) {
        try {
            // 1.1 查询支付订单信息
            PayClient payClient = channelService.getPayClient(signExtension.getChannelId());
            if (payClient == null) {
                log.error("[syncOrder][渠道编号({}) 找不到对应的支付客户端]", signExtension.getChannelId());
                return false;
            }
            PayOrderRespDTO respDTO = payClient.getOrder(signExtension.getNo());
            // 1.2 回调支付结果
            notifySign(signExtension.getChannelId(), respDTO);

            // 2. 如果是已支付，则返回 true
            return PayOrderStatusRespEnum.isSuccess(respDTO.getStatus());
        } catch (Throwable e) {
            log.error("[syncOrder][orderExtension({}) 同步支付状态异常]", signExtension.getId(), e);
            return false;
        }
    }

    @Override
    public int expireSign() {
        // 1. 查询过期的待支付订单
        List<PaySignDO> orders = signMapper.selectListByStatusAndExpireTimeLt(
                PayOrderStatusEnum.WAITING.getStatus(), LocalDateTime.now());
        if (CollUtil.isEmpty(orders)) {
            return 0;
        }

        // 2. 遍历执行
        int count = 0;
        for (PaySignDO signDO : orders) {
            count += expireSign(signDO) ? 1 : 0;
        }
        return count;
    }

    /**
     * 同步单个支付单
     *
     * @param sign 签约记录
     * @return 是否已过期
     */
    private boolean expireSign(PaySignDO sign) {
        try {
            // 1. 需要先处理关联的支付拓展单，避免错误的过期已支付 or 已退款的订单
            List<PaySignExtensionDO> signExtensions = signExtensionMapper.selectListBySignId(sign.getId());
            for (PaySignExtensionDO orderExtension : signExtensions) {
                if (PayOrderStatusEnum.isClosed(orderExtension.getStatus())) {
                    continue;
                }
                // 情况一：校验数据库中的 orderExtension 是不是已支付
                if (PayOrderStatusEnum.isSuccess(orderExtension.getStatus())) {
                    log.error("[expireOrder][order({}) 的 extension({}) 已支付，可能是数据不一致]",
                            sign.getId(), orderExtension.getId());
                    return false;
                }
                // 情况二：调用三方接口，查询支付单状态，是不是已支付/已退款
                PayClient payClient = channelService.getPayClient(orderExtension.getChannelId());
                if (payClient == null) {
                    log.error("[expireOrder][渠道编号({}) 找不到对应的支付客户端]", orderExtension.getChannelId());
                    return false;
                }
                PayOrderRespDTO respDTO = payClient.getOrder(orderExtension.getNo());
                if (PayOrderStatusRespEnum.isRefund(respDTO.getStatus())) {
                    // 补充说明：按道理，应该是 WAITING => SUCCESS => REFUND 状态，如果直接 WAITING => REFUND 状态，说明中间丢了过程
                    // 此时，需要人工介入，手工补齐数据，保持 WAITING => SUCCESS => REFUND 的过程
                    log.error("[expireOrder][extension({}) 的 PayOrderRespDTO({}) 已退款，可能是回调延迟]",
                            orderExtension.getId(), toJsonString(respDTO));
                    return false;
                }
                if (PayOrderStatusRespEnum.isSuccess(respDTO.getStatus())) {
                    notifySign(orderExtension.getChannelId(), respDTO);
                    return false;
                }
                // 兜底逻辑：将支付拓展单更新为已关闭
                PaySignExtensionDO updateObj = new PaySignExtensionDO().setStatus(PaySignStatusEnum.CLOSED.getStatus())
                        .setChannelNotifyData(toJsonString(respDTO));
                if (signExtensionMapper.updateByIdAndStatus(orderExtension.getId(), PaySignStatusEnum.WAITING.getStatus(),
                        updateObj) == 0) {
                    log.error("[expireOrder][extension({}) 更新为签约关闭失败]", orderExtension.getId());
                    return false;
                }
                log.info("[expireOrder][extension({}) 更新为签约关闭成功]", orderExtension.getId());
            }

            // 2. 都没有上述情况，可以安心更新为已关闭
            PaySignDO updateObj = new PaySignDO().setStatus(PaySignStatusEnum.CLOSED.getStatus());
            if (signMapper.updateByIdAndStatus(sign.getId(), sign.getStatus(), updateObj) == 0) {
                log.error("[expireOrder][order({}) 更新为支付关闭失败]", sign.getId());
                return false;
            }
            log.info("[expireOrder][order({}) 更新为支付关闭失败]", sign.getId());
            return true;
        } catch (Throwable e) {
            log.error("[expireOrder][order({}) 过期订单异常]", sign.getId(), e);
            return false;
        }
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PaySignServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
