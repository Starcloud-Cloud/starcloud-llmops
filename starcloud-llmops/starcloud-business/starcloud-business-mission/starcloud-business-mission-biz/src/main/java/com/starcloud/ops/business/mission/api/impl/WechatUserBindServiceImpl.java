package com.starcloud.ops.business.mission.api.impl;

import cn.hutool.core.util.ReUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.service.group.MemberGroupService;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.mission.api.WechatUserBindService;
import com.starcloud.ops.business.mission.api.vo.request.WechatUserBindReqVO;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;

@Slf4j
@Service
public class WechatUserBindServiceImpl implements WechatUserBindService {

    @Resource
    private MemberGroupService groupService;

    @Resource
    private MemberUserService memberUserService;

    @Resource
    private AdminUserService adminUserService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindGroup(WechatUserBindReqVO reqVO) {
        Long adminUserId = EncryptionUtils.decrypt(reqVO.getInviteCode());
        AdminUserDO adminUserDO = adminUserService.getUser(adminUserId);
        if (adminUserDO == null) {
            throw exception(USER_NOT_EXISTS);
        }
        String groupName = adminUserDO.getUsername() + "-" + adminUserId;
        MemberGroupDO memberGroupDO = groupService.saveGroup(groupName);
        Long memberUserId = SecurityFrameworkUtils.getLoginUserId();
        memberUserService.updateUserGroup(memberUserId, memberGroupDO.getId());
    }

    @Override
    public String getBindUser(Long memberUserId) {
        MemberUserDO user = memberUserService.getUser(memberUserId);
        MemberGroupDO groupDO = groupService.getGroup(user.getGroupId());
        return ReUtil.get("-([0-9]+$)", groupDO.getName(), 1);
    }
}
