package com.starcloud.ops.business.promotion.convert.reward;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.promotion.controller.admin.reward.vo.RewardActivityCreateReqVO;
import com.starcloud.ops.business.promotion.controller.admin.reward.vo.RewardActivityRespVO;
import com.starcloud.ops.business.promotion.controller.admin.reward.vo.RewardActivityUpdateReqVO;
import com.starcloud.ops.business.promotion.dal.dataobject.reward.RewardActivityDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 满减送活动 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface RewardActivityConvert {

    RewardActivityConvert INSTANCE = Mappers.getMapper(RewardActivityConvert.class);

    RewardActivityDO convert(RewardActivityCreateReqVO bean);

    RewardActivityDO convert(RewardActivityUpdateReqVO bean);

    RewardActivityRespVO convert(RewardActivityDO bean);

    PageResult<RewardActivityRespVO> convertPage(PageResult<RewardActivityDO> page);

}
