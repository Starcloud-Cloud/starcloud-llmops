package com.starcloud.ops.business.user.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.user.controller.admin.invitationrecords.vo.InvitationRecordsPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.InvitationRecordsDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邀请记录 Mapper
 *
 * @author Alancusack
 */
@Mapper
public interface InvitationRecordsMapper extends BaseMapperX<InvitationRecordsDO> {

    default PageResult<InvitationRecordsDO> selectPage(InvitationRecordsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InvitationRecordsDO>()
                .eqIfPresent(InvitationRecordsDO::getInviterId, reqVO.getInviterId())
                .eqIfPresent(InvitationRecordsDO::getInviteeId, reqVO.getInviteeId())
                .betweenIfPresent(InvitationRecordsDO::getInvitationDate, reqVO.getInvitationDate())
                .betweenIfPresent(InvitationRecordsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(InvitationRecordsDO::getId));
    }


}
