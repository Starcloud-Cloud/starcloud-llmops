package cn.iocoder.yudao.framework.pay.core.client.impl.alipay;

import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import cn.iocoder.yudao.framework.pay.core.enums.PayDisplayModeEnum;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付宝【Wap 网站】的 PayClient 实现类
 * <p>
 * 文档：<a href="https://opendocs.alipay.com/apis/api_1/alipay.trade.wap.pay">手机网站支付接口</a>
 *
 * @author 芋道源码
 */
@Slf4j
public class AlipaySignClient extends AbstractAlipayClient {

    public AlipaySignClient(Long channelId, AlipayPayClientConfig config) {
        super(channelId, PayChannelEnum.ALIPAY_SIGN.getCode(), config);
    }

    @Override
    public PayOrderUnifiedRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) throws AlipayApiException {

        // 1.1 构建 AlipayTradePrecreateModel 请求
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        // ① 通用的参数
        model.setOutTradeNo(reqDTO.getMerchantOrderId());
        model.setSubject(reqDTO.getSubject());
        model.setBody(reqDTO.getBody());
        model.setTotalAmount(formatAmount(reqDTO.getAmount()));
        model.setProductCode("FACE_TO_FACE_PAYMENT"); // 销售产品码. 目前扫码支付场景下仅支持 FACE_TO_FACE_PAYMENT
        // ② 签约参数
        JSONObject agreement_sign_params = new JSONObject();
        agreement_sign_params.put("product_code", "GENERAL_WITHHOLDING");
        agreement_sign_params.put("personal_product_code", "CYCLE_PAY_AUTH_P");
        agreement_sign_params.put("sign_scene", "场景码xxxx");
        agreement_sign_params.put("external_agreement_no", "外部协议号xxx");
        agreement_sign_params.put("sign_notify_url", "异步通知地址xxx");
        //周期规则参数，必填
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
        // bizContent.put("agreement_sign_params", agreement_sign_params);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "20210817010101003");
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", "测试商品");
        bizContent.put("buyer_id", "2088102146225135");
        bizContent.put("timeout_express", "10m");
        bizContent.put("product_code", "JSAPI_PAY");

        // ③ 支付宝扫码支付只有一种展示，考虑到前端可能希望二维码扫描后，手机打开
        String displayMode = PayDisplayModeEnum.QR_CODE.getMode();

        // 1.2 构建 AlipayTradePrecreateRequest 请求
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setBizModel(model);
        request.setNotifyUrl(reqDTO.getNotifyUrl());
        request.setReturnUrl(reqDTO.getReturnUrl());

        // 2.1 执行请求
        AlipayTradePrecreateResponse response = client.execute(request);
        // 2.2 处理结果
        validateSuccess(response);
        return new PayOrderUnifiedRespDTO()
                .setDisplayMode(displayMode).setDisplayContent(response.getQrCode());


        // // 1.1 构建 AlipayTradeWapPayModel 请求
        // AlipayUserAgreementPageSignRequest request = new AlipayUserAgreementPageSignRequest();
        // AlipayUserAgreementPageSignModel model = new AlipayUserAgreementPageSignModel();
        //
        // // 签约参数
        // AccessParams accessParams = new AccessParams();
        // // 签约渠道
        // accessParams.setChannel("ALIPAYAPP");
        //
        // model.setAccessParams(accessParams);
        // // 当前用户签约请求的协议有效周期。 当前用户签约请求的协议有效周期。整形数字加上时间单位的协议有效期，从发起签约请求的时间开始算起。
        // // 目前支持的时间单位：1. d：天 2. m：月
        // model.setSignValidityPeriod("2m");
        //
        // // 此参数用于传递子商户信息，无特殊需求时不用关注。目前商户代扣、海外代扣、淘旅行信用住产品支持传入该参数（在销售方案中“是否允许自定义子商户信息”需要选是）。
        // SubMerchantParams subMerchant = new SubMerchantParams();
        //
        // subMerchant.setSubMerchantServiceName("滴滴出行免密支付");
        // subMerchant.setSubMerchantServiceDescription("免密付车费，单次最高500");
        // subMerchant.setSubMerchantName("滴滴出行");
        // // 子商户的商户id
        // subMerchant.setSubMerchantId("2088123412341234");
        // model.setSubMerchant(subMerchant);
        //
        // // 销售产品码，商户签约的支付宝合同所对应的产品码。
        // model.setProductCode("GENERAL_WITHHOLDING");
        //
        // // 协议生效类型, 用于指定协议是立即生效还是等待商户通知再生效. 可空, 不填默认为立即生效.
        // model.setAgreementEffectType("DIRECT");
        //
        // // 签约产品属性，json格式
        // ProdParams prodParams = new ProdParams();
        // prodParams.setAuthBizParams("{\"platform\":\"taobao\"}");
        // model.setProdParams(prodParams);
        //
        // // 签约营销参数
        // model.setPromoParams("{\"key\":\"value\"}");
        // // 签约有效时间限制，单位是秒，有效范围是0-86400，商户传入此字段会用商户传入的值否则使用支付宝侧默认值，在有效时间外进行签约，会进行安全拦截；（备注：此字段适用于需要开通安全防控的商户，且依赖商户传入生成签约时的时间戳字段timestamp）
        // model.setEffectTime(300L);
        //
        // // 用户在商户网站的登录账号，用于在签约页面展示，如果为空，则不展示
        // model.setExternalLogonId("13852852877");
        // // 协议签约场景，商户可根据 代扣产品常见场景值 选择符合自身的行业场景。
        // // 说明：当传入商户签约号 external_agreement_no 时，本参数必填，不能为默认值 DEFAULT|DEFAULT
        // model.setSignScene("INDUSTRY|CARRENTAL");
        //
        // // 个人签约产品码，商户和支付宝签约时确定，商户可咨询技术支持。
        // model.setPersonalProductCode("GENERAL_WITHHOLDING_P");
        //
        // // 商户希望限制的签约用户的年龄范围，min表示可签该协议的用户年龄下限，max表示年龄上限。如{"min": "18","max": "30"}表示18=<年龄<=30的用户可以签约该协议
        // model.setUserAgeRange("{\"min\":\"18\",\"max\":\"30\"}");
        //
        // // 用户实名信息参数，包含：姓名、身份证号、签约指定uid。商户传入用户实名信息参数，支付宝会对比用户在支付宝端的实名信息。
        // IdentityParams identityParams = new IdentityParams();
        // identityParams.setCertNo("61102619921108888");
        // identityParams.setIdentityHash("8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92");
        // identityParams.setSignUserId("2088202888530893");
        // identityParams.setUserName("张三");
        // model.setIdentityParams(identityParams);
        //
        // // 商户签约号
        // model.setExternalAgreementNo("test");
        //
        // //周期管控规则参数 周期管控规则参数period_rule_params，在签约周期扣款产品（如CYCLE_PAY_AUTH_P）时必传，在签约其他产品时无需传入。 周期扣款产品，会按照这里传入的参数提示用户，并对发起扣款的时间、金额、次数等做相应限制。
        // PeriodRuleParams periodRuleParams = new PeriodRuleParams();
        //
        // // 周期类型period_type是周期扣款产品必填，枚举值为DAY和MONTH。 DAY即扣款周期按天计，MONTH代表扣款周期按自然月
        // periodRuleParams.setPeriodType("DAY");
        // // 周期数period是周期扣款产品必填。与另一参数period_type组合使用确定扣款周期，例如period_type为DAY，period=90，则扣款周期为90天。
        // periodRuleParams.setPeriod(3L);
        // // 总扣款次数。如果传入此参数，则商户成功扣款的次数不能超过此次数限制（扣款失败不计入）
        // periodRuleParams.setTotalPayments(12L);
        // // 首次执行时间execute_time是周期扣款产品必填，即商户发起首次扣款的时间。精确到日，格式为yyyy-MM-dd
        // // 结合其他必填的扣款周期参数，会确定商户以后的扣款计划。发起扣款的时间需符合这里的扣款计划。
        // periodRuleParams.setExecuteTime("2019-01-23");
        //
        // // 单次扣款最大金额single_amount是周期扣款产品必填，即每次发起扣款时限制的最大金额，单位为元。商户每次发起扣款都不允许大于此金额。
        // periodRuleParams.setSingleAmount("10.99");
        // // 总金额限制，单位为元。如果传入此参数，商户多次扣款的累计金额不允许超过此金额。
        // periodRuleParams.setTotalAmount("600");
        //
        // model.setPeriodRuleParams(periodRuleParams);
        // //签约第三方主体类型。对于三方协议，表示当前用户和哪一类的第三方主体进行签约。
        // // 默认为PARTNER。
        // // 枚举值
        // // 平台商户: PARTNER
        // model.setThirdPartyType("PARTNER");
        //
        // request.setBizModel(model);
        //
        //
        // // 2.1 执行请求
        // AlipayUserAgreementPageSignResponse response = client.pageExecute(request);
        //
        // // 2.2 处理结果
        // validateSuccess(response);
        //
        // return new PayOrderUnifiedRespDTO()
        //         .setDisplayMode(displayMode).setDisplayContent(response.getQrCode());
    }

}
