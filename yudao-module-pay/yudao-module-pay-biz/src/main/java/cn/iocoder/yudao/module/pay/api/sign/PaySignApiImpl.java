package cn.iocoder.yudao.module.pay.api.sign;

import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignRespDTO;
import cn.iocoder.yudao.module.pay.service.sign.PaySignService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 支付单 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class PaySignApiImpl implements PaySignApi {

    @Resource
    private PaySignService paySignService;

    @Override
    public Long createSign(PaySignCreateReqDTO reqDTO) {
        return paySignService.createSign(reqDTO);
    }

    @Override
    public PaySignRespDTO getSign(Long id) {
        return null;
    }

    @Override
    public void updatePaySignPrice(Long id, Integer payPrice) {

    }

    //@Override
    //public Long createOrder(PayOrderCreateReqDTO reqDTO) {
    //    return payOrderService.createOrder(reqDTO);
    //}
    //
    //@Override
    //public PayOrderRespDTO getOrder(Long id) {
    //    PayOrderDO order = payOrderService.getOrder(id);
    //    return PayOrderConvert.INSTANCE.convert2(order);
    //}
    //
    //@Override
    //public void updatePayOrderPrice(Long id, Integer payPrice) {
    //    payOrderService.updatePayOrderPrice(id, payPrice);
    //}

}
