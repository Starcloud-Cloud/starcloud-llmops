package com.starcloud.ops.business.order.service.sign;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.pay.config.PayProperties;
import cn.iocoder.yudao.framework.pay.core.client.PayClient;
import cn.iocoder.yudao.framework.pay.core.client.PayClientFactory;
import cn.iocoder.yudao.framework.pay.core.client.dto.notify.PayNotifyReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.notify.PaySignNotifyRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayResponse;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.limits.enums.ProductEnum;
import com.starcloud.ops.business.limits.enums.ProductSignEnum;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import com.starcloud.ops.business.order.api.sign.dto.PaySignCreateReqDTO;
import com.starcloud.ops.business.order.api.sign.dto.PaySignSubmitReqDTO;
import com.starcloud.ops.business.order.api.sign.dto.PaySignToPayCreatReqDTO;
import com.starcloud.ops.business.order.api.sign.dto.PaySignToPaySubmitReqDTO;
import com.starcloud.ops.business.order.controller.admin.sign.vo.SignPayResultReqVO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayChannelDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderExtensionDO;
import com.starcloud.ops.business.order.dal.dataobject.sign.PaySignDO;
import com.starcloud.ops.business.order.dal.mysql.order.PayOrderExtensionMapper;
import com.starcloud.ops.business.order.dal.mysql.sign.PaySignMapper;
import com.starcloud.ops.business.order.enums.ErrorCodeConstants;
import com.starcloud.ops.business.order.enums.order.PayOrderStatusEnum;
import com.starcloud.ops.business.order.enums.sign.PaySignStatusEnum;
import com.starcloud.ops.business.order.service.merchant.PayAppService;
import com.starcloud.ops.business.order.service.merchant.PayChannelService;
import com.starcloud.ops.business.order.service.order.PayOrderService;
import com.starcloud.ops.business.order.util.PaySeqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;
import static cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.framework.pay.core.enums.PayFrameworkErrorCodeConstants.PAY_EXCEPTION;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;
import static com.starcloud.ops.business.order.enums.ErrorCodeConstants.PAY_ORDER_NOT_FOUND;

@Service
@Validated
@Slf4j
public class PaySignServiceImpl implements PaySignService {

    @Resource
    private PayProperties payProperties;

    @Resource
    private PaySignMapper signMapper;

    @Resource
    private PayOrderService orderService;
    @Resource
    private PayAppService appService;
    @Resource
    private PayChannelService channelService;

    @Resource
    private PayClientFactory payClientFactory;

    @Resource
    private PayOrderExtensionMapper orderExtensionMapper;


    @Resource
    private UserBenefitsService userBenefitsService;

    /**
     * 获取订阅信息
     *
     * @param id id
     * @return 支付单编号
     */
    @Override
    public PaySignDO getPaySign(Long id) {
        return signMapper.selectById(id);
    }

    /**
     * 获取订阅信息
     *
     * @param merchantSignId id
     * @return 支付单编号
     */
    @Override
    public PaySignDO getPaySign(String merchantSignId) {
        return validatePaySignCanSubmit(merchantSignId);
    }

    /**
     * 创建支付单
     *
     * @param reqDTO 创建请求
     * @return 支付单编号
     */
    @Override
    public String createSign(PaySignCreateReqDTO reqDTO) {
        log.info("[创建订阅],用户[userId({})｜租户[({})｜开始创建订阅记录,产品为({})]", getLoginUserId(), getTenantId(), reqDTO.getProductCode());
        // 判断产品是否存在
        ProductEnum productEnum;
        try {
            productEnum = ProductEnum.getByCode(reqDTO.getProductCode());
        } catch (RuntimeException e) {
            throw new RuntimeException("产品不存在，请重新核对后提交");
        }

        // 根据产品获取订阅参数
        ProductSignEnum productSignEnum = productEnum.getProductSignEnum();
        if (ObjectUtil.isNull(productSignEnum)) {
            throw new RuntimeException("该产品不支持订阅");
        }

        // 校验 App
        PayAppDO app = appService.validPayApp(reqDTO.getAppId());

        // 查询对应的支付交易单是否已经存在。如果是，则直接返回
        PaySignDO signDO = signMapper.selectByAppIdAndMerchantSignId(
                reqDTO.getAppId(), reqDTO.getMerchantSignId());
        if (signDO != null) {
            log.warn("[createOrder][appId({}) merchantOrderId({}) 已经存在对应的签约记录({})]", signDO.getAppId(),
                    signDO.getMerchantSignId(), toJsonString(signDO)); // 理论来说，不会出现这个情况

            throw new RuntimeException("系统异常，请重新提交");
        }
        signDO = PaySignDO.builder()
                .merchantId(app.getMerchantId())
                .appId(app.getId())
                .merchantSignId(reqDTO.getMerchantSignId())
                .productCode(reqDTO.getProductCode())
                .productName(productEnum.getName())
                .amount(productSignEnum.getSingleAmount())
                .nextPay(DateUtil.date().toLocalDateTime())
                .status(PaySignStatusEnum.WAITING.getStatus())
                .userId(getLoginUserId().toString())
                .userIp(getClientIP())
                .build();

        signMapper.insert(signDO);
        return signDO.getMerchantSignId();
    }

