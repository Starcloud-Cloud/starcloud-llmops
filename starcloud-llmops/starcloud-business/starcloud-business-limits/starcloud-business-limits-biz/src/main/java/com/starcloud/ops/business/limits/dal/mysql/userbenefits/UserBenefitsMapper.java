package com.starcloud.ops.business.limits.dal.mysql.userbenefits;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsPageReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * 用户权益 Mapper
 *
 * @author AlanCusack
 */
@Mapper
public interface UserBenefitsMapper extends BaseMapperX<UserBenefitsDO> {

    default PageResult<UserBenefitsDO> selectPage(UserBenefitsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserBenefitsDO>()
                .eqIfPresent(UserBenefitsDO::getUserId, reqVO.getUserId())
                // .ge(UserBenefitsDO::getExpirationTime, LocalDateTime.now())
                .orderByDesc(UserBenefitsDO::getId));
    }
}
