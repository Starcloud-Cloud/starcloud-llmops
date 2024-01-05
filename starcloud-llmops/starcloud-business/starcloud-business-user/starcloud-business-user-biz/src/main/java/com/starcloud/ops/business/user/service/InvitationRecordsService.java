package com.starcloud.ops.business.user.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.controller.admin.invitationrecords.vo.InvitationRecordsPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.InvitationRecordsDO;

import java.util.Collection;
import java.util.List;

/**
 * 邀请记录 Service 接口
 *
 * @author Alancusack
 */
public interface InvitationRecordsService {

    /**
     * 创建邀请记录
     *
     * @param inviterId 邀请人 ID
     * @param inviteeId 被邀请人 ID
     */
    Long createInvitationRecords(Long inviterId, Long inviteeId);


    /**
     * 根据用户ID 查询邀请记录
     * @param userId 用户ID
     * @return  List<InvitationRecordsDO>
     */
    List<InvitationRecordsDO> getInvitationRecords(Long userId);

    /**
     * 获得邀请记录列表
     *
     * @param ids 编号
     * @return 邀请记录列表
     */
    List<InvitationRecordsDO> getInvitationRecordsList(Collection<Long> ids);

    /**
     * 获得邀请记录分页
     *
     * @param pageReqVO 分页查询
     * @return 邀请记录分页
     */
    PageResult<InvitationRecordsDO> getInvitationRecordsPage(InvitationRecordsPageReqVO pageReqVO);


    /**
     * 获取当天邀请记录
     * @param userId 用户ID
     * @return  List<InvitationRecordsDO>
     */
    List<InvitationRecordsDO> getTodayInvitations(Long userId);

}
