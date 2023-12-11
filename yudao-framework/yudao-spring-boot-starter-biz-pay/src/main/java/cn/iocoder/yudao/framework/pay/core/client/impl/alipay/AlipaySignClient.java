package cn.iocoder.yudao.framework.pay.core.client.impl.alipay;

import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.enums.channel.PayChannelEnum;
import cn.iocoder.yudao.framework.pay.core.enums.order.PayOrderDisplayModeEnum;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.*;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;

import java.util.Objects;

import static cn.iocoder.yudao.framework.pay.core.client.impl.alipay.AlipayPayClientConfig.MODE_CERTIFICATE;

/**
 * 支付宝【扫码支付】的 PayClient 实现类
 *
 * 文档：<a href="https://opendocs.alipay.com/apis/02890k">扫码支付</a>
 *
 * @author 芋道源码
 */
@Slf4j
public class AlipaySignClient extends AbstractAlipayPayClient {

    public AlipaySignClient(Long channelId, AlipayPayClientConfig config) {
        super(channelId, PayChannelEnum.ALIPAY_AGREEMENT_APP.getCode(), config);
    }

    @Override
    public PayOrderRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) throws AlipayApiException {
//
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

        request.setNotifyUrl("");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "20210817010101004");
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", "测试商品");
        bizContent.put("product_code", "QUICK_MSECURITY_PAY");
//签约参数
        JSONObject agreement_sign_params = new JSONObject();
        agreement_sign_params.put("product_code", "GENERAL_WITHHOLDING");
        agreement_sign_params.put("personal_product_code", "CYCLE_PAY_AUTH_P");
        agreement_sign_params.put("sign_scene", "INDUSTRY|MOFAAI");
        agreement_sign_params.put("external_agreement_no", "20210817010101004");
        agreement_sign_params.put("sign_notify_url", "");
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
        bizContent.put("agreement_sign_params", agreement_sign_params);
        //        // ③ 支付宝扫码支付只有一种展示，考虑到前端可能希望二维码扫描后，手机打开
        String displayMode = PayOrderDisplayModeEnum.APP.getMode();
////bizContent.put("time_expire", "2022-08-01 22:00:00");
//
////// 商品明细信息，按需传入
////JSONArray goodsDetail = new JSONArray();
////JSONObject goods1 = new JSONObject();
////goods1.put("goods_id", "goodsNo1");
////goods1.put("goods_name", "子商品1");
////goods1.put("quantity", 1);
////goods1.put("price", 0.01);
////goodsDetail.add(goods1);
////bizContent.put("goods_detail", goodsDetail)
//        // 1.1 构建 AlipayTradePrecreateModel 请求
//        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
//        // ① 通用的参数
////        model.setOutTradeNo(reqDTO.getOutTradeNo());
////        model.setSubject(reqDTO.getSubject());
////        model.setBody(reqDTO.getBody());
////        model.setTotalAmount(formatAmount(reqDTO.getPrice()));
//        model.setOutTradeNo("20231213213123123123");
//        model.setSubject("测试");
//        model.setBody("数据测试");
//        model.setTotalAmount("29.99");
//        model.setProductCode("QUICK_MSECURITY_PAY");
//
//        // ② 签约参数
//        AgreementSignParams agreementSignParams = new AgreementSignParams();
//        SignParams signParams = new SignParams();
//        signParams.setPersonalProductCode("CYCLE_PAY_AUTH_P");
//        // 场景码
//        signParams.setSignScene("INDUSTRY|MOFAAI");
//        signParams.setSignNotifyUrl("");
//        signParams.setExternalAgreementNo("20232213213123123123");
//
//        // ③ 签约参数设置渠道参数
//        AccessParams accessParams = new AccessParams();
//        // ④ 固定为扫码签约
//        accessParams.setChannel("ALIPAYAPP");
//        signParams.setAccessParams(accessParams);
//
//        // ⑤ 周期规则参数
//        PeriodRuleParams periodRuleParams = new PeriodRuleParams();
//        periodRuleParams.setPeriod(1L);
//        periodRuleParams.setPeriodType("MONTH");
//        periodRuleParams.setExecuteTime(DateUtil.today());
//        periodRuleParams.setSingleAmount("29.99");
//        periodRuleParams.setTotalAmount(null);
//        periodRuleParams.setTotalPayments(null);
//
//        signParams.setPeriodRuleParams(periodRuleParams);
//
//        model.setAgreementSignParams(signParams);
//
//        // ③ 支付宝扫码支付只有一种展示，考虑到前端可能希望二维码扫描后，手机打开
//        String displayMode = PayOrderDisplayModeEnum.APP.getMode();
//
//        // 1.2 构建 AlipayTradePrecreateRequest 请求
//        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
//        request.setBizModel(model);
//        request.setNotifyUrl(reqDTO.getNotifyUrl());
//        request.setReturnUrl(reqDTO.getReturnUrl());

        request.setBizContent(bizContent.toString());
        // 2.1 执行请求
        AlipayTradeAppPayResponse response;
        if (Objects.equals(config.getMode(), MODE_CERTIFICATE)) {
            // 证书模式
            response = client.certificateExecute(request);
        } else {
            response = client.sdkExecute(request);
        }
        // 2.2 处理结果
        if (!response.isSuccess()) {
            return buildClosedPayOrderRespDTO(reqDTO, response);
        }
        return PayOrderRespDTO.successOf(displayMode, response.getTradeNo(),null,
                reqDTO.getOutTradeNo(), response);
    }
}
