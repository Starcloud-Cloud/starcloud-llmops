package com.starcloud.ops.business.order.service.sign;

import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Slf4j
public class PaySignServiceImpl implements PaySignService {

    /**
     * 创建支付单
     *
     * @param reqDTO 创建请求
     * @return 支付单编号
     */
    @Override
    public String createPaySign(PayOrderCreateReqDTO reqDTO) {

        return null;
    }

    /**
     * 更新示例订单为已支付
     *
     * @param id         编号
     * @param payOrderId 支付订单号
     * @param signId
     */
    @Override
    public void updatePaySign(Long id, Long payOrderId, String signId) {

    }
}
