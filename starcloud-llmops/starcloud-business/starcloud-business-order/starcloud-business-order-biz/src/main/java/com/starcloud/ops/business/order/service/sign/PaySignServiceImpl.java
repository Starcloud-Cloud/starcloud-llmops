package com.starcloud.ops.business.order.service.sign;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.pay.config.PayProperties;
import cn.iocoder.yudao.framework.pay.core.client.PayClient;
import cn.iocoder.yudao.framework.pay.core.client.PayClientFactory;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.enums.ProductEnum;
import com.starcloud.ops.business.limits.enums.ProductSignEnum;
import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import com.starcloud.ops.business.order.convert.order.PayOrderConvert;
import com.starcloud.ops.business.order.convert.sign.PaySignConvert;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayChannelDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderExtensionDO;
import com.starcloud.ops.business.order.dal.dataobject.sign.PaySignDO;
import com.starcloud.ops.business.order.dal.mysql.order.PayOrderMapper;
import com.starcloud.ops.business.order.dal.mysql.sign.PaySignMapper;
import com.starcloud.ops.business.order.enums.ErrorCodeConstants;
import com.starcloud.ops.business.order.enums.order.PayOrderNotifyStatusEnum;
import com.starcloud.ops.business.order.enums.order.PayOrderStatusEnum;
import com.starcloud.ops.business.order.enums.refund.PayRefundTypeEnum;
import com.starcloud.ops.business.order.service.merchant.PayAppService;
import com.starcloud.ops.business.order.service.merchant.PayChannelService;
import com.starcloud.ops.business.order.service.order.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;

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

    /**
     * 创建支付单
     *
     * @param reqDTO 创建请求
     * @return 支付单编号
     */
    @Override
    public String createPaySign(PayOrderCreateReqDTO reqDTO) {
        log.info("[创建订阅],用户[userId({})｜租户[({})｜开始创建订阅记录({})]", getLoginUserId(), getTenantId(), reqDTO.getMerchantOrderId());

        // 判断产品是否存在
        ProductEnum productEnum;
        try {
            productEnum = ProductEnum.valueOf(reqDTO.getProductCode());
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
        PaySignDO signDO = signMapper.selectByAppIdAndMerchantOrderId(
                reqDTO.getAppId(), reqDTO.getMerchantOrderId());

        if (signDO != null) {
            log.warn("[创建订阅][appId({}) merchantOrderId({}) 已经存在对应的支付单({})]", signDO.getAppId(),
                    signDO.getMerchantOrderId(), toJsonString(signDO)); // 理论来说，不会出现这个情况
            return signDO.getMerchantOrderId();
        }
        // 创建支付交易单
        signDO = PaySignConvert.INSTANCE.convert(reqDTO)
                .setMerchantId(app.getMerchantId())
                .setAppId(app.getId())
                .setProductCode(reqDTO.getProductCode());
        // 订单相关字段
        signDO.setStatus(PayOrderStatusEnum.WAITING.getStatus());

        // 创建订单
        String merchantOrderId = orderService.createPayOrder(reqDTO);


        signDO.setMerchantOrderId(merchantOrderId);
        // 创建订阅记录
        signMapper.insert(signDO);


        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "20210817010101003");
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", "测试商品");
        bizContent.put("buyer_id", "2088102146225135");
        bizContent.put("timeout_express", "10m");
        bizContent.put("product_code", "JSAPI_PAY");

        // 签约参数
        JSONObject agreement_sign_params = new JSONObject();
        agreement_sign_params.put("product_code", "GENERAL_WITHHOLDING");
        agreement_sign_params.put("personal_product_code", "CYCLE_PAY_AUTH_P");
        agreement_sign_params.put("sign_scene", "场景码xxxx");
        agreement_sign_params.put("external_agreement_no", "外部协议号xxx");
        // 周期规则参数，必填
        JSONObject period_rule_params = new JSONObject();
        period_rule_params.put("period_type", "DAY");
        period_rule_params.put("period", "7");
        period_rule_params.put("execute_time", "2024-12-01");
        period_rule_params.put("single_amount", "0.01");
        period_rule_params.put("total_amount", "0.05");
        period_rule_params.put("total_payments", "5");
        agreement_sign_params.put("period_rule_params", period_rule_params);
        JSONObject access_params = new JSONObject();
        access_params.put("channel", "ALIPAYAPP");
        agreement_sign_params.put("access_params", access_params);
        bizContent.put("agreement_sign_params", agreement_sign_params);


        // 1.2 校验支付渠道是否有效
        PayChannelDO channel = validatePayChannelCanSubmit(signDO.getAppId(), signDO.getChannelCode());
        PayClient client = payClientFactory.getPayClient(channel.getId());
        log.info("[创建订阅][支付渠道有效：用户ID({})|渠道 ID({})｜用户 IP({})]", getLoginUser(), channel.getId(), null);


        log.info("[submitPayOrder][2.创建扩展订单数据成功：用户ID({})|订单 ID({})｜用户 IP({})]", getLoginUser(), null, null);
        // 3. 调用三方接口
        PayOrderUnifiedReqDTO unifiedOrderReqDTO =new PayOrderUnifiedReqDTO();
        unifiedOrderReqDTO.setBizContent(bizContent.toString());

        return client.unifiedOrder(unifiedOrderReqDTO).getDisplayContent();
    }

    /**
     * 更新示例订单为已支付
     *
     * @param id         编号
     * @param payOrderId 支付订单号
     * @param signId
     */
    @Override
    public void updatePaySign(Long id, Long payOrderId, String signId) {

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
    private String genChannelPayNotifyUrl(PayChannelDO channel) {
        return payProperties.getCallbackUrl() + "/" + channel.getId();
    }

    private String generateOrderExtensionNo() {
        // 时间序列，年月日时分秒 14 位
        // 纯随机，6 位 TODO 芋艿：此处估计是会有问题的，后续在调整
        return DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + // 时间序列
                RandomUtil.randomInt(100000, 199999) // 随机。为什么是这个范围，因为偷懒
                ;
    }

}
