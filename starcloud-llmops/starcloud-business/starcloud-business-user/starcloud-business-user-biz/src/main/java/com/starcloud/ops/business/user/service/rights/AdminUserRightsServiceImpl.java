package com.starcloud.ops.business.user.service.rights;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.rights.vo.AdminUserRightsPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.dal.mysql.rights.AdminUserRightsMapper;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;


/**
 * 积分记录 Service 实现类
 *
 * @author QingX
 */
@Slf4j
@Service
@Validated
public class AdminUserRightsServiceImpl implements AdminUserRightsService {

    @Resource
    private AdminUserRightsMapper adminUserRightsMapper;

    @Resource
    private AdminUserService adminUserService;

    @Override
    public PageResult<AdminUserRightsDO> getPointRecordPage(AdminUserRightsPageReqVO pageReqVO) {
        // 根据用户昵称查询出用户 ids
        Set<Long> userIds = null;
        if (StringUtils.isNotBlank(pageReqVO.getNickname())) {
            List<AdminUserDO> users = adminUserService.getUserListByNickname(pageReqVO.getNickname());
            // 如果查询用户结果为空直接返回无需继续查询
            if (CollectionUtils.isEmpty(users)) {
                return PageResult.empty();
            }
            userIds = convertSet(users, AdminUserDO::getId);
        }
        // 执行查询
        return adminUserRightsMapper.selectPage(pageReqVO, userIds);
    }

    @Override
    public PageResult<AdminUserRightsDO> getPointRecordPage(Long userId, PageParam pageVO) {
        return adminUserRightsMapper.selectPage(userId, pageVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPointRecord(Long userId, Integer magicBean, Integer magicImage, AdminUserRightsBizTypeEnum bizType, String bizId) {
        if (magicBean == 0 || magicImage == 0) {
            return;
        }

        // 3. 增加积分记录
        AdminUserRightsDO record = new AdminUserRightsDO()
                .setUserId(userId).setBizId(bizId).setBizType(bizType.getType())
                .setTitle(bizType.getName()).setDescription(StrUtil.format(bizType.getDescription(), magicBean, magicImage))
                .setMagicBean(magicBean).setMagicImage(magicImage).setMagicBeanInit(magicBean).setMagicImageInit(magicImage);
        adminUserRightsMapper.insert(record);
    }

}
