package com.starcloud.ops.business.user.convert;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.user.controller.admin.invitationrecords.vo.InvitationRecordsCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.invitationrecords.vo.InvitationRecordsRespVO;
import com.starcloud.ops.business.user.dal.dataobject.InvitationRecordsDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 邀请记录 Convert
 *
 * @author Alancusack
 */
@Mapper
public interface InvitationRecordsConvert {

    InvitationRecordsConvert INSTANCE = Mappers.getMapper(InvitationRecordsConvert.class);

    InvitationRecordsDO convert(InvitationRecordsCreateReqVO bean);


    InvitationRecordsRespVO convert(InvitationRecordsDO bean);

    List<InvitationRecordsRespVO> convertList(List<InvitationRecordsDO> list);

    PageResult<InvitationRecordsRespVO> convertPage(PageResult<InvitationRecordsDO> page);


}
