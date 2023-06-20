package com.starcloud.ops.business.order.convert.merchant;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.merchant.PayMerchantCreateReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.merchant.PayMerchantRespVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.merchant.PayMerchantUpdateReqVO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayMerchantDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayMerchantConvert {

    PayMerchantConvert INSTANCE = Mappers.getMapper(PayMerchantConvert.class);

    PayMerchantDO convert(PayMerchantCreateReqVO bean);

    PayMerchantDO convert(PayMerchantUpdateReqVO bean);

    PayMerchantRespVO convert(PayMerchantDO bean);

    List<PayMerchantRespVO> convertList(List<PayMerchantDO> list);

    PageResult<PayMerchantRespVO> convertPage(PageResult<PayMerchantDO> page);


}
