package com.starcloud.ops.business.user.service.level;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.api.level.dto.LevelConfigDTO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.*;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelConvert;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.mysql.level.AdminUserLevelMapper;
import com.starcloud.ops.business.user.dal.redis.UserLevelConfigLimitRedisDAO;
import com.starcloud.ops.business.user.enums.LevelRightsLimitEnums;
import com.starcloud.ops.business.user.enums.level.AdminUserLevelBizTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.ROLE_NOT_EXISTS;
import static cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum.getPlusTimeByRange;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;

/**
 * 会员等级记录 Service 实现类
 *
 * @author owen
 */
@Slf4j
@Service
@Validated
public class AdminUserLevelServiceImpl implements AdminUserLevelService {

    @Resource
    private AdminUserLevelMapper adminUserLevelMapper;

    @Resource
    private UserLevelConfigLimitRedisDAO userLevelConfigLimitRedisDAO;

    @Resource
    private AdminUserLevelConfigService levelConfigService;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private SmsSendApi smsSendApi;


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
            endTime = getPlusTimeByRange(createReqVO.getTimeRange(), createReqVO.getTimeNums(), startTime);
        }
        AdminUserLevelDO adminUserLevelDO = AdminUserLevelConvert.INSTANCE.convert01(createReqVO, levelConfig.getName(), startTime, endTime);

        adminUserLevelDO.setCreator(String.valueOf(createReqVO.getUserId()));
        adminUserLevelDO.setUpdater(String.valueOf(createReqVO.getUserId()));

        adminUserLevelDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        adminUserLevelDO.setDescription(StrUtil.format(AdminUserLevelBizTypeEnum.getByType(adminUserLevelDO.getBizType()).getDescription(), levelConfig.getName()));
        // 3.0 添加会员等级记录
        adminUserLevelMapper.insert(adminUserLevelDO);

        // 设置等级中绑定的角色
        getSelf().buildUserRole(adminUserLevelDO.getUserId(), levelConfig.getRoleId(), null);
    }

    /**
     * 创建会员默认等级记录
     * 默认为 免费版会员 99 年
     *
     * @param userId 用户 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
     * @param userId 用户 ID
     */
    @Override
    public List<AdminUserLevelDetailRespVO> getLevelList(Long userId) {
        List<AdminUserLevelDO> adminUserLevelDOS = adminUserLevelMapper.getValidAdminUserLevels(userId, null, LocalDateTime.now());
        List<AdminUserLevelDetailRespVO> adminUserLevelDetailRespVOS = new ArrayList<>();

        for (AdminUserLevelDO level : adminUserLevelDOS) {
            AdminUserLevelConfigDO levelConfig;
            try {
                levelConfig = levelConfigService.getLevelConfig(level.getLevelId());
            } catch (Exception e) {
                log.warn("未获取到该会员等级，{}", level.getLevelId());
                continue; // 或者跳过当前循环迭代
            }

            AdminUserLevelDetailRespVO adminUserLevelDetailRespVO = new AdminUserLevelDetailRespVO();
            adminUserLevelDetailRespVO.setUserId(userId)
                    .setLevelId(level.getLevelId())
                    .setLevelName(level.getLevelName())
                    .setBizType(level.getBizType());
            adminUserLevelDetailRespVO.setSort(levelConfig.getSort())
                    .setLevelConfigDTO(BeanUtil.toBean(levelConfig.getLevelConfig(), LevelConfigDTO.class));

            adminUserLevelDetailRespVOS.add(adminUserLevelDetailRespVO);
        }

        return adminUserLevelDetailRespVOS.stream()
                .sorted(Comparator.comparing(AdminUserLevelDetailRespVO::getSort).reversed())
                .collect(Collectors.toList());

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
        // 一周内提醒
        // LocalDateTime nextWeek = today.plusDays(7);
        // fix 三天内提醒

        LocalDateTime ThreeDaysLater = today.plusDays(3);

        List<AdminUserLevelDO> validLevelList = adminUserLevelMapper.getValidAdminUserLevels(userId, null, LocalDateTime.now());
        if (CollUtil.isEmpty(validLevelList)) {
            return notifyExpiringLevelRespVO;
        }

        // 1.0 获取 3 天内过期的所有用户等级

        // 判断是否存在生效的用户等级

        // 获取 3 天内即将过期的等级
        List<AdminUserLevelDO> nextWeekExpiringLevel = validLevelList.stream()
                .filter(level -> level.getValidEndTime().isBefore(ThreeDaysLater) && level.getValidEndTime().isAfter(today))
                .sorted(Comparator.comparing(AdminUserLevelDO::getValidEndTime).reversed())
                .collect(Collectors.toList());

        // 获取大于 3 天的用户等级
        List<AdminUserLevelDO> noExpiringLevelDOS = validLevelList.stream()
                .filter(level -> !level.getValidEndTime().isAfter(today) || !level.getValidEndTime().isBefore(ThreeDaysLater))
                .sorted(Comparator.comparing(AdminUserLevelDO::getValidEndTime).reversed())
                .collect(Collectors.toList());

        nextWeekExpiringLevel.removeIf(level -> noExpiringLevelDOS.stream()
                .anyMatch(noExpiringLevel -> noExpiringLevel.getLevelId().equals(level.getLevelId())));

        if (CollUtil.isEmpty(nextWeekExpiringLevel)) {
            return notifyExpiringLevelRespVO;
        }

        notifyExpiringLevelRespVO.setLevelId(nextWeekExpiringLevel.get(0).getLevelId());
        notifyExpiringLevelRespVO.setLevelName(nextWeekExpiringLevel.get(0).getLevelName());
        notifyExpiringLevelRespVO.setValidEndTime(nextWeekExpiringLevel.get(0).getValidEndTime());
        notifyExpiringLevelRespVO.setIsNotify(true);
        return notifyExpiringLevelRespVO;
    }

    /**
     * 设置默认等级
     */
    @Override
    public void setInitLevel() {
        RoleDO role = roleService.getRoleByCode(roleCode);

        if (role == null) {
            throw exception(ROLE_NOT_EXISTS);
        }

        AdminUserLevelConfigDO levelConfigDO = levelConfigService.getLevelByRoleId(role.getId());
        if (Objects.isNull(levelConfigDO)) {
            throw exception(LEVEL_NOT_EXISTS);
        }
        List<AdminUserDO> userDOS = adminUserService.getUserList();
        for (AdminUserDO adminUserDO : userDOS) {
            AdminUserLevelCreateReqVO createReqVO = new AdminUserLevelCreateReqVO();
            createReqVO.setUserId(adminUserDO.getId());
            createReqVO.setLevelId(levelConfigDO.getId());

            createReqVO.setBizId(String.valueOf(adminUserDO.getId()));
            createReqVO.setBizType(AdminUserLevelBizTypeEnum.REGISTER.getType());

            createReqVO.setStartTime(adminUserDO.getCreateTime());
            createReqVO.setEndTime(LocalDateTime.now().plusYears(99));

            createReqVO.setDescription(String.format(AdminUserLevelBizTypeEnum.REGISTER.getDescription(), adminUserDO.getId()));
            createLevelRecord(createReqVO);
        }


    }

    /**
     * @return 过期数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int expireLevel() {
        // 1. 查询过期的用户等级数据
        List<AdminUserLevelDO> levelDOS = adminUserLevelMapper.getUserLevelsNearExpiry(
                CommonStatusEnum.ENABLE.getStatus(), LocalDateTime.now());
        if (CollUtil.isEmpty(levelDOS)) {
            return 0;
        }

        // 2. 遍历执行，逐个取消
        int count = 0;
        for (AdminUserLevelDO levelDO : levelDOS) {
            try {
                getSelf().expireLevelBySystem(levelDO);
                count++;
            } catch (Throwable e) {
                log.error("[expireLevelBySystem][levelDO({}) 用户等级过期异常]", levelDO.getId(), e);
            }
        }
        return count;

    }

    /**
     * @param levelDO 等级 DO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void expireLevelBySystem(AdminUserLevelDO levelDO) {
        AdminUserLevelConfigDO levelConfig = levelConfigService.getLevelConfig(levelDO.getLevelId());
        // 注意：系统内存在多个角色 ID 对应同一等级数据
        List<AdminUserLevelConfigDO> levelConfigDOS = levelConfigService.getListByRoleId(levelConfig.getRoleId());

        levelConfigDOS.removeIf(config -> Objects.equals(config.getId(), levelDO.getLevelId()));
        if (!levelConfigDOS.isEmpty()) {
            List<Long> configIds = levelConfigDOS.stream().map(AdminUserLevelConfigDO::getId).collect(Collectors.toList());

            if (CollUtil.isEmpty(adminUserLevelMapper.getValidAdminUserLevels(levelDO.getUserId(), configIds, LocalDateTime.now()))) {
                // 移除过期等级中绑定的角色
                getSelf().buildUserRole(levelDO.getUserId(), null, levelConfig.getRoleId());
            }
        }

        // 更新 AdminUserLevelDO 状态为已关闭
        int updateCount = adminUserLevelMapper.updateByIdAndStatus(levelDO.getId(), levelDO.getStatus(),
                new AdminUserLevelDO().setStatus(CommonStatusEnum.DISABLE.getStatus()));
        if (updateCount == 0) {
            throw exception(LEVEL_EXPIRE_FAIL_STATUS_NOT_ENABLE);
        }
    }

    /**
     * @param levelRightsCode 权益限制编码
     * @param userId          用户 ID
     */
    @Override
    public AdminUserLevelLimitRespVO validateLevelRightsLimit(String levelRightsCode, Long userId) {
        AdminUserLevelLimitRespVO adminUserLevelLimitRespVO = new AdminUserLevelLimitRespVO();
        // 获取用户等级信息
        List<AdminUserLevelDetailRespVO> levelList = getLevelList(userId);

        AdminUserLevelConfigDO levelConfigDO = levelConfigService.getLevelConfig(levelList.get(0).getLevelId());
        // 获取用户当前权益最大的值
        LevelConfigDTO levelConfigDTO = levelConfigDO.getLevelConfig();

        LevelRightsLimitEnums value = Optional.ofNullable(LevelRightsLimitEnums.getByRedisKey(levelRightsCode))
                .orElseThrow(() -> exception(USER_RIGHTS_LIMIT_USE_TYPE_NO_FOUND));

        Integer data = (Integer) value.getExtractor().apply(levelConfigDTO);

        //  如果不做限制
        if (data == -1) {
            adminUserLevelLimitRespVO.setPass(true);
            adminUserLevelLimitRespVO.setUsedCount(-1);
            return adminUserLevelLimitRespVO;
        }
        String redisKey = userLevelConfigLimitRedisDAO.buildRedisKey(value.getRedisKey(), userId);

        Integer result = userLevelConfigLimitRedisDAO.get(redisKey);

        //  如果第一次访问
        if (result == 0 || data > result) {
            userLevelConfigLimitRedisDAO.increment(redisKey);
            adminUserLevelLimitRespVO.setPass(true);
            adminUserLevelLimitRespVO.setUsedCount(result + 1);
            return adminUserLevelLimitRespVO;
        }
        adminUserLevelLimitRespVO.setPass(false);
        adminUserLevelLimitRespVO.setUsedCount(result);
        return adminUserLevelLimitRespVO;

    }

    /**
     * @param levelRightsCode 权益限制编码
     * @param userId          用户ID
     * @return AdminUserLevelLimitUsedRespVO
     */
    @Override
    public AdminUserLevelLimitUsedRespVO getLevelRightsLimitCount(String levelRightsCode, Long userId) {
        AdminUserLevelLimitUsedRespVO adminUserLevelLimitUsedRespVO = new AdminUserLevelLimitUsedRespVO();

        // 获取用户等级信息
        List<AdminUserLevelDetailRespVO> levelList = getLevelList(userId);

        AdminUserLevelConfigDO levelConfigDO = levelConfigService.getLevelConfig(levelList.get(0).getLevelId());
        // 获取用户当前权益最大的值
        LevelConfigDTO levelConfigDTO = levelConfigDO.getLevelConfig();

        LevelRightsLimitEnums value = Optional.ofNullable(LevelRightsLimitEnums.getByRedisKey(levelRightsCode))
                .orElseThrow(() -> exception(USER_RIGHTS_LIMIT_USE_TYPE_NO_FOUND));

        Integer data = (Integer) value.getExtractor().apply(levelConfigDTO);

        String redisKey = userLevelConfigLimitRedisDAO.buildRedisKey(value.getRedisKey(), userId);

        Integer result = userLevelConfigLimitRedisDAO.get(redisKey);

        adminUserLevelLimitUsedRespVO.setTotal(data);
        adminUserLevelLimitUsedRespVO.setUsedCount(result);
        return adminUserLevelLimitUsedRespVO;
    }

    /**
     * 【系统】验证用户等级和用户角色是否对应
     *
     * @param userId 用户编号（可以为空）
     */
    @Override
    public void validateLevelAndRole(Long userId) {
        List<AdminUserLevelDO> adminUserLevelDOS;
        if (Objects.nonNull(userId)) {
            adminUserLevelDOS = adminUserLevelMapper.getValidAdminUserLevels(userId, null, LocalDateTime.now());
        } else {
            adminUserLevelDOS = adminUserLevelMapper.getValidAdminUserLevels(null, null, LocalDateTime.now());
        }
        Map<Long, List<AdminUserLevelDO>> LevelGroups = adminUserLevelDOS.stream().collect(Collectors.groupingBy(AdminUserLevelDO::getUserId));

        LevelGroups.forEach((user, userLevels) -> {
                    // 获取用户角色组
                    Set<Long> userRoleIdListByUserIdFromCache = permissionService.getUserRoleIdListByUserIdFromCache(user);
                    // 获取用户
                    List<Long> levelConfigIds = userLevels.stream().map(AdminUserLevelDO::getLevelId).collect(Collectors.toList());

                    List<AdminUserLevelConfigDO> levelConfigDOList = levelConfigService.getLevelList(levelConfigIds);

                    Set<Long> userRoles = levelConfigDOList.stream().map(AdminUserLevelConfigDO::getRoleId).collect(Collectors.toSet());
                    if (!userRoleIdListByUserIdFromCache.equals(userRoles)) {
                        // 添加预警数据
                    }
                }
        );
        // 发送通知

        HashMap<String, Object> templateParams = MapUtil.newHashMap();
        templateParams.put("warn_name", 1);
        templateParams.put("warn_type", 1);
        templateParams.put("err_type", 1);
        templateParams.put("data_from", 1);
        templateParams.put("data_code", 1);
        templateParams.put("data_code", 1);
        templateParams.put("data_code", 1);
        templateParams.put("2", 2);
        templateParams.put("3", "3");

        smsSendApi.sendSingleSmsToAdmin(
                new SmsSendSingleToUserReqDTO()
                        .setUserId(1L).setMobile("17835411844")
                        .setTemplateCode("DING_TALK_PAY_NOTIFY_02")
                        .setTemplateParams(templateParams));
    }

    /**
     * 获取最后一条生效记录
     *
     * @param userId  用户编号
     * @param levelId 等级编号
     * @return 最后一条有效记录
     */
    private AdminUserLevelDO findLatestExpirationByLevel(Long userId, Long levelId) {
        // 1.0 根据会员配置等级 获取会员配置信息
        List<AdminUserLevelDO> adminUserLevelDOS = adminUserLevelMapper.getValidAdminUserLevels(userId, Collections.singletonList(levelId), LocalDateTime.now());
        if (CollUtil.isEmpty(adminUserLevelDOS)) {
            return null;
        }
        return adminUserLevelDOS.stream().sorted(Comparator.comparing(AdminUserLevelDO::getValidEndTime, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList()).get(0);
    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private AdminUserLevelServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }


    @Transactional(rollbackFor = Exception.class)
    public void buildUserRole(Long userId, Long incrRoleId, Long decrRoleCode) {
        // 获取当前用户角色
        Set<Long> userRoles = permissionService.getUserRoleIdListByUserId(userId);

        if (decrRoleCode != null && userRoles.contains(decrRoleCode)) {
            userRoles.remove(decrRoleCode);
        }
        if (incrRoleId != null) {
            userRoles.add(incrRoleId);
        }

        // 重新设置用户角色
        permissionService.assignUserRole(userId, userRoles);
    }


}
