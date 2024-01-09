package cn.iocoder.yudao.module.pay.service.sign;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.framework.pay.core.client.PayClient;
import cn.iocoder.yudao.framework.pay.core.client.dto.agreement.PayAgreementRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.agreement.PayAgreementUnifiedReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import cn.iocoder.yudao.module.pay.convert.sign.PaySignConvert;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.channel.PayChannelDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignExtensionDO;
import cn.iocoder.yudao.module.pay.dal.mysql.sign.PaySignExtensionMapper;
import cn.iocoder.yudao.module.pay.dal.redis.no.PayNoRedisDAO;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.enums.sign.PaySignStatusEnum;
import cn.iocoder.yudao.module.pay.framework.pay.config.PayProperties;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import cn.iocoder.yudao.module.pay.service.channel.PayChannelService;
import cn.iocoder.yudao.module.pay.service.order.PayOrderService;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;

import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.*;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.pay.dal.mysql.sign.PaySignMapper;

import java.time.LocalDateTime;

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
        sign = PaySignConvert.INSTANCE.convert(reqDTO).setAppId(app.getId())
                // 商户相关字段
                .setNotifyUrl(app.getOrderNotifyUrl())
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
                .setChannelId(channel.getId()).setChannelCode(channel.getCode())
                .setStatus(PayOrderStatusEnum.WAITING.getStatus());
        paySignExtensionMapper.insert(signExtensionDO);

        // 创建支付 获取支付单号
        Long order = payOrderService.createOrder(
                new PayOrderCreateReqDTO()
                        .setAppId(signDO.getAppId())
                        .setUserIp(userIp)
                .setMerchantOrderId(signDO.getMerchantSignId()).setSubject(signDO.getSubject()).setBody(signDO.getBody()).setPrice(signDO.getFirstPrice()).setExpireTime(signDO.getExpireTime()).setSignId(signDO.getId()));
        PayOrderSubmitRespVO payOrderSubmitRespVO = payOrderService.submitOrder(PaySignConvert.INSTANCE.convert(reqVO).setId(order).setIsSign(true), userIp);
        // new PayOrderSubmitReqVO().setId(order).setChannelCode(signDO.getChannelCode()).setIsSign(true),

        // 3. 调用三方接口
        PayAgreementUnifiedReqDTO unifiedReqDTO = PaySignConvert.INSTANCE.convert2(reqVO, userIp)
                // 商户相关的字段
                .setOutTradeNo(payOrderSubmitRespVO.getDisplayContent()) // 注意，此处使用的是 PayOrderExtensionDO.no 属性！
                // 订单相关字段
                .setTotalAmount(signDO.getFirstPrice())
                .setSubject(signDO.getSubject())
                .setBody(signDO.getBody())
                .setExpireTime(signDO.getExpireTime())
                .setExternalAgreementNo(signExtensionDO.getNo())
                .setPeriodType(signDO.getPeriodUnit())
                .setPeriod(Long.valueOf(signDO.getPeriod()))
                .setExecuteTime(BuildExecuteTime(payProperties.getFixedDeductionTime()))
                .setSingleAmount(signDO.getPrice())
                .setSignNotifyUrl(genChannelSignNotifyUrl(channel))
                ;
        PayAgreementRespDTO unifiedAgreement = payClient.unifiedAgreement(unifiedReqDTO);

        // 4. 如果调用直接支付成功，则直接更新支付单状态为成功。例如说：付款码支付，免密支付时，就直接验证支付成功
        if (unifiedAgreement != null) {
            // getSelf().notifyOrder(channel, unifiedAgreement);
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

    private LocalDateTime BuildExecuteTime(Integer fixedDeductionTime) {
        LocalDateTime currentDate = LocalDateTimeUtil.now();
        return currentDate.withDayOfMonth(fixedDeductionTime);
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

    @Override
    public PageResult<PaySignDO> getSignPage(SignPageReqVO pageReqVO) {
        return paySignMapper.selectPage(pageReqVO);
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


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PaySignServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }




}
