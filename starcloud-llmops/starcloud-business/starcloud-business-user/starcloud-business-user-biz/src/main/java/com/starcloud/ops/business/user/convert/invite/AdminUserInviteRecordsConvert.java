package com.starcloud.ops.business.user.convert.invite;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.user.controller.admin.invite.vo.records.InvitationRecordsCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.invite.vo.records.InvitationRecordsRespVO;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 邀请记录 Convert
 *
 * @author Alancusack
 */
@Mapper
public interface AdminUserInviteRecordsConvert {

    AdminUserInviteRecordsConvert INSTANCE = Mappers.getMapper(AdminUserInviteRecordsConvert.class);

    AdminUserInviteDO convert(InvitationRecordsCreateReqVO bean);


    InvitationRecordsRespVO convert(AdminUserInviteDO bean);

    List<InvitationRecordsRespVO> convertList(List<AdminUserInviteDO> list);

    PageResult<InvitationRecordsRespVO> convertPage(PageResult<AdminUserInviteDO> page);


}
