package com.starcloud.ops.business.user.api.level;

import com.starcloud.ops.business.user.api.level.dto.AdminUserLevelRespDTO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelRecordDO;
import com.starcloud.ops.business.user.service.level.AdminUserLevelRecordService;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 会员等级 API 实现类
 *
 * @author owen
 */
@Service
@Validated
@Slf4j
public class AdminUserLevelApiImpl implements AdminUserLevelApi {

    @Resource
    private AdminUserLevelService adminUserLevelService;
    @Resource
    private AdminUserLevelRecordService adminUserLevelRecordService;


    /**
     * 获得会员等级列表
     *
     * @param userId 会员ID
     * @return 会员等级
     */
    @Override
    public List<AdminUserLevelRespDTO> getAdminUserLevelList(Long userId) {
        return null;
    }

    /**
     * 新增会员等级
     *
     * @param userId  会员ID
     * @param levelId 会员等级编号
     * @return 会员等级
     */
    @Override
    public void addAdminUserLevel(Long userId, Long levelId) {
        log.warn("设置会员等级");
        AdminUserLevelDO level = adminUserLevelService.getLevel(levelId);
        AdminUserLevelRecordDO adminUserLevelRecordDO = new AdminUserLevelRecordDO();
        adminUserLevelRecordDO.setUserId(userId);
        adminUserLevelRecordDO.setUserId(levelId);
        adminUserLevelRecordDO.setLevelBefore(level.getLevel());
        adminUserLevelRecordDO.setLevelAfter(level.getLevel());
        adminUserLevelRecordDO.setValidStartTime(null);
        adminUserLevelRecordDO.setValidEndTime(null);
        adminUserLevelRecordService.createLevelRecord(adminUserLevelRecordDO);
    }

    /**
     * 过期会员等级
     *
     * @param userId  会员ID
     * @param levelId 会员等级编号
     * @return 会员等级
     */
    @Override
    public void expireAdminUserLevel(Long userId, Long levelId) {

    }
}
