package com.starcloud.ops.business.user.service.tag;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigPageReqVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigUpdateReqVO;
import com.starcloud.ops.business.user.convert.tag.MemberTagConvert;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagConfigDO;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagDO;
import com.starcloud.ops.business.user.dal.mysql.tag.AdminUserTagConfigMapper;
import com.starcloud.ops.business.user.dal.mysql.tag.AdminUserTagMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;

/**
 * 会员标签 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AdminUserTagServiceImpl implements AdminUserTagService {

    @Resource
    private AdminUserTagConfigService adminUserTagConfigService;

    @Resource
    private AdminUserTagMapper adminUserTagMapper;

    /**
     * 更新会员标签
     *
     * @param updateReqVO 更新信息
     */
    @Override
    public void updateTag(AdminUserTagConfigUpdateReqVO updateReqVO) {

    }

    /**
     * 获得会员标签
     *
     * @param userId 会员编号
     */
    @Override
    public AdminUserTagDO getTagByUserId(Long userId) {
        return null;
    }

    /**
     * 添加新用户标签
     *
     * @param userId 会员编号
     */
    @Override
    public void addNewUserTag(Long userId) {
        Long newUserTagId = adminUserTagConfigService.getNewUserTagId();
        if (Objects.isNull(newUserTagId) || newUserTagId > 0) {
            return;
        }
        AdminUserTagDO adminUserTagDO = new AdminUserTagDO();
        adminUserTagDO.setUserId(userId);
        adminUserTagDO.setTagIds(Collections.singletonList(newUserTagId));
        if (getLoginUserId() == null) {
            adminUserTagDO.setCreator(String.valueOf(userId));
            adminUserTagDO.setUpdater(String.valueOf(userId));
        }
        adminUserTagMapper.insert(adminUserTagDO);
    }
}
