package cn.iocoder.yudao.module.pay.api.sign;

import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;

public class PaySignApiImpl implements PaySignApi {


    /**
     * 创建签约
     *
     * @param reqDTO 创建请求
     * @return 签约编号
     */
    @Override
    public Long createSign(PayOrderCreateReqDTO reqDTO) {
        return null;
    }

    /**
     * 获得签约记录
     *
     * @param id 签约编号
     * @return 获得签约记录
     */
    @Override
    public PayOrderRespDTO getSignRecord(Long id) {
        return null;
    }
}