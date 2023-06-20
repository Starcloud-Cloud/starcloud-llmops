package com.starcloud.ops.business.order.convert.refund;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.order.controller.admin.refund.vo.*;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.refund.PayRefundDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayRefundConvert {

    PayRefundConvert INSTANCE = Mappers.getMapper(PayRefundConvert.class);

    PayRefundDO convert(PayRefundCreateReqVO bean);

    PayRefundDO convert(PayRefundUpdateReqVO bean);

    PayRefundRespVO convert(PayRefundDO bean);

    /**
     * 退款订单 DO 转 退款详情订单 VO
     *
     * @param bean 退款订单 DO
     * @return 退款详情订单 VO
     */
    PayRefundDetailsRespVO refundDetailConvert(PayRefundDO bean);

    /**
     * 退款订单DO 转 分页退款条目VO
     *
     * @param bean 退款订单DO
     * @return 分页退款条目VO
     */
    PayRefundPageItemRespVO pageItemConvert(PayRefundDO bean);

    List<PayRefundRespVO> convertList(List<PayRefundDO> list);

    PageResult<PayRefundRespVO> convertPage(PageResult<PayRefundDO> page);

    //TODO 太多需要处理了， 暂时不用
    @Mappings(value = {
            @Mapping(source = "amount", target = "payAmount"),
            @Mapping(source = "id", target = "orderId"),
            @Mapping(target = "status",ignore = true)
    })
    PayRefundDO convert(PayOrderDO orderDO);

}
