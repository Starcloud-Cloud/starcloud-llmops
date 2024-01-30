package cn.iocoder.yudao.module.pay.service.sign;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.framework.pay.core.client.PayClient;
import cn.iocoder.yudao.framework.pay.core.client.dto.agreement.PayAgreementRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.agreement.PayAgreementUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.enums.agreement.PayAgreementStatusRespEnum;
import cn.iocoder.yudao.framework.pay.core.enums.order.PayOrderStatusRespEnum;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import cn.iocoder.yudao.module.pay.convert.sign.PaySignConvert;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.channel.PayChannelDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderExtensionDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignExtensionDO;
import cn.iocoder.yudao.module.pay.dal.mysql.sign.PaySignExtensionMapper;
import cn.iocoder.yudao.module.pay.dal.redis.no.PayNoRedisDAO;
import cn.iocoder.yudao.module.pay.enums.notify.PayNotifyTypeEnum;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.enums.sign.PaySignStatusEnum;
import cn.iocoder.yudao.module.pay.framework.pay.config.PayProperties;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import cn.iocoder.yudao.module.pay.service.channel.PayChannelService;
import cn.iocoder.yudao.module.pay.service.notify.PayNotifyService;
import cn.iocoder.yudao.module.pay.service.order.PayOrderService;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.*;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.pay.dal.mysql.sign.PaySignMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString;
import static cn.iocoder.yudao.module.pay.enums.ErrorCodeConstants.*;

