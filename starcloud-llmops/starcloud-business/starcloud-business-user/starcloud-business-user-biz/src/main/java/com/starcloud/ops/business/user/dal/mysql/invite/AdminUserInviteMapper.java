package com.starcloud.ops.business.user.dal.mysql.invite;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.user.controller.admin.invite.vo.records.InvitationRecordsPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邀请记录 Mapper
 *
 * @author Alancusack
 */
@Mapper
public interface AdminUserInviteMapper extends BaseMapperX<AdminUserInviteDO> {

    default PageResult<AdminUserInviteDO> selectPage(InvitationRecordsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserInviteDO>()
                .eqIfPresent(AdminUserInviteDO::getInviterId, reqVO.getInviterId())
                .eqIfPresent(AdminUserInviteDO::getInviteeId, reqVO.getInviteeId())
                .betweenIfPresent(AdminUserInviteDO::getInvitationDate, reqVO.getInvitationDate())
                .betweenIfPresent(AdminUserInviteDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdminUserInviteDO::getId));
    }


}
