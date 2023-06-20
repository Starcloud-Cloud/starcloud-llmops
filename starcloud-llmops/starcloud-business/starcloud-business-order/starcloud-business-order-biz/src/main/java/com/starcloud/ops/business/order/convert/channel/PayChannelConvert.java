package com.starcloud.ops.business.order.convert.channel;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.channel.PayChannelCreateReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.channel.PayChannelRespVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.channel.PayChannelUpdateReqVO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayChannelDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayChannelConvert {

    PayChannelConvert INSTANCE = Mappers.getMapper(PayChannelConvert.class);

    @Mapping(target = "config",ignore = true)
    PayChannelDO convert(PayChannelCreateReqVO bean);

    @Mapping(target = "config",ignore = true)
    PayChannelDO convert(PayChannelUpdateReqVO bean);

    @Mapping(target = "config",expression = "java(cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString(bean.getConfig()))")
    PayChannelRespVO convert(PayChannelDO bean);

    List<PayChannelRespVO> convertList(List<PayChannelDO> list);

    PageResult<PayChannelRespVO> convertPage(PageResult<PayChannelDO> page);




}