    /**
     * 创建支付单
     *
     * @param reqDTO 创建请求
     * @return 支付单编号
     */
    @Override
    public String submitSign(PaySignSubmitReqDTO reqDTO) {


        // 封装签约参数
        JSONObject access_params = new JSONObject();
        access_params.put("channel", "QRCODE");

        JSONObject bizContent = new JSONObject();
        bizContent.put("product_code", "GENERAL_WITHHOLDING");
        bizContent.put("personal_product_code", "CYCLE_PAY_AUTH_P");
        // bizContent.put("sign_scene", "INDUSTRY|SOCIALIZATION");
        bizContent.put("sign_scene", "INDUSTRY|MOFAAI");
        // 商家签约号
        bizContent.put("external_agreement_no", reqDTO.getMerchantSignId());
        // 签约请求的协议有效周期
        bizContent.put("sign_validity_period", "");

        bizContent.put("access_params", access_params);

        // 周期规则参数
        JSONObject period_rule_params = new JSONObject();

        // period_rule_params.put("period_type", "DAY");
        // period_rule_params.put("period", "7");
        // period_rule_params.put("execute_time", DateUtil.today());
        // period_rule_params.put("single_amount", "0.01");
        // period_rule_params.put("total_amount", "");
        // period_rule_params.put("total_payments", "");
        // bizContent.put("period_rule_params", period_rule_params);

        period_rule_params.put("period_type", reqDTO.getPeriodType());
        period_rule_params.put("period", reqDTO.getPeriod());
        period_rule_params.put("execute_time", reqDTO.getExecuteTime());
        period_rule_params.put("single_amount", "0.01");
        period_rule_params.put("total_amount", reqDTO.getTotalAmount());
        period_rule_params.put("total_payments", reqDTO.getTotalPayments());
        bizContent.put("period_rule_params", period_rule_params);

        // 2 校验支付渠道是否有效
        PayChannelDO channel = validatePayChannelCanSubmit(reqDTO.getAppId(), reqDTO.getChannelCode());
        PayClient client = payClientFactory.getPayClient(channel.getId());
        log.info("[创建订阅][支付渠道有效，准备生成【支付宝】签约地址：用户ID({})|渠道 ID({})｜用户 IP({})]", getLoginUser(), channel.getId(), null);

        // 3. 调用三方接口
        PayOrderUnifiedReqDTO unifiedOrderReqDTO = new PayOrderUnifiedReqDTO();
        unifiedOrderReqDTO.setNotifyUrl(getChannelPayNotifyUrl(channel));
        unifiedOrderReqDTO.setBizContent(bizContent.toString());

        return client.unifiedOrder(unifiedOrderReqDTO).getDisplayContent();
    }

