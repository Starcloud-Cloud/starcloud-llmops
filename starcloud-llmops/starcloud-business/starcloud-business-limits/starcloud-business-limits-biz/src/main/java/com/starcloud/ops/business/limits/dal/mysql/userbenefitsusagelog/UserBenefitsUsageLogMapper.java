package com.starcloud.ops.business.limits.dal.mysql.userbenefitsusagelog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogPageReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsusagelog.UserBenefitsUsageLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户权益使用日志 Mapper
 *
 * @author AlanCusack
 */
@Mapper
public interface UserBenefitsUsageLogMapper extends BaseMapperX<UserBenefitsUsageLogDO> {

    default PageResult<UserBenefitsUsageLogDO> selectPage(UserBenefitsUsageLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserBenefitsUsageLogDO>()
                .eqIfPresent(UserBenefitsUsageLogDO::getUid, reqVO.getUid())
                .eqIfPresent(UserBenefitsUsageLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(UserBenefitsUsageLogDO::getAction, reqVO.getAction())
                .eqIfPresent(UserBenefitsUsageLogDO::getBenefitsType, reqVO.getBenefitsType())
                .eqIfPresent(UserBenefitsUsageLogDO::getAmount, reqVO.getAmount())
                .eqIfPresent(UserBenefitsUsageLogDO::getOutId, reqVO.getOutId())
                .eqIfPresent(UserBenefitsUsageLogDO::getBenefitsIds, reqVO.getBenefitsIds())
                .betweenIfPresent(UserBenefitsUsageLogDO::getUsageTime, reqVO.getUsageTime())
                .betweenIfPresent(UserBenefitsUsageLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserBenefitsUsageLogDO::getId));
    }


}
