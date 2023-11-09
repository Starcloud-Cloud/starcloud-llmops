package com.starcloud.ops.business.order.service.sign;

import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import org.springframework.validation.annotation.Validated;

/**
 * 支付订单 Service 接口
 *
 * @author aquan
 */
public interface PaySignService {


    /**
     * 创建支付单
     *
     * @param reqDTO 创建请求
     * @return 支付单编号
     */
    String createPaySign(@Validated PayOrderCreateReqDTO reqDTO);

    /**
     * 更新示例订单为已支付
     *
     * @param id 编号
     * @param payOrderId 支付订单号
     */
    void updatePaySign(Long id, Long payOrderId,String signId);





}
