package com.starcloud.ops.business.user.dal.mysql.rights;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.user.controller.admin.rights.vo.AdminUserRightsRecordPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * 用户积分记录 Mapper
 *
 * @author QingX
 */
@Mapper
public interface AdminUserRightsRecordMapper extends BaseMapperX<AdminUserRightsRecordDO> {

    default PageResult<AdminUserRightsRecordDO> selectPage(AdminUserRightsRecordPageReqVO reqVO, Set<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserRightsRecordDO>()
                .inIfPresent(AdminUserRightsRecordDO::getUserId, userIds)
                .eqIfPresent(AdminUserRightsRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AdminUserRightsRecordDO::getBizType, reqVO.getBizType())
                .likeIfPresent(AdminUserRightsRecordDO::getTitle, reqVO.getTitle())
                .orderByDesc(AdminUserRightsRecordDO::getId));
    }

    default PageResult<AdminUserRightsRecordDO> selectPage(Long userId, PageParam pageVO) {
        return selectPage(pageVO, new LambdaQueryWrapperX<AdminUserRightsRecordDO>()
                .eq(AdminUserRightsRecordDO::getUserId, userId)
                .orderByDesc(AdminUserRightsRecordDO::getId));
    }

}
