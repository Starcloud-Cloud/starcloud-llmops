package com.starcloud.ops.business.user.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.user.controller.admin.invitationrecords.vo.InvitationRecordsPageReqVO;
import com.starcloud.ops.business.user.dal.dataObject.InvitationRecordsDO;
import com.starcloud.ops.business.user.dal.mysql.InvitationRecordsMapper;
import com.starcloud.ops.business.user.service.InvitationRecordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 邀请记录 Service 实现类
 *
 * @author Alancusack
 */
@Slf4j
@Service
@Validated
public class InvitationRecordsServiceImpl implements InvitationRecordsService {

    @Resource
    private InvitationRecordsMapper invitationRecordsMapper;

    @Override
    public void createInvitationRecords(Long inviterId, Long inviteeId ) {
        try {
            log.info("[createInvitationRecords][增加邀请记录：邀请人用户ID({})｜被邀请人({})]", inviterId, inviteeId);
            // 插入
            InvitationRecordsDO invitationRecords = new InvitationRecordsDO();
            invitationRecords.setInviterId(inviterId);
            invitationRecords.setInviteeId(inviteeId);
            invitationRecords.setInvitationDate(LocalDateTimeUtil.now());
            invitationRecords.setCreator(String.valueOf(inviteeId));
            invitationRecords.setUpdater(String.valueOf(inviteeId));
            invitationRecordsMapper.insert(invitationRecords);
        }catch (RuntimeException e){
            log.error("[createInvitationRecords][增加邀请记录失败：邀请人用户ID({})｜被邀请人({})]", inviterId, inviteeId);
        }

    }


    @Override
    public List<InvitationRecordsDO> getInvitationRecords(Long userId) {
        LambdaQueryWrapper<InvitationRecordsDO> queryWrapper = Wrappers.lambdaQuery(InvitationRecordsDO.class)
                .eq(InvitationRecordsDO::getInviterId, userId)
                .orderByAsc(InvitationRecordsDO::getId);
        return invitationRecordsMapper.selectList(queryWrapper);
    }

    @Override
    public List<InvitationRecordsDO> getInvitationRecordsList(Collection<Long> ids) {
        return invitationRecordsMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<InvitationRecordsDO> getInvitationRecordsPage(InvitationRecordsPageReqVO pageReqVO) {
        return invitationRecordsMapper.selectPage(pageReqVO);
    }


}
