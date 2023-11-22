package cn.iocoder.yudao.framework.pay.core.client.impl.alipay;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.enums.channel.PayChannelEnum;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 支付宝【Wap 网站】的 PayClient 实现类
 * <p>
 * 文档：<a href="https://opendocs.alipay.com/apis/api_1/alipay.trade.wap.pay">手机网站支付接口</a>
 *
 * @author 芋道源码
 */
@Slf4j
public class AlipayAgreementPayClient extends AbstractAlipayPayClient {

    public AlipayAgreementPayClient(Long channelId, AlipayPayClientConfig config) {
        super(channelId, PayChannelEnum.ALIPAY_AGREEMENT_PAY.getCode(), config);
    }

    @Override
    public PayOrderRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) throws AlipayApiException {
        // 1.1 构建 AlipayTradeCreateRequest 请求
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        // 1.2 签约参数
        request.setBizContent(reqDTO.getBizContent());

        AlipayTradePayResponse response = client.execute(request);
        // 2.2 处理结果
        // validateSuccess(response);

        if ("10000".equals(response.getCode())) { // 免密支付
            LocalDateTime successTime = LocalDateTimeUtil.of(response.getGmtPayment());
            return PayOrderRespDTO.successOf(response.getTradeNo(), response.getBuyerUserId(), successTime,
                            response.getOutTradeNo(), response)
                    .setDisplayMode(response.getCode()).setDisplayContent(response.getSubCode());
        }
        // 大额支付，需要用户输入密码，所以返回 waiting。此时，前端一般会进行轮询
        return PayOrderRespDTO.waitingOf(null, "",
                reqDTO.getOutTradeNo(), response);
    }

}