    /**
     * 验证签约状态是否成功
     *
     * @param reqDTO 创建请求
     * @return Boolean
     */
    @Override
    public Boolean validateSignStatusIsSuccess(PaySignCreateReqDTO reqDTO) {
        try {
            validatePaySignCanPay(reqDTO.getMerchantSignId());
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 根据订阅记录创建支付订单
     *
     * @param merchantSignId 创建请求
     * @return 支付单编号
     */
    @Override
    public String createSignPay(String merchantSignId) {

        // 获取签约记录
        PaySignDO paySign = validatePaySignCanPay(merchantSignId);

        return orderService.createPayOrder(new PayOrderCreateReqDTO()
                .setMerchantOrderId(PaySeqUtils.genMerchantSignPayOrderNo())
                .setSubject(paySign.getProductName())
                .setProductCode(paySign.getProductCode())
                .setBody(paySign.getProductName())
                .setAmount(paySign.getAmount())
                .setExpireTime(LocalDateTimeUtil.now().plusMinutes(5))
                .setUserIp(getClientIP())
                .setAppId(paySign.getAppId())
                .setSignId(paySign.getId()));
    }

    /**
     * 提交签约
     *
     * @param reqDTO 创建请求
     * @return 签约地址
     */
    @Override
    public SignPayResultReqVO submitSignPay(String merchantOrderId) {
        log.info("【签约订单创建支付请求】[当前订单编号为：({})]", merchantOrderId);
        // 1. 获得 PayOrderDO ，并校验其是否存在
        PayOrderDO order = orderService.getOrder(merchantOrderId);

        log.info("【签约订单创建支付请求】：用户ID({})|订单 ID({})]", order.getCreator(), merchantOrderId);

        // 1.2 校验支付渠道是否有效
        PayChannelDO channel = validatePayChannelCanSubmit(order.getAppId(), PayChannelEnum.ALIPAY_SIGN_PAY.getCode());
        PayClient client = payClientFactory.getPayClient(channel.getId());
        log.info("[签约订单创建支付请求][支付渠道验证通过：用户ID({})|渠道 ID({})｜订单编号({})]", order.getCreator(), channel.getId(), merchantOrderId);
        // 2. 插入 PayOrderExtensionDO
        PayOrderExtensionDO orderExtension = PayOrderExtensionDO.builder()
                .orderId(order.getId())
                .no(generateOrderExtensionNo())
                .channelId(channel.getId())
                .channelCode(channel.getCode())
                .userIp(order.getUserIp())
                .status(PayOrderStatusEnum.WAITING.getStatus())
                .build();
        orderExtensionMapper.insert(orderExtension);

        log.info("[签约订单创建支付请求][创建扩展订单数据成功：用户ID({})|订单 ID({})｜用户 IP({})]", order.getCreator(), JSONObject.toJSONString(orderExtension), null);

        PaySignDO paySign = getPaySign(order.getSignId());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderExtension.getNo());
        bizContent.put("product_code", "GENERAL_WITHHOLDING");// 商家扣款产品码固定为GENERAL_WITHHOLDING
        bizContent.put("total_amount", String.valueOf(order.getAmount() / 100.0));
        bizContent.put("subject", order.getSubject());
        JSONObject agreement_params = new JSONObject();
        agreement_params.put("agreement_no", paySign.getAgreementNo());
        bizContent.put("agreement_params", agreement_params);
        // 3. 调用三方接口
        PayOrderUnifiedReqDTO unifiedOrderReqDTO = new PayOrderUnifiedReqDTO();
        unifiedOrderReqDTO.setNotifyUrl(getChannelPayNotifyUrl(channel));
        unifiedOrderReqDTO.setBizContent(bizContent.toString());
        PayOrderUnifiedRespDTO payOrderUnifiedRespDTO = client.unifiedOrder(unifiedOrderReqDTO);

        SignPayResultReqVO signPayResultReqVO = new SignPayResultReqVO()
                .setChannelId(channel.getId())
                .setChannelCode(channel.getCode())
                .setOrderId(order.getId())
                .setOrderExtensionId(orderExtension.getId())
                .setOrderExtensionNo(orderExtension.getNo())
                .setResultCode(payOrderUnifiedRespDTO.getDisplayMode())
                .setResultMsg(payOrderUnifiedRespDTO.getDisplayContent());

        return signPayResultReqVO;

    }

    /**
     * 通知签约成功
     *
     * @param channelId 渠道编号
     * @param notify    通知
     * @param rawNotify 通知数据
     */
    @Override
    public void notifySign(Long channelId, PaySignNotifyRespDTO notify, PayNotifyReqDTO rawNotify) {
        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(channelId);
        TenantUtils.execute(channel.getTenantId(), () -> {
            // 签约验证
            PaySignDO signDO = signMapper.selectByAppIdAndMerchantSignId(channel.getAppId(), notify.getExternalAgreementNo());

            signDO.setChannelId(channel.getId());
            signDO.setChannelCode(channel.getCode());
            Boolean tryPay = false;
            // 更新签约状态
            if (!"NORMAL".equals(notify.getStatus())) {
                log.info("【签约取消】[用户:({})取消签约，取消的产品为:({})],取消时间({})", signDO.getCreator(), signDO.getProductName(), DateUtil.now());
                signDO.setStatus(PaySignStatusEnum.CLOSED.getStatus());
                signDO.setExpireTime(notify.getSignTime());
            } else {
                log.info("【签约成功】[用户:({})成功签约，签约的产品为:({})],签约成功时间({})", signDO.getCreator(), signDO.getProductName(), DateUtil.now());
                signDO.setStatus(PaySignStatusEnum.SUCCESS.getStatus());
                signDO.setAgreementNo(notify.getAgreementNo());
                signDO.setContractTime(notify.getSignTime());
                signDO.setExtensionData(notify.toString());

                tryPay = true;
            }
            signMapper.updateById(signDO);

            if (tryPay) {
                // 签约成功 开启第一次支付
                processSigningPayment(signDO);
            }
        });
    }

    /**
     * 更新示例订单为已支付
     *
     * @param id         编号
     * @param payOrderId 支付订单号
     * @param signId
     */
    @Override
    public void updatePaySign(PaySignDO paySignDO) {
        signMapper.updateById(paySignDO);
    }

    /**
     * 获取可以支付的签约记录
     */
    @Override
    public List<PaySignDO> getAbleToPayRecords() {
        DateTime now = DateUtil.date();
        return signMapper.selectList(Wrappers.lambdaQuery(PaySignDO.class)
                .eq(PaySignDO::getStatus, PaySignStatusEnum.SUCCESS.getStatus())
                .between(PaySignDO::getNextPay, DateUtil.beginOfDay(now), DateUtil.endOfDay(now)));

    }

    /**
     * @param code
     * @return
     */
    @Override
    public Boolean validatePaySignResult(String code) {
        if ("10000".equals(code)) {
            return true;
        }
        return false;
    }

    /**
     * 处理 签约支付
     *
     * @param paySignDO 创建请求
     * @return 支付单编号
     */
    @Override
    public void processSigningPayment(PaySignDO paySignDO) {
        // TODO 查账
        String signPayOrderId = createSignPay(paySignDO.getMerchantSignId());
        SignPayResultReqVO resultReqVO = submitSignPay(signPayOrderId);
        Boolean paySignResult = validatePaySignResult(resultReqVO.getResultCode());
        if (paySignResult) {
            // 更新表中的下次支付时间
            paySignDO.setNextPay(DateUtil.date().toLocalDateTime());
            updatePaySign(paySignDO);
            // 更新订单数据
            orderService.updatePayOrderExtensionSuccess(resultReqVO.getOrderExtensionNo(), JSONUtil.toJsonStr(resultReqVO));
            // 2. 更新 PayOrderDO 支付成功
            PayOrderDO order = orderService.updatePayOrderSuccess(resultReqVO.getChannelId(), resultReqVO.getChannelCode(), resultReqVO.getOrderId(), resultReqVO.getOrderExtensionId());
            // 根据商品 code 获取商品预设用户等级
            String roleCode = ProductEnum.getRoleCodeByCode(order.getProductCode());
            // 根据商品 code 获取权益类型
            String benefitsType = ProductEnum.getBenefitsTypeByCode(order.getProductCode());
            // 设置上下文租户
            TenantContextHolder.setTenantId(order.getTenantId());
            // TODO 设置用户角色 异常处理 日志
            userBenefitsService.addBenefitsAndRole(benefitsType, Long.valueOf(order.getCreator()), roleCode);
        }else {
            PayOrderDO order = orderService.getOrder(signPayOrderId);
            order.setStatus(PayOrderStatusEnum.CLOSED.getStatus());
            order.setErrorMsg(resultReqVO.getResultMsg());
            orderService.updatePayOrder(order);
        }
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
     * 根据支付渠道的编码，生成支付渠道的回调地址
     *
     * @param channel 支付渠道
     * @return 支付渠道的回调地址  配置地址 + "/" + channel id
     */
    private String getChannelPayNotifyUrl(PayChannelDO channel) {
        return payProperties.getCallbackUrl() + "/" + channel.getId();
    }

    private String generateOrderExtensionNo() {
        // 时间序列，年月日时分秒 14 位
        // 纯随机，6 位 TODO 芋艿：此处估计是会有问题的，后续在调整
        return DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + // 时间序列
                RandomUtil.randomInt(100000, 199999) // 随机。为什么是这个范围，因为偷懒
                ;
    }

    private PaySignDO validatePaySignCanSubmit(String merchantSignId) {
        PaySignDO order = signMapper.selectByMerchantSignId(merchantSignId);
        if (order == null) { // 是否存在
            throw exception(PAY_ORDER_NOT_FOUND);
        }
        if (!PaySignStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待签约
            throw exception(ErrorCodeConstants.PAY_SIGN_STATUS_IS_NOT_SUCCESS);
        }
        return order;
    }

    private PaySignDO validatePaySignCanPay(String merchantSignId) {
        PaySignDO order = signMapper.selectByMerchantSignId(merchantSignId);
        if (order == null) { // 是否存在
            throw exception(PAY_ORDER_NOT_FOUND);
        }
        if (!PaySignStatusEnum.SUCCESS.getStatus().equals(order.getStatus())) { // 校验状态，必须是签约成功
            throw exception(ErrorCodeConstants.PAY_SIGN_STATUS_IS_NOT_SUCCESS);
        }
        return order;
    }

}
