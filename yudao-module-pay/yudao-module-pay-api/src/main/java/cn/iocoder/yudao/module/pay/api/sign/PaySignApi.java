package cn.iocoder.yudao.module.pay.api.sign;

import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignRespDTO;

import javax.validation.Valid;

/**
 * 支付单 API 接口
 *
 * @author LeeYan9
 * @since 2022-08-26
 */
public interface PaySignApi {

    /**
     * 创建支付单
     *
     * @param reqDTO 创建请求
     * @return 支付单编号
     */
    Long createSign(@Valid PaySignCreateReqDTO reqDTO);

    /**
     * 获得支付单
     *
     * @param id 支付单编号
     * @return 支付单
     */
    PaySignRespDTO getSign(Long id);

    /**
     * 更新支付订单价格
     *
     * @param id 支付单编号
     * @param payPrice   支付单价格
     */
    void updatePaySignPrice(Long id, Integer payPrice);

}
