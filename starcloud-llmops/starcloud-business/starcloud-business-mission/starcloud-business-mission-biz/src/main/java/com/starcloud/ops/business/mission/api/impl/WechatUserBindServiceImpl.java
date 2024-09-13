package com.starcloud.ops.business.mission.api.impl;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.service.group.MemberGroupService;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import com.starcloud.ops.business.mission.api.WechatUserBindService;
import com.starcloud.ops.business.mission.api.vo.request.WechatUserBindReqVO;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.GROUP_NOT_EXIST;

@Slf4j
@Service
public class WechatUserBindServiceImpl implements WechatUserBindService {

    @Resource
    private MemberGroupService groupService;

    @Resource
    private MemberUserService memberUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindGroup(WechatUserBindReqVO reqVO) {
        Long adminUserId = EncryptionUtils.decrypt(reqVO.getInviteCode());
        MemberGroupDO memberGroupDO = groupService.selectByAdminUser(adminUserId);
        if (Objects.isNull(memberGroupDO)) {
            throw exception(GROUP_NOT_EXIST);
        }
        Long memberUserId = SecurityFrameworkUtils.getLoginUserId();
        memberUserService.updateUserGroup(memberUserId, memberGroupDO.getId());
    }

    @Override
    public String getBindUser() {
        Long memberUserId = SecurityFrameworkUtils.getLoginUserId();
        MemberUserDO user = memberUserService.getUser(memberUserId);
        if (Objects.isNull(user.getGroupId())) {
            throw exception(GROUP_NOT_EXIST);
        }
        MemberGroupDO groupDO = groupService.getGroup(user.getGroupId());
        if (Objects.isNull(groupDO)) {
            throw exception(GROUP_NOT_EXIST);
        }
        return groupDO.getAdminUserId().toString();
    }
}
