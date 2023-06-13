package com.starcloud.ops.business.limits.dal.mysql.userbenefits;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsPageReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户权益 Mapper
 *
 * @author AlanCusack
 */
@Mapper
public interface UserBenefitsMapper extends BaseMapperX<UserBenefitsDO> {

    default PageResult<UserBenefitsDO> selectPage(UserBenefitsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserBenefitsDO>()
                .eqIfPresent(UserBenefitsDO::getUid, reqVO.getUid())
                .eqIfPresent(UserBenefitsDO::getUserId, reqVO.getUserId())
                .eqIfPresent(UserBenefitsDO::getStrategyId, reqVO.getStrategyId())
                .eqIfPresent(UserBenefitsDO::getAppCountUsed, reqVO.getAppCountUsed())
                .eqIfPresent(UserBenefitsDO::getDatasetCountUsed, reqVO.getDatasetCountUsed())
                .eqIfPresent(UserBenefitsDO::getImageCountUsed, reqVO.getImageCountUsed())
                .eqIfPresent(UserBenefitsDO::getTokenCountUsed, reqVO.getTokenCountUsed())
                .betweenIfPresent(UserBenefitsDO::getEffectiveTime, reqVO.getEffectiveTime())
                .betweenIfPresent(UserBenefitsDO::getExpirationTime, reqVO.getExpirationTime())
                .betweenIfPresent(UserBenefitsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserBenefitsDO::getId));
    }
}
