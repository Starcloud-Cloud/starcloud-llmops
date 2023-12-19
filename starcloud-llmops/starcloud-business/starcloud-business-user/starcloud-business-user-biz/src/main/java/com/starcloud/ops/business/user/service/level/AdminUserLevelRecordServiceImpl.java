package com.starcloud.ops.business.user.service.level;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleServiceImpl;
import com.starcloud.ops.business.user.controller.admin.level.vo.record.AdminUserLevelRecordCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.record.AdminUserLevelRecordPageReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.record.AdminUserLevelRecordRespVO;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelRecordConvert;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelRecordDO;
import com.starcloud.ops.business.user.dal.mysql.level.AdminUserLevelRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.LEVEL_NOT_EXISTS;

/**
 * 会员等级记录 Service 实现类
 *
 * @author owen
 */
@Service
@Validated
public class AdminUserLevelRecordServiceImpl implements AdminUserLevelRecordService {

    @Resource
    private AdminUserLevelRecordMapper adminUserLevelRecordMapper;

    @Resource
    private AdminUserLevelConfigService levelConfigService;


    @Resource
    private PermissionService permissionService;

    @Override
    public AdminUserLevelRecordDO getLevelRecord(Long id) {
        return adminUserLevelRecordMapper.selectById(id);
    }

    @Override
    public PageResult<AdminUserLevelRecordDO> getLevelRecordPage(AdminUserLevelRecordPageReqVO pageReqVO) {
        return adminUserLevelRecordMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createLevelRecord(AdminUserLevelRecordCreateReqVO createReqVO) {
        // 1.0 根据会员配置等级 获取会员配置信息
        AdminUserLevelConfigDO levelConfig = levelConfigService.getLevelConfig(createReqVO.getLevelId());
        if (levelConfig == null) {
            throw exception(LEVEL_NOT_EXISTS);
        }
        // 2.0 设置会员有效期
        LocalDateTime startTime = LocalDateTimeUtil.now();
        LocalDateTime endTime;
        // 2.1 判断当前会员是否有当前等级信息
        AdminUserLevelRecordDO latestExpirationByLevel = findLatestExpirationByLevel(createReqVO.getUserId(), createReqVO.getLevelId());
        if (latestExpirationByLevel != null) {
            startTime = latestExpirationByLevel.getValidEndTime();
        }

        if (createReqVO.getStartTime() != null && createReqVO.getEndTime() != null) {
            startTime = createReqVO.getStartTime();
            endTime = createReqVO.getEndTime() ;
        }else {
            endTime = getSpecificTime(startTime, createReqVO.getTimeNums(), createReqVO.getTimeRange());
        }

        AdminUserLevelRecordDO AdminUserLevelRecordDO = AdminUserLevelRecordConvert.INSTANCE.convert01(createReqVO, levelConfig.getName(), startTime, endTime);

        // 3.0 添加会员等级记录
        adminUserLevelRecordMapper.insert(AdminUserLevelRecordDO);
        // 获取当前用户角色
        Set<Long> userRoleIdListByUserId = permissionService.getUserRoleIdListByUserId(createReqVO.getUserId());

        userRoleIdListByUserId.add(levelConfig.getRoleId());
        // 重新设置用户角色
        permissionService.assignUserRole(createReqVO.getUserId(), userRoleIdListByUserId);


    }

    public AdminUserLevelRecordDO findLatestExpirationByLevel(Long userId, Long levelId) {
        // 1.0 根据会员配置等级 获取会员配置信息
        return adminUserLevelRecordMapper.findLatestExpirationByLevel(userId, levelId);

    }

    public LocalDateTime getSpecificTime(LocalDateTime times, Integer timeNums, Integer TimeRange) {
        Assert.notNull(times);
        // 1.0 根据会员配置等级 获取会员配置信息
        TimeRangeTypeEnum timeRangeTypeEnum = TimeRangeTypeEnum.getByType(TimeRange);

        switch (timeRangeTypeEnum) {
            case DAY:
                return times.plusDays(timeNums);
            case WEEK:
                return times.plusWeeks(timeNums);
            case MONTH:
                return times.plusMonths(timeNums);
            case YEAR:
                return times.plusYears(timeNums);
            default:
                return times;
        }
    }


}
