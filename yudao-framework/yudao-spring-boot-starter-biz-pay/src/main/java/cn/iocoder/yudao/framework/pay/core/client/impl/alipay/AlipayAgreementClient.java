package cn.iocoder.yudao.framework.pay.core.client.impl.alipay;

import cn.hutool.http.Method;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayUserAgreementPageSignRequest;
import com.alipay.api.response.AlipayUserAgreementPageSignResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付宝【Wap 网站】的 PayClient 实现类
 * <p>
 * 文档：<a href="https://opendocs.alipay.com/apis/api_1/alipay.trade.wap.pay">手机网站支付接口</a>
 *
 * @author 芋道源码
 */
@Slf4j
public class AlipayAgreementClient extends AbstractAlipayClient {

    public AlipayAgreementClient(Long channelId, AlipayPayClientConfig config) {
        super(channelId, PayChannelEnum.ALIPAY_AGREEMENT.getCode(), config);
    }

    @Override
    public PayOrderUnifiedRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) throws AlipayApiException {

        // 1.1 构建 AlipayTradeCreateRequest 请求
        AlipayUserAgreementPageSignRequest request = new AlipayUserAgreementPageSignRequest();
        request.setNotifyUrl(reqDTO.getNotifyUrl());
        // 1.2 签约参数
        request.setBizContent(reqDTO.getBizContent());
        // 2.1 执行请求
        AlipayUserAgreementPageSignResponse response = client.pageExecute(request, Method.GET.name());
        // 2.2 处理结果
        validateSuccess(response);
        return new PayOrderUnifiedRespDTO()
                .setDisplayMode(null).setDisplayContent(response.getBody());
    }

}
