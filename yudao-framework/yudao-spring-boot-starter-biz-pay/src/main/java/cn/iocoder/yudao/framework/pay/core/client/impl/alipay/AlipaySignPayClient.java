package cn.iocoder.yudao.framework.pay.core.client.impl.alipay;

import cn.hutool.http.Method;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayUserAgreementPageSignRequest;
import com.alipay.api.response.AlipayTradePayResponse;
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
public class AlipaySignPayClient extends AbstractAlipayClient {

    public AlipaySignPayClient(Long channelId, AlipayPayClientConfig config) {
        super(channelId, PayChannelEnum.ALIPAY_SIGN_PAY.getCode(), config);
    }

    @Override
    public PayOrderUnifiedRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) throws AlipayApiException {
        // 1.1 构建 AlipayTradeCreateRequest 请求
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        // 1.2 签约参数
        request.setBizContent(reqDTO.getBizContent());
        // 2.1 执行请求
        request.setBizContent(reqDTO.getBizContent());
        AlipayTradePayResponse response = client.execute(request);;
        // 2.2 处理结果
        // validateSuccess(response);
        return new PayOrderUnifiedRespDTO()
                .setDisplayMode(response.getCode()).setDisplayContent(response.getSubCode());
    }

}
