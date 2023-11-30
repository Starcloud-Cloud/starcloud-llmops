package cn.iocoder.yudao.framework.pay.core.client.impl.alipay;

import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.enums.channel.PayChannelEnum;
import cn.iocoder.yudao.framework.pay.core.enums.order.PayOrderDisplayModeEnum;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.*;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import lombok.extern.slf4j.Slf4j;

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
        // 1.1 构建 AlipayTradePrecreateModel 请求
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        // ① 通用的参数
//        model.setOutTradeNo(reqDTO.getOutTradeNo());
//        model.setSubject(reqDTO.getSubject());
//        model.setBody(reqDTO.getBody());
//        model.setTotalAmount(formatAmount(reqDTO.getPrice()));
        model.setOutTradeNo("20231213213123123123");
        model.setSubject("测试");
        model.setBody("数据测试");
        model.setTotalAmount("0.01");
        model.setProductCode("QUICK_MSECURITY_PAY");

        // ② 签约参数
        SignParams signParams = new SignParams();
        signParams.setSignScene("INDUSTRY|MOFAAI");
        signParams.setSignNotifyUrl("");
        signParams.setExternalAgreementNo("20232213213123123123");
        signParams.setPersonalProductCode("CYCLE_PAY_AUTH_P");
        // ③ 签约参数设置渠道参数
        AccessParams accessParams = new AccessParams();
        // ④ 固定为扫码签约
        accessParams.setChannel("QRCODE");
        signParams.setAccessParams(accessParams);

        // ⑤ 周期规则参数
        PeriodRuleParams periodRuleParams = new PeriodRuleParams();
        periodRuleParams.setPeriod(1L);
        periodRuleParams.setPeriodType("MONTH");
        periodRuleParams.setExecuteTime(DateUtil.today());
        periodRuleParams.setSingleAmount("29.99");
        periodRuleParams.setTotalAmount(null);
        periodRuleParams.setTotalPayments(null);

        signParams.setPeriodRuleParams(periodRuleParams);

        model.setAgreementSignParams(signParams);

        // ③ 支付宝扫码支付只有一种展示，考虑到前端可能希望二维码扫描后，手机打开
        String displayMode = PayOrderDisplayModeEnum.APP.getMode();

        // 1.2 构建 AlipayTradePrecreateRequest 请求
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        request.setBizModel(model);
        request.setNotifyUrl(reqDTO.getNotifyUrl());
        request.setReturnUrl(reqDTO.getReturnUrl());

        // 2.1 执行请求
        AlipayTradeCreateResponse response;
        if (Objects.equals(config.getMode(), MODE_CERTIFICATE)) {
            // 证书模式
            response = client.certificateExecute(request);
        } else {
            response = client.execute(request);
        }
        // 2.2 处理结果
        if (!response.isSuccess()) {
            return buildClosedPayOrderRespDTO(reqDTO, response);
        }
        return PayOrderRespDTO.successOf(displayMode, response.getTradeNo(),null,
                reqDTO.getOutTradeNo(), response);
    }
}
