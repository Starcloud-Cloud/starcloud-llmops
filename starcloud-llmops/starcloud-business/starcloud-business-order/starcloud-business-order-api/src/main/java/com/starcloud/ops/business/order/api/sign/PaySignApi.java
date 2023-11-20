package com.starcloud.ops.business.order.api.sign;


import com.starcloud.ops.business.order.api.order.dto.PayOrderRespDTO;
import com.starcloud.ops.business.order.api.sign.dto.PaySignCreateReqDTO;

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
    Long createOrder(@Valid PaySignCreateReqDTO reqDTO);

    /**
     * 获得支付单
     *
     * @param id 支付单编号
     * @return 支付单
     */
    PayOrderRespDTO getOrder(Long id);

}
