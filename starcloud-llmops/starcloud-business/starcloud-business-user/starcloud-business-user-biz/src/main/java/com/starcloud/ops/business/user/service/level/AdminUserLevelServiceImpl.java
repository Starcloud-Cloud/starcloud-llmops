package com.starcloud.ops.business.user.service.level;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.redis.RedisKeyConstants;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelPageReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.NotifyExpiringLevelRespVO;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelConvert;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.dal.mysql.level.AdminUserLevelMapper;
import com.starcloud.ops.business.user.enums.level.AdminUserLevelBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.ROLE_NOT_EXISTS;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.LEVEL_NOT_EXISTS;

/**
 * 会员等级记录 Service 实现类
 *
 * @author owen
 */
@Service
@Validated
public class AdminUserLevelServiceImpl implements AdminUserLevelService {

    @Resource
    private AdminUserLevelMapper adminUserLevelMapper;

    @Resource
    private AdminUserLevelConfigService levelConfigService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleService roleService;

    @Value("${starcloud-llm.role.code:mofaai_free}")
    private String roleCode;

    @Override
    public AdminUserLevelDO getLevel(Long id) {
        return adminUserLevelMapper.selectById(id);
    }

    @Override
    public PageResult<AdminUserLevelDO> getLevelPage(AdminUserLevelPageReqVO pageReqVO) {
        return adminUserLevelMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
//    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#reqVO.permission",
//            condition = "#reqVO.permission != null")
    public void createLevelRecord(AdminUserLevelCreateReqVO createReqVO) {
        // 1.0 根据会员配置等级 获取会员配置信息
        AdminUserLevelConfigDO levelConfig = levelConfigService.getLevelConfig(createReqVO.getLevelId());
        if (levelConfig == null) {
            throw exception(LEVEL_NOT_EXISTS);
        }
        // 2.0 设置会员有效期
        LocalDateTime startTime = LocalDateTimeUtil.now();
        LocalDateTime endTime;
        // 2.1 判断当前会员是否有当前等级信息
        AdminUserLevelDO latestExpirationByLevel = findLatestExpirationByLevel(createReqVO.getUserId(), createReqVO.getLevelId());
        if (latestExpirationByLevel != null) {
            startTime = latestExpirationByLevel.getValidEndTime();
        }

        if (createReqVO.getStartTime() != null && createReqVO.getEndTime() != null) {
            startTime = createReqVO.getStartTime();
            endTime = createReqVO.getEndTime();
        } else {
            endTime = getSpecificTime(startTime, createReqVO.getTimeNums(), createReqVO.getTimeRange());
        }

        AdminUserLevelDO AdminUserLevelDO = AdminUserLevelConvert.INSTANCE.convert01(createReqVO, levelConfig.getName(), startTime, endTime);

        // 3.0 添加会员等级记录
        adminUserLevelMapper.insert(AdminUserLevelDO);
        // 获取当前用户角色
        Set<Long> userRoleIdListByUserId = permissionService.getUserRoleIdListByUserId(createReqVO.getUserId());

        userRoleIdListByUserId.add(levelConfig.getRoleId());
        // 重新设置用户角色
        permissionService.assignUserRole(createReqVO.getUserId(), userRoleIdListByUserId);


    }

    /**
     * 创建会员默认等级记录
     * 默认为 免费版会员 99 年
     *
     * @param userId 用户 ID
     */
    @Override
    public void createInitLevelRecord(Long userId) {
        RoleDO role = roleService.getRoleByCode(roleCode);

        if (role == null) {
            throw exception(ROLE_NOT_EXISTS);
        }

        AdminUserLevelConfigDO levelConfigDO = levelConfigService.getLevelByRoleId(role.getId());
        if (Objects.isNull(levelConfigDO)) {
            throw exception(LEVEL_NOT_EXISTS);
        }
        AdminUserLevelCreateReqVO createReqVO = new AdminUserLevelCreateReqVO();
        createReqVO.setUserId(userId);
        createReqVO.setLevelId(levelConfigDO.getId());

        createReqVO.setBizId(String.valueOf(userId));
        createReqVO.setBizType(AdminUserLevelBizTypeEnum.REGISTER.getType());

        createReqVO.setStartTime(LocalDateTime.now());
        createReqVO.setEndTime(LocalDateTime.now().plusYears(99));

        createReqVO.setDescription(String.format(AdminUserLevelBizTypeEnum.REGISTER.getDescription(), userId));
        createLevelRecord(createReqVO);

    }

    /**
     * 获取会员下有效的等级列表
     *
     * @param userId
     */
    @Override
    public List<AdminUserLevelDO> getLevelList(Long userId) {
        return adminUserLevelMapper.selectValidList(userId);

    }

    /**
     * 等级过期提醒
     *
     * @param userId 用户 ID
     */
    @Override
    public NotifyExpiringLevelRespVO notifyExpiringLevel(Long userId) {

        NotifyExpiringLevelRespVO notifyExpiringLevelRespVO = new NotifyExpiringLevelRespVO();
        notifyExpiringLevelRespVO.setIsNotify(false);
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime nextWeek = today.plusDays(7);
        List<AdminUserLevelDO> validLevelList = adminUserLevelMapper.selectValidList(userId);
        if (CollUtil.isEmpty(validLevelList)){
            return notifyExpiringLevelRespVO;
        }

        // 获取 7 天内即将过期的等级
        List<AdminUserLevelDO> nextWeekExpiringLevel = validLevelList.stream()
                .filter(level -> level.getValidEndTime().isBefore(nextWeek) && level.getValidEndTime().isAfter(today))
                .sorted(Comparator.comparing(AdminUserLevelDO::getValidEndTime).reversed())
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(nextWeekExpiringLevel)){
            return notifyExpiringLevelRespVO;
        }

        notifyExpiringLevelRespVO.setLevelId(nextWeekExpiringLevel.get(0).getLevelId());
        notifyExpiringLevelRespVO.setLevelName(nextWeekExpiringLevel.get(0).getLevelName());
        notifyExpiringLevelRespVO.setValidEndTime(nextWeekExpiringLevel.get(0).getValidEndTime());
        notifyExpiringLevelRespVO.setIsNotify(true);
        return notifyExpiringLevelRespVO;
    }

    public AdminUserLevelDO findLatestExpirationByLevel(Long userId, Long levelId) {
        // 1.0 根据会员配置等级 获取会员配置信息
        return adminUserLevelMapper.findLatestExpirationByLevel(userId, levelId);

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