/**
 * 支付签约
 * Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
@Slf4j
public class PaySignServiceImpl implements PaySignService {


    @Resource
    private PaySignMapper paySignMapper;

    @Resource
    private PaySignExtensionMapper paySignExtensionMapper;
    @Resource
    private PayAppService appService;
    @Resource
    private PayChannelService channelService;

    @Resource
    private PayNoRedisDAO noRedisDAO;

    @Resource
    private PayProperties payProperties;

    @Resource
    private PayOrderService payOrderService;

    @Resource
    private PayNotifyService notifyService;


    @Override
    public Long createSign(PaySignCreateReqDTO reqDTO) {
        // 校验 App
        PayAppDO app = appService.validPayApp(reqDTO.getAppId());

        // 查询对应的支付交易单是否已经存在。如果是，则直接返回
        PaySignDO sign = paySignMapper.selectByAppIdAndMerchantSignId(
                reqDTO.getAppId(), reqDTO.getMerchantSignId());
        if (sign != null) {
            log.warn("[createSign][appId({}) merchantOrderId({}) 已经存在对应的签约记录({})]", sign.getAppId(),
                    sign.getMerchantSignId(), toJsonString(sign)); // 理论来说，不会出现这个情况
            return sign.getId();
        }

        // 创建支付交易单
        sign = PaySignConvert.INSTANCE.convert(reqDTO)
                .setAppId(app.getId())
                .setPayTime(reqDTO.getPayTime())
                // 商户相关字段
                .setNotifyUrl(app.getSignNotifyUrl())
                // 订单相关字段
                .setStatus(PaySignStatusEnum.WAITING.getStatus());
        paySignMapper.insert(sign);
        return sign.getId();
    }


    @Override // 注意，这里不能添加事务注解，避免调用支付渠道失败时，将 PayOrderExtensionDO 回滚了
    public PaySignSubmitRespVO submitSign(PaySignSubmitReqVO reqVO, String userIp) {

        // 1.1 获得 PayOrderDO ，并校验其是否存在
        PaySignDO signDO = validateSignCanSubmit(reqVO.getId());
        // 1.32 校验支付渠道是否有效
        PayChannelDO channel = validateSignChannelCanSubmit(signDO.getAppId(), reqVO.getChannelCode());
        PayClient payClient = channelService.getPayClient(channel.getId());

        // 2. 插入 PayOrderExtensionDO
        String no = noRedisDAO.generateSignNO(payProperties.getSignNoPrefix());
        PaySignExtensionDO signExtensionDO = PaySignConvert.INSTANCE.convert(reqVO, userIp)
                .setSignId(signDO.getId()).setNo(no)
                .setChannelId(channel.getId())
                .setChannelCode(channel.getCode())
                .setStatus(PayOrderStatusEnum.WAITING.getStatus());
        paySignExtensionMapper.insert(signExtensionDO);

        // // 创建支付 获取支付单号
        // Long order = payOrderService.createOrder(
        //         new PayOrderCreateReqDTO()
        //                 .setAppId(signDO.getAppId())
        //                 .setUserIp(userIp)
        //         .setMerchantOrderId(signDO.getMerchantSignId()).setSubject(signDO.getSubject()).setBody(signDO.getBody()).setPrice(signDO.getFirstPrice()).setExpireTime(signDO.getExpireTime()).setSignId(signDO.getId()));
        // PayOrderSubmitRespVO payOrderSubmitRespVO = payOrderService.submitOrder(PaySignConvert.INSTANCE.convert(reqVO).setId(order).setIsSign(true), userIp);
        //
        // 3. 调用三方接口
        PayAgreementUnifiedReqDTO unifiedReqDTO = PaySignConvert.INSTANCE.convert2(reqVO, userIp)
                // 商户相关的字段
                // .setOutTradeNo(payOrderSubmitRespVO.getDisplayContent()) // 注意，此处使用的是 PayOrderExtensionDO.no 属性！
                // 订单相关字段
                .setTotalAmount(signDO.getPrice())
                .setSubject(signDO.getSubject())
                .setBody(signDO.getBody())
                .setExpireTime(signDO.getExpireTime())
                .setExternalAgreementNo(signExtensionDO.getNo())
                .setPeriodType(buildPeriodType(signDO.getPeriodUnit()))
                .setPeriod(Long.valueOf(signDO.getPeriod()))
                // .setExecuteTime(BuildExecuteTime(payProperties.getFixedDeductionTime()))
                .setExecuteTime(signDO.getPayTime())
                .setSingleAmount(signDO.getPrice())
                .setSignNotifyUrl(genChannelSignNotifyUrl(channel));
        PayAgreementRespDTO unifiedAgreement = payClient.unifiedPageAgreement(unifiedReqDTO);

        // 4. 如果调用直接支付成功，则直接更新支付单状态为成功。例如说：付款码支付，免密支付时，就直接验证支付成功
        if (unifiedAgreement != null) {
            // getSelf().notifyOrder(channel, unifiedPayAgreement);
            // 如有渠道错误码，则抛出业务异常，提示用户
            if (StrUtil.isNotEmpty(unifiedAgreement.getChannelErrorCode())) {
                throw exception(PAY_ORDER_SUBMIT_CHANNEL_ERROR, unifiedAgreement.getChannelErrorCode(),
                        unifiedAgreement.getChannelErrorMsg());
            }
            // 此处需要读取最新的状态
            signDO = paySignMapper.selectById(signDO.getId());
        }
        return PaySignConvert.INSTANCE.convert(signDO, unifiedAgreement);
    }

    private String buildPeriodType(Integer periodUnit) {
        TimeRangeTypeEnum timeRange = TimeRangeTypeEnum.getByType(periodUnit);
        switch (timeRange) {
            case DAY:
                return "DAY";
            case MONTH:
                return "MONTH";
        }
        throw new RuntimeException("周期异常，请联系管理员");

    }


    @Override
    public void updateSign(SignSaveReqVO updateReqVO) {
        // 校验存在
        validateSignExists(updateReqVO.getId());
        // 更新
        PaySignDO updateObj = BeanUtils.toBean(updateReqVO, PaySignDO.class);
        paySignMapper.updateById(updateObj);
    }

    @Override
    public void deleteSign(Long id) {
        // 校验存在
        validateSignExists(id);
        // 删除
        paySignMapper.deleteById(id);
    }

    private void validateSignExists(Long id) {
        if (paySignMapper.selectById(id) == null) {
            throw exception(PAY_SIGN_NOT_EXISTS);
        }
    }

    @Override
    public PaySignDO getSign(Long id) {
        return paySignMapper.selectById(id);
    }

    /**
     * 获得支付签约
     *
     * @return 支付签约
     */
    @Override
    public PaySignDO getSignByMerchantSignId(Long appId, String merchantSignId) {
        return paySignMapper.selectByAppIdAndMerchantSignId(appId, merchantSignId);
    }

    @Override
    public PageResult<PaySignDO> getSignPage(SignPageReqVO pageReqVO) {
        return paySignMapper.selectPage(pageReqVO);
    }

    /**
     * @param minCreateTime
     * @return
     */
    @Override
    public int syncSign(LocalDateTime minCreateTime) {
        // 1. 查询指定创建时间内的待签约订单
        List<PaySignExtensionDO> paySignExtensionDOS = paySignExtensionMapper.selectListByStatusAndCreateTimeGe(
                PayOrderStatusEnum.WAITING.getStatus(), minCreateTime);
        if (CollUtil.isEmpty(paySignExtensionDOS)) {
            return 0;
        }
        // 2. 遍历执行
        int count = 0;
        for (PaySignExtensionDO signExtensionDO : paySignExtensionDOS) {
            // count += syncSignStatus(signExtensionDO) ? 1 : 0;
        }
        return count;
    }

    /**
     * @return
     */
    @Override
    public int syncSignPay() {
        // 1.0 获取签约成功的记录
        List<PaySignDO> paySignDOS = paySignMapper.selectIsSignSuccess();
        if (paySignDOS.isEmpty()) {
            return 0;
        }
        List<PaySignDO> waitePaySigns = paySignDOS
                .stream()
                .filter(paySignDO ->
                        LocalDateTimeUtil
                                .isIn(LocalDate.now().atStartOfDay(), paySignDO.getPayTime().minusDays(5L).atStartOfDay(),
                                        LocalDateTimeUtil.endOfDay(paySignDO.getPayTime().atStartOfDay())))
                .collect(Collectors.toList());
        if (waitePaySigns.isEmpty()) {
            return 0;
        }

        // 2. 遍历执行扣款
        int count = 0;
        for (PaySignDO waitePaySign : waitePaySigns) {
            count += syncSignPay(waitePaySign) ? 1 : 0;
        }
        return count;
    }

    /**
     * 同步签约状态
     *
     * @return
     */
    @Override
    public int syncSignStatus() {
        // 1.0 获取签约成功的记录
        List<PaySignDO> paySignDOS = paySignMapper.selectIsSignSuccess();
        if (CollUtil.isEmpty(paySignDOS)) {
            return 0;
        }
        // 2. 遍历执行 查询状态的操作
        int count = 0;
        for (PaySignDO signDO : paySignDOS) {
            count += syncSignStatus(signDO) ? 1 : 0;
        }
        return count;
    }

    /**
     * @param channelId
     * @param notify
     */
    @Override
    public void notifySignStatus(Long channelId, PayAgreementRespDTO notify) {
        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(channelId);

        // 更新支付订单为已支付
        TenantUtils.execute(channel.getTenantId(), () -> {
            // 情况一：签约成功的回调
            if (PayOrderStatusRespEnum.isSuccess(notify.getStatus())) {
                notifySignSuccess(channel, notify);
                return;
            }
            // 情况二：签约失败的回调
            if (PayOrderStatusRespEnum.isClosed(notify.getStatus())) {
                notifySignClosed(channel, notify);
            }
        });

    }

    /**
     * 同步签约支付
     *
     * @param signNo
     * @return
     */
    @Override
    public Object getSignRecord(String signNo) {
            PayClient payClient = channelService.getPayClient(22L);
            if (payClient == null) {

            }
            PayAgreementRespDTO respDTO = payClient.getAgreement(signNo);
           return respDTO.getRawData();
    }

    /**
     * 同步签约支付
     *
     * @param signNo
     * @return
     */
    @Override
    public Object UpdateSign(String signNo,String deductTime) {
        PayClient payClient = channelService.getPayClient(22L);
        if (payClient == null) {

        }
        PayAgreementRespDTO respDTO = payClient.updateAgreement(signNo,deductTime);
        return respDTO.getRawData();
    }


    private boolean syncSignPay(PaySignDO waitePaySign) {
        try {
            // 1.1 获取支付渠道信息
            PayClient payClient = channelService.getPayClient(waitePaySign.getChannelId());
            if (payClient == null) {
                log.error("[syncSignPay][渠道编号({}) 找不到对应的支付客户端]", waitePaySign.getChannelId());
                return false;
            }

            // 创建订单
            PayOrderUnifiedReqDTO payOrderUnifiedReqDTO = createSignPayOrder(waitePaySign);

            // 提交支付
            PayOrderRespDTO respDTO = payClient.unifiedAgreementPay(payOrderUnifiedReqDTO);

            // 1.2 回调支付结果
            notifySignPayResult(waitePaySign, respDTO);

            return PayOrderStatusRespEnum.isSuccess(respDTO.getStatus());
        } catch (Throwable e) {
            log.error("[syncSignPay][PaySignDO({}) 签约支付状态异常]", waitePaySign.getId(), e);
            return false;
        }
    }

    private void notifySignPayResult(PaySignDO signDO, PayOrderRespDTO notify) {

        PayChannelDO channel = channelService.validPayChannel(signDO.getAppId(), signDO.getChannelCode());
        // 更新支付订单为已支付
        TenantUtils.execute(channel.getTenantId(), () -> {
            payOrderService.notifyOrder(channel.getId(), notify);
            getSelf().notifySignPayResult(signDO, channel, notify);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    // 注意，如果是方法内调用该方法，需要通过 getSelf().notifyPayOrder(channel, notify) 调用，否则事务不生效
    public void notifySignPayResult(PaySignDO signDO, PayChannelDO channel, PayOrderRespDTO notify) {
        // 情况一：支付成功的回调
        if (PayOrderStatusRespEnum.isSuccess(notify.getStatus())) {
            notifySignPaySuccess(signDO, channel, notify);
            return;
        }
        //// 情况二：支付失败的回调
        // if (PayOrderStatusRespEnum.isClosed(notify.getStatus())) {
        //    notifySignPayClosed(channel, notify);
        //}
    }

    private void notifySignPaySuccess(PaySignDO signDO, PayChannelDO channel, PayOrderRespDTO notify) {
        // 2. 更新 PayOrderDO 支付成功
        Boolean paid = updateSignPaySuccess(signDO, channel, notify);
        if (paid) { // 如果之前已经成功回调，则直接返回，不用重复记录支付通知记录；例如说：支付平台重复回调
            return;
        }
    }

    private Boolean updateSignPaySuccess(PaySignDO signDO, PayChannelDO channel, PayOrderRespDTO notify) {

        //  更新 PaySignDO
        int updateCounts = paySignMapper.updateById(signDO.setPayTime(buildSignPayTime(signDO.getPayTime().plusMonths(1), TimeRangeTypeEnum.MONTH)));
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        log.info("[updateOrderExtensionSuccess][order({}) 更新为已支付]", signDO.getId());
        return true;
    }

    private PayOrderUnifiedReqDTO createSignPayOrder(PaySignDO signDO) {

        // 创建支付 获取支付单号
        Long orderId = payOrderService.createOrder(
                new PayOrderCreateReqDTO()
                        .setAppId(signDO.getAppId())
                        .setUserIp(signDO.getUserIp())
                        .setMerchantOrderId(signDO.getId() + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .setSubject(signDO.getSubject())
                        .setBody(signDO.getBody())
                        .setPrice(signDO.getPrice())
                        .setExpireTime(signDO.getExpireTime())
                // .setSignId(signDO.getId())
        );

        PayOrderDO order = payOrderService.getOrder(orderId);
        PayOrderSubmitRespVO payOrderSubmitRespVO = payOrderService.submitOrder(new PayOrderSubmitReqVO()
                .setId(orderId)
                .setChannelCode(signDO.getChannelCode())
                .setIsSign(true), signDO.getUserIp());


        PayOrderExtensionDO orderExtension = payOrderService.getOrderExtensionByNo(payOrderSubmitRespVO.getDisplayContent());
        // 支付参数

        PayChannelDO channel = channelService.validPayChannel(order.getAppId(), signDO.getChannelCode());

        return new PayOrderUnifiedReqDTO()
                .setUserIp(order.getUserIp())
                // 商户相关的字段
                .setOutTradeNo(orderExtension.getNo()) // 注意，此处使用的是 PayOrderExtensionDO.no 属性！
                .setAgreementNo(signDO.getChannelSignNo())
                .setSubject(order.getSubject())
                .setBody(order.getBody())
                .setNotifyUrl(genChannelOrderNotifyUrl(channel))
                // 订单相关字段
                .setPrice(order.getPrice())
                .setExpireTime(order.getExpireTime());

    }

    private boolean syncSignStatus(PaySignDO signDO) {
        // try {
        //     // 1.1 查询支付订单信息
        //     PayClient payClient = channelService.getPayClient(paySignExtensionDO.getChannelId());
        //     if (payClient == null) {
        //         log.error("[syncSignStatus][渠道编号({}) 找不到对应的支付客户端]", paySignExtensionDO.getChannelId());
        //         return false;
        //     }
        //     PayAgreementRespDTO respDTO = payClient.getAgreement(paySignExtensionDO.getNo());
        //     // 1.2 回调支付结果
        //     notifySign(paySignExtensionDO.getChannelId(), respDTO);
        //
        //     // 2. 如果是已支付，则返回 true
        //     return PayOrderStatusRespEnum.isSuccess(respDTO.getStatus());
        // } catch (Throwable e) {
        //     log.error("[syncSignStatus][orderExtension({}) 同步支付状态异常]", paySignExtensionDO.getId(), e);
        //     return false;
        // }
        return false;
    }

    private void notifySign(Long channelId, PayAgreementRespDTO respDTO) {
        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(channelId);
        // 更新支付订单为已支付
        TenantUtils.execute(channel.getTenantId(), () -> getSelf().notifySign(channel, respDTO));
    }

    @Transactional(rollbackFor = Exception.class)
    // 注意，如果是方法内调用该方法，需要通过 getSelf().notifyPayOrder(channel, notify) 调用，否则事务不生效
    public void notifySign(PayChannelDO channel, PayAgreementRespDTO notify) {
        // 情况一：签约成功的回调
        if (PayAgreementStatusRespEnum.isSuccess(notify.getStatus())) {
            notifySignSuccess(channel, notify);
            return;
        }
        // 情况二：取消签约的回调
        if (PayAgreementStatusRespEnum.isClosed(notify.getStatus())) {
            notifySignClosed(channel, notify);
        }
        // 情况三：WAITING：无需处理
        // 情况四：REFUND：通过退款回调处理
    }

    private void notifySignClosed(PayChannelDO channel, PayAgreementRespDTO notify) {
        PaySignExtensionDO paySignExtensionDO = updateSignExtensionClosed(channel, notify);
        updateSignClosed(channel, paySignExtensionDO, notify);

        // 3. 插入支付通知记录
        notifyService.createPayNotifyTask(PayNotifyTypeEnum.SIGN_CLOSE.getType(),
                paySignExtensionDO.getSignId());
    }

    private PaySignExtensionDO updateSignExtensionClosed(PayChannelDO channel, PayAgreementRespDTO notify) {
        // 1. 查询 PayOrderExtensionDO
        PaySignExtensionDO signExtensionDO = paySignExtensionMapper.selectByNo(notify.getAgreementNo());
        if (signExtensionDO == null) {
            throw exception(PAY_SIGN_NOT_EXISTS);
        }
        if (PaySignStatusEnum.isClosed(signExtensionDO.getStatus())) { // 如果已经是关闭，直接返回，不用重复更新
            log.info("[updateSignExtensionClosed][signExtension({}) 已经是签约关闭，无需更新]", signExtensionDO.getId());
            return signExtensionDO;
        }

        if (!ObjectUtils.equalsAny(signExtensionDO.getStatus(), PaySignStatusEnum.WAITING.getStatus(), PaySignStatusEnum.SUCCESS.getStatus())) { // 校验状态，必须是待支付
            throw exception(PAY_SIGN_STATUS_IS_NOT_WAITING);
        }

        // 2. 更新 PayOrderExtensionDO
        int updateCounts = paySignExtensionMapper.updateByIdAndStatus(signExtensionDO.getId(), signExtensionDO.getStatus(),
                PaySignExtensionDO.builder().status(PaySignStatusEnum.CLOSED.getStatus()).channelNotifyData(toJsonString(notify))
                        .channelErrorCode(notify.getChannelErrorCode()).channelErrorMsg(notify.getChannelErrorMsg()).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(PAY_SIGN_STATUS_IS_NOT_WAITING);
        }
        log.info("[updateSignExtensionClosed][signExtension({}) 更新为支付关闭]", signExtensionDO.getId());
        return signExtensionDO;
    }

    public void notifySignSuccess(PayChannelDO channel, PayAgreementRespDTO notify) {
        // 1. 更新 PayOrderExtensionDO 支付成功
        PaySignExtensionDO signExtensionDO = updateSignSuccess(notify);
        // 2. 更新 PayOrderDO 支付成功
        Boolean paid = updateSignSuccess(channel, signExtensionDO, notify);
        if (paid) { // 如果之前已经成功回调，则直接返回，不用重复记录支付通知记录；例如说：支付平台重复回调
            return;
        }

        // 3. 插入支付通知记录
        notifyService.createPayNotifyTask(PayNotifyTypeEnum.SIGN_SUCCESS.getType(),
                signExtensionDO.getSignId());
    }

    /**
     * 更新 PayOrderDO 支付成功
     *
     * @param channel         支付渠道
     * @param signExtensionDO 支付拓展单
     * @param notify          通知回调
     * @return 是否之前已经成功回调
     */
    private Boolean updateSignSuccess(PayChannelDO channel, PaySignExtensionDO signExtensionDO,
                                      PayAgreementRespDTO notify) {
        // 1. 判断 PayOrderDO 是否处于待支付
        PaySignDO signDO = paySignMapper.selectById(signExtensionDO.getSignId());
        if (signDO == null) {
            throw exception(PAY_SIGN_NOT_EXISTS);
        }
        if (PaySignStatusEnum.isSuccess(signDO.getStatus()) // 如果已经是成功，直接返回，不用重复更新
                && Objects.equals(signDO.getExtensionId(), signExtensionDO.getId())) {
            log.info("[updateOrderExtensionSuccess][order({}) 已经是已签约，无需更新]", signDO.getId());
            return true;
        }
        if (!PaySignStatusEnum.WAITING.getStatus().equals(signDO.getStatus())) { // 校验状态，必须是待签约
            throw exception(PAY_SIGN_STATUS_IS_NOT_WAITING);
        }

        // 2. 更新 PayOrderDO
        int updateCounts = paySignMapper.updateByIdAndStatus(signDO.getId(), PaySignStatusEnum.WAITING.getStatus(),
                PaySignDO.builder()
                        .status(PayOrderStatusEnum.SUCCESS.getStatus())
                        .channelId(channel.getId())
                        .channelCode(channel.getCode())
                        .contractTime(notify.getSignTime())
                        .extensionId(signExtensionDO.getId())
                        .no(signExtensionDO.getNo())
                        .channelSignNo(notify.getChannelAgreementNo())
                        .channelUserId(notify.getChannelUserId())
                        .channelFeeRate(channel.getFeeRate())
                        .channelFeePrice(MoneyUtils.calculateRatePrice(signDO.getPrice(), channel.getFeeRate()))
                        .build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(PAY_SIGN_STATUS_IS_NOT_WAITING);
        }
        log.info("[updateSignSuccess][sign({}) 更新为签约成功]", signDO.getId());
        return false;
    }


    private Boolean updateSignClosed(PayChannelDO channel, PaySignExtensionDO signExtensionDO, PayAgreementRespDTO notify) {
        // 1. 判断 PayOrderDO 是否处于待支付
        PaySignDO signDO = paySignMapper.selectById(signExtensionDO.getSignId());
        if (signDO == null) {
            throw exception(PAY_SIGN_NOT_EXISTS);
        }
        if (PaySignStatusEnum.isClosed(signDO.getStatus()) // 如果已经是成功，直接返回，不用重复更新
                && Objects.equals(signDO.getExtensionId(), signExtensionDO.getId())) {
            log.info("[updateSignClosed][sign({}) 已经是已关闭签约，无需更新]", signDO.getId());
            return true;
        }
        // 2. 更新 PayOrderDO
        int updateCounts = paySignMapper.updateByIdAndStatus(signDO.getId(), PaySignStatusEnum.SUCCESS.getStatus(),
                PaySignDO.builder()
                        .status(PayOrderStatusEnum.CLOSED.getStatus())
                        .closeTime(notify.getInvalidTime())
                        .build());
        if (updateCounts == 0) { // 校验状态，必须是成功
            throw exception(PAY_SIGN_IS_CLOSE);
        }
        log.info("[updateSignClosed][sign({}) 更新为已关闭]", signDO.getId());
        return false;
    }

    /**
     * 更新 PayOrderExtensionDO 支付成功
     *
     * @param notify 通知
     * @return PayOrderExtensionDO 对象
     */
    private PaySignExtensionDO updateSignSuccess(PayAgreementRespDTO notify) {
        // 1. 查询 signExtensionDO
        PaySignExtensionDO signExtensionDO = paySignExtensionMapper.selectByNo(notify.getAgreementNo());
        if (signExtensionDO == null) {
            throw exception(PAY_SIGN_NOT_EXISTS);
        }
        if (PaySignStatusEnum.isSuccess(signExtensionDO.getStatus())) { // 如果已经是成功，直接返回，不用重复更新
            log.info("[updateSignExtensionSuccess][orderExtension({}) 已经是已支付，无需更新]", signExtensionDO.getId());
            return signExtensionDO;
        }
        // if (ObjectUtil.notEqual(signExtensionDO.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
        //     throw exception(PAY_SIGN_STATUS_IS_NOT_WAITING);
        // }

        // 2. 更新 PayOrderExtensionDO
        int updateCounts = paySignExtensionMapper.updateByIdAndStatus(signExtensionDO.getId(), signExtensionDO.getStatus(),
                PaySignExtensionDO.builder().status(PaySignStatusEnum.SUCCESS.getStatus()).channelNotifyData(toJsonString(notify)).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(PAY_SIGN_STATUS_IS_NOT_WAITING);
        }
        log.info("[updateSignExtensionSuccess][signExtension({}) 更新为已支付]", signExtensionDO.getId());
        return signExtensionDO;
    }

    private PaySignDO validateSignCanSubmit(Long id) {
        PaySignDO signDO = paySignMapper.selectById(id);
        if (signDO == null) { // 是否存在
            throw exception(PAY_SIGN_NOT_EXISTS);
        }
        if (PayOrderStatusEnum.isSuccess(signDO.getStatus())) { // 校验状态，发现已支付
            throw exception(PAY_SIGN_STATUS_IS_SUCCESS);
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(signDO.getStatus())) { // 校验状态，必须是待支付
            throw exception(PAY_SIGN_STATUS_IS_NOT_WAITING);
        }
        if (LocalDateTimeUtils.beforeNow(signDO.getExpireTime())) { // 校验是否过期
            throw exception(PAY_SIGN_IS_EXPIRED);
        }

        // 【重要】校验是否支付拓展单已支付，只是没有回调、或者数据不正常
        validateSignActuallyPaid(id);
        return signDO;
    }

    /**
     * 校验支付订单实际已支付
     *
     * @param id 支付编号
     */
    @VisibleForTesting
    void validateSignActuallyPaid(Long id) {
        // List<PayOrderExtensionDO> orderExtensions = orderExtensionMapper.selectListByOrderId(id);
        // orderExtensions.forEach(orderExtension -> {
        //     // 情况一：校验数据库中的 orderExtension 是不是已支付
        //     if (PayOrderStatusEnum.isSuccess(orderExtension.getStatus())) {
        //         log.warn("[validateOrderCanSubmit][order({}) 的 extension({}) 已支付，可能是数据不一致]",
        //                 id, orderExtension.getId());
        //         throw exception(PAY_ORDER_EXTENSION_IS_PAID);
        //     }
        //     // 情况二：调用三方接口，查询支付单状态，是不是已支付
        //     PayClient payClient = channelService.getPayClient(orderExtension.getChannelId());
        //     if (payClient == null) {
        //         log.error("[validateOrderCanSubmit][渠道编号({}) 找不到对应的支付客户端]", orderExtension.getChannelId());
        //         return;
        //     }
        //     PayOrderRespDTO respDTO = payClient.getOrder(orderExtension.getNo());
        //     if (respDTO != null && PayOrderStatusRespEnum.isSuccess(respDTO.getStatus())) {
        //         log.warn("[validateOrderCanSubmit][order({}) 的 PayOrderRespDTO({}) 已支付，可能是回调延迟]",
        //                 id, toJsonString(respDTO));
        //         throw exception(PAY_ORDER_EXTENSION_IS_PAID);
        //     }
        // });
    }

    private PayChannelDO validateSignChannelCanSubmit(Long appId, String channelCode) {
        // 校验 App
        appService.validPayApp(appId);
        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(appId, channelCode);
        PayClient client = channelService.getPayClient(channel.getId());
        if (client == null) {
            log.error("[validateSignChannelCanSubmit][渠道编号({}) 找不到对应的签约客户端]", channel.getId());
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
    private String genChannelSignNotifyUrl(PayChannelDO channel) {
        return payProperties.getSignNotifyUrl() + "/" + channel.getId();
    }

    private String genChannelOrderNotifyUrl(PayChannelDO channel) {
        return payProperties.getOrderNotifyUrl() + "/" + channel.getId();
    }


    private LocalDate buildSignPayTime(LocalDate date, TimeRangeTypeEnum timeRange) {
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


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PaySignServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }


}
