package com.starcloud.ops.business.order.convert.sign;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import com.starcloud.ops.business.order.api.order.dto.PayOrderRespOldDTO;
import com.starcloud.ops.business.order.controller.admin.order.vo.*;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderExtensionDO;
import com.starcloud.ops.business.order.dal.dataobject.sign.PaySignDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付订单 Convert
 *
 * @author aquan
 */
@Mapper
public interface PaySignConvert {

    PaySignConvert INSTANCE = Mappers.getMapper(PaySignConvert.class);

    PayOrderRespVO convert(PaySignDO bean);

    PayOrderRespOldDTO convert2(PaySignDO order);

    PayOrderDetailsRespVO orderDetailConvert(PayOrderDO bean);

    PayOrderDetailsRespVO.PayOrderExtension orderDetailExtensionConvert(PayOrderExtensionDO bean);

    List<PayOrderRespVO> convertList(List<PayOrderDO> list);

    PageResult<PayOrderRespVO> convertPage(PageResult<PayOrderDO> page);


    /**
     * 订单 DO 转自定义分页对象
     *
     * @param bean 订单DO
     * @return 分页对象
     */
    PayOrderPageItemRespVO pageConvertItemPage(PayOrderDO bean);


//    PageResult<AppPayOrderDetailsRespVO> convertAppPage(PageResult<PayOrderDO> page);


    PaySignDO convert(PayOrderCreateReqDTO bean);

    @Mapping(target = "id", ignore = true)
    default PayOrderExtensionDO convert(PayOrderSubmitReqVO bean, String userIp,Long orderId) {
        if (bean == null && userIp == null) {
            return null;
        }

        PayOrderExtensionDO.PayOrderExtensionDOBuilder payOrderExtensionDO = PayOrderExtensionDO.builder();

        if (bean != null) {
            if (bean.getOrderId() != null) {
                payOrderExtensionDO.orderId(orderId);
            }
            payOrderExtensionDO.channelCode(bean.getChannelCode());
            Map<String, String> map = bean.getChannelExtras();
            if (map != null) {
                payOrderExtensionDO.channelExtras(new LinkedHashMap<String, String>(map));
            }
        }
        payOrderExtensionDO.userIp(userIp);

        return payOrderExtensionDO.build();

    }

    @Mapping(target = "id", ignore = true)
    default PayOrderExtensionDO convert(PayOrderRepaySubmitReqVO bean, String userIp,Long orderId) {
        if (bean == null && userIp == null) {
            return null;
        }

        PayOrderExtensionDO.PayOrderExtensionDOBuilder payOrderExtensionDO = PayOrderExtensionDO.builder();

        if (bean != null) {
            if (bean.getOrderId() != null) {
                payOrderExtensionDO.orderId(orderId);
            }
            payOrderExtensionDO.channelCode(bean.getChannelCode());
            Map<String, String> map = bean.getChannelExtras();
            if (map != null) {
                payOrderExtensionDO.channelExtras(new LinkedHashMap<String, String>(map));
            }
        }
        payOrderExtensionDO.userIp(userIp);

        return payOrderExtensionDO.build();

    }

    PayOrderUnifiedReqDTO convert2(PayOrderSubmitReqVO reqVO);

    PayOrderUnifiedReqDTO convert4(PayOrder2ReqVO reqVO);

    PayOrderSubmitRespVO convert(PayOrderUnifiedRespDTO bean);

//    AppPayOrderSubmitRespVO convert3(PayOrderSubmitRespVO bean);

}
