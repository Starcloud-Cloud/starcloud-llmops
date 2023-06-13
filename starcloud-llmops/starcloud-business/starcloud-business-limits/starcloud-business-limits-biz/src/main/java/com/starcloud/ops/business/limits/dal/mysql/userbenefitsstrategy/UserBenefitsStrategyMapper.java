package com.starcloud.ops.business.limits.dal.mysql.userbenefitsstrategy;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyPageReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户权益策略表
 Mapper
 *
 * @author AlanCusack
 */
@Mapper
public interface UserBenefitsStrategyMapper extends BaseMapperX<UserBenefitsStrategyDO> {

    default PageResult<UserBenefitsStrategyDO> selectPage(UserBenefitsStrategyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserBenefitsStrategyDO>()
                .eqIfPresent(UserBenefitsStrategyDO::getCode, reqVO.getCode())
                .likeIfPresent(UserBenefitsStrategyDO::getStrategyName, reqVO.getStrategyName())
                .eqIfPresent(UserBenefitsStrategyDO::getStrategyDesc, reqVO.getStrategyDesc())
                .eqIfPresent(UserBenefitsStrategyDO::getStrategyType, reqVO.getStrategyType())
                .eqIfPresent(UserBenefitsStrategyDO::getAppCount, reqVO.getAppCount())
                .eqIfPresent(UserBenefitsStrategyDO::getDatasetCount, reqVO.getDatasetCount())
                .eqIfPresent(UserBenefitsStrategyDO::getImageCount, reqVO.getImageCount())
                .eqIfPresent(UserBenefitsStrategyDO::getTokenCount, reqVO.getTokenCount())
                .eqIfPresent(UserBenefitsStrategyDO::getScope, reqVO.getScope())
                .eqIfPresent(UserBenefitsStrategyDO::getScopeNum, reqVO.getScopeNum())
                .eqIfPresent(UserBenefitsStrategyDO::getLimitUnit, reqVO.getLimitUnit())
                .eqIfPresent(UserBenefitsStrategyDO::getEnabled, reqVO.getEnabled())
                .eqIfPresent(UserBenefitsStrategyDO::getArchived, reqVO.getArchived())
                .eqIfPresent(UserBenefitsStrategyDO::getArchivedBy, reqVO.getArchivedBy())
                .betweenIfPresent(UserBenefitsStrategyDO::getArchivedTime, reqVO.getArchivedTime())
                .betweenIfPresent(UserBenefitsStrategyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserBenefitsStrategyDO::getId));
    }


}