package com.starcloud.ops.business.limits.dal.mysql.userbenefitsstrategy;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
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
                .eqIfPresent(UserBenefitsStrategyDO::getArchived, reqVO.getArchived())
                .eqIfPresent(UserBenefitsStrategyDO::getArchivedBy, reqVO.getArchivedBy())
                .betweenIfPresent(UserBenefitsStrategyDO::getArchivedTime, reqVO.getArchivedTime())
                .betweenIfPresent(UserBenefitsStrategyDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(UserBenefitsStrategyDO::getCode, reqVO.getCode())
                .likeIfPresent(UserBenefitsStrategyDO::getStrategyName, reqVO.getStrategyName())
                .eqIfPresent(UserBenefitsStrategyDO::getStrategyDesc, reqVO.getStrategyDesc())
                .eqIfPresent(UserBenefitsStrategyDO::getStrategyType, reqVO.getStrategyType())
                // .eqIfPresent(UserBenefitsStrategyDO::getAppCount, reqVO.getAppCount())
                // .eqIfPresent(UserBenefitsStrategyDO::getDatasetCount, reqVO.getDatasetCount())
                // .eqIfPresent(UserBenefitsStrategyDO::getImageCount, reqVO.getImageCount())
                // .eqIfPresent(UserBenefitsStrategyDO::getTokenCount, reqVO.getTokenCount())
                // .eqIfPresent(UserBenefitsStrategyDO::getEffectiveUnit, reqVO.getEffectiveUnit())
                // .eqIfPresent(UserBenefitsStrategyDO::getEffectiveNum, reqVO.getEffectiveNum())
                // .eqIfPresent(UserBenefitsStrategyDO::getLimitNum, reqVO.getLimitNum())
                // .eqIfPresent(UserBenefitsStrategyDO::getLimitIntervalUnit, reqVO.getLimitIntervalUnit())
                // .eqIfPresent(UserBenefitsStrategyDO::getLimitIntervalNum, reqVO.getLimitIntervalNum())
                .eqIfPresent(UserBenefitsStrategyDO::getEnabled, reqVO.getEnabled())
                .orderByDesc(UserBenefitsStrategyDO::getId));
    }

}