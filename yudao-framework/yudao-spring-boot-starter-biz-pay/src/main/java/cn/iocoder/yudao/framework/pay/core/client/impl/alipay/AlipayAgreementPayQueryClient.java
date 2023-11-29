package cn.iocoder.yudao.framework.pay.core.client.impl.alipay;

import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付宝【Wap 网站】的 PayClient 实现类
 * <p>
 * 文档：<a href="https://opendocs.alipay.com/apis/api_1/alipay.trade.wap.pay">手机网站支付接口</a>
 *
 * @author 芋道源码
 */
@Slf4j
public class AlipayAgreementPayQueryClient extends AbstractAlipayClient {

    public AlipayAgreementPayQueryClient(Long channelId, AlipayPayClientConfig config) {
        super(channelId, PayChannelEnum.ALIPAY_AGREEMENT_PAY_QUERY.getCode(), config);
    }

    @Override
    public PayOrderUnifiedRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) throws AlipayApiException {
        // 1.1 构建 AlipayTradeCreateRequest 请求
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        // 1.2 签约参数
        request.setBizContent(reqDTO.getBizContent());
        // 2.1 执行请求
        AlipayTradeQueryResponse response = client.execute(request);
        // 2.2 处理结果
        // validateSuccess(response);
        return new PayOrderUnifiedRespDTO()
                .setDisplayMode(response.getCode()).setDisplayContent(response.getSubCode());
    }

}
