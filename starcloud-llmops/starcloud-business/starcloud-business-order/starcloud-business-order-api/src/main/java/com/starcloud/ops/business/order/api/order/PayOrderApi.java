package com.starcloud.ops.business.order.api.order;


import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import com.starcloud.ops.business.order.api.order.dto.PayOrderRespOldDTO;

import javax.validation.Valid;

/**
 * 支付单 API 接口
 *
 * @author LeeYan9
 * @since 2022-08-26
 */
public interface PayOrderApi {

    /**
     * 创建支付单
     *
     * @param reqDTO 创建请求
     * @return 支付单编号
     */
    String createOrder(@Valid PayOrderCreateReqDTO reqDTO);

    /**
     * 获得支付单
     *
     * @param id 支付单编号
     * @return 支付单
     */
    PayOrderRespOldDTO getOrder(Long id);

    /**
     * 是否存在支付成功订单
     *
     * @param userId 支付单编号
     * @return 支付单列表
     */
    Boolean exitSuccessPayOrder(Long userId);

}
