package com.starcloud.ops.business.limits.convert.userbenefitsstrategy;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.api.benefitsstrategy.dto.UserBenefitsStrategyBaseDTO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyRespVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyUpdateReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import lombok.experimental.UtilityClass;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户权益策略表
 * Convert
 *
 * @author AlanCusack
 */
@Mapper
public interface UserBenefitsStrategyConvert {

    UserBenefitsStrategyConvert INSTANCE = Mappers.getMapper(UserBenefitsStrategyConvert.class);
    UserBenefitsStrategyDO convert(UserBenefitsStrategyCreateReqVO bean);

    UserBenefitsStrategyDO convert(UserBenefitsStrategyUpdateReqVO bean);

    UserBenefitsStrategyRespVO convert(UserBenefitsStrategyDO bean);

    List<UserBenefitsStrategyRespVO> convertList(List<UserBenefitsStrategyDO> list);

    PageResult<UserBenefitsStrategyRespVO> convertPage(PageResult<UserBenefitsStrategyDO> page);

    UserBenefitsStrategyBaseDTO convertBaseDTO(UserBenefitsStrategyDO bean);
}