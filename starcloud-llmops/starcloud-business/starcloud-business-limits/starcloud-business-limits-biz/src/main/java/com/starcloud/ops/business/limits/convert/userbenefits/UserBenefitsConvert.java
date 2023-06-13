package com.starcloud.ops.business.limits.convert.userbenefits;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsRespVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsUpdateReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 用户权益 Convert
 *
 * @author AlanCusack
 */
@Mapper
public interface UserBenefitsConvert {

    UserBenefitsConvert INSTANCE = Mappers.getMapper(UserBenefitsConvert.class);

    UserBenefitsDO convert(UserBenefitsCreateReqVO bean);

    UserBenefitsDO convert(UserBenefitsUpdateReqVO bean);

    UserBenefitsRespVO convert(UserBenefitsDO bean);

    List<UserBenefitsRespVO> convertList(List<UserBenefitsDO> list);

    PageResult<UserBenefitsRespVO> convertPage(PageResult<UserBenefitsDO> page);


}
