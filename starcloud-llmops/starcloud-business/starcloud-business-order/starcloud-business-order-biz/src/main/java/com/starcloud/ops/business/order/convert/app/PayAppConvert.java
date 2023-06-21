package com.starcloud.ops.business.order.convert.app;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppCreateReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppPageItemRespVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppRespVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppUpdateReqVO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayMerchantDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 支付应用信息 Convert
 *
 * @author 芋艿
 */
@Mapper
public interface PayAppConvert {

    PayAppConvert INSTANCE = Mappers.getMapper(PayAppConvert.class);

    PayAppPageItemRespVO pageConvert (PayAppDO bean);

    PayAppPageItemRespVO.PayMerchant convert(PayMerchantDO bean);

    PayAppDO convert(PayAppCreateReqVO bean);

    PayAppDO convert(PayAppUpdateReqVO bean);

    PayAppRespVO convert(PayAppDO bean);

    List<PayAppRespVO> convertList(List<PayAppDO> list);

    PageResult<PayAppRespVO> convertPage(PageResult<PayAppDO> page);


}
