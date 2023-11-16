package com.starcloud.ops.business.order.api.order;


import com.starcloud.ops.business.order.api.order.dto.PayOrderBaseDTO;
import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import com.starcloud.ops.business.order.api.order.dto.PayOrderRespDTO;
import com.starcloud.ops.business.order.controller.admin.order.vo.PayOrderExportReqVO;
import com.starcloud.ops.business.order.controller.admin.order.vo.PayOrderPageReqVO;
import com.starcloud.ops.business.order.convert.order.PayOrderConvert;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.enums.order.PayOrderNotifyStatusEnum;
import com.starcloud.ops.business.order.enums.order.PayOrderStatusEnum;
import com.starcloud.ops.business.order.service.order.PayOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 支付单 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class PayOrderApiImpl implements PayOrderApi {

    @Resource
    private PayOrderService payOrderService;

    @Override
    public String createOrder(PayOrderCreateReqDTO reqDTO) {
        return payOrderService.createPayOrder(reqDTO);
    }

    @Override
    public PayOrderRespDTO getOrder(Long id) {
        PayOrderDO order = payOrderService.getOrder(id);
        return PayOrderConvert.INSTANCE.convert2(order);
    }

    /**
     * 是否存在支付成功订单
     *
     * @param userId 支付单编号
     * @return 支付单列表
     */
    @Override
    public Boolean exitSuccessPayOrder(Long userId) {
        PayOrderExportReqVO exportReqVO = new PayOrderExportReqVO();
        exportReqVO.setStatus(PayOrderStatusEnum.SUCCESS.getStatus());
        return payOrderService.getOrderList(exportReqVO).size() == 0;
    }

}
