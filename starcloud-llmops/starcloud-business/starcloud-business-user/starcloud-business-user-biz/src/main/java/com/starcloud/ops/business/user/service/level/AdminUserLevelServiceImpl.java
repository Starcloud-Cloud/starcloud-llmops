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
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import com.starcloud.ops.business.user.api.level.dto.LevelConfigDTO;
import com.starcloud.ops.business.user.api.level.dto.UserLevelBasicDTO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.*;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelConvert;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.dal.mysql.level.AdminUserLevelMapper;
import com.starcloud.ops.business.user.dal.redis.UserLevelConfigLimitRedisDAO;
import com.starcloud.ops.business.user.enums.LevelRightsLimitEnums;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.dept.UserDeptService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private DingTalkNoticeProperties dingTalkNoticeProperties;


    @Resource
    private RoleService roleService;

    @Value("${starcloud-llm.role.code:mofaai_free}")
    private String roleCode;


    @Resource
    private UserDeptService userDeptService;

    @Override
    public AdminUserLevelDO getLevel(Long id) {
        return adminUserLevelMapper.selectById(id);
    }

    /**
     * 通过业务 ID 和业务类型获得会员等级记录明细
     *
     * @param bizType 业务类型
     * @param bizId   业务编号
     * @param userId  用户编号
     * @return 会员等级记录
     */
    @Override
    public AdminUserLevelDO getRecordByBiz(Integer bizType, Long bizId, Long userId) {
        return adminUserLevelMapper.selectOne(Wrappers.lambdaQuery(AdminUserLevelDO.class)
                .eq(AdminUserLevelDO::getBizType, bizType)
                .eq(AdminUserLevelDO::getBizId, bizId)
                .eq(AdminUserLevelDO::getUserId, userId));
    }

    @Override
    public PageResult<AdminUserLevelDO> getLevelPage(AdminUserLevelPageReqVO pageReqVO) {
        return adminUserLevelMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserLevelDO createLevelRecord(AdminUserLevelCreateReqVO createReqVO) {
        log.info("【开始添加用户等级，当前数据为[{}]】", createReqVO);
        if (Objects.isNull(createReqVO)) {
            log.info("【添加用户等级失败，当前数据为空直接跳出添加步骤");
            return null;
        }
        // 1.0 根据会员配置等级 获取会员配置信息
        AdminUserLevelConfigDO levelConfig = levelConfigService.getLevelConfig(createReqVO.getLevelId());
        if (levelConfig == null) {
            throw exception(LEVEL_NOT_EXISTS);
        }
        // 设置开始时间
        LocalDateTime startTime = buildValidTime(createReqVO.getUserId(), Optional.ofNullable(createReqVO.getLevelId()));
        // 设置结束时间
        LocalDateTime endTime = getPlusTimeByRange(createReqVO.getTimeRange(), createReqVO.getTimeNums(), startTime);

        AdminUserLevelDO adminUserLevelDO = AdminUserLevelConvert.INSTANCE.convert01(createReqVO, levelConfig.getName(), startTime, endTime);

        adminUserLevelDO.setCreator(String.valueOf(createReqVO.getUserId()));
        adminUserLevelDO.setUpdater(String.valueOf(createReqVO.getUserId()));

        adminUserLevelDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        adminUserLevelDO.setDescription(StrUtil.format(AdminUserRightsBizTypeEnum.getByType(adminUserLevelDO.getBizType()).getDescription(), levelConfig.getName()));
        // 3.0 添加会员等级记录
        adminUserLevelMapper.insert(adminUserLevelDO);

        // 设置等级中绑定的角色
        getSelf().buildUserRole(adminUserLevelDO.getUserId(), levelConfig.getRoleId(), null);

        return adminUserLevelDO;
    }

    /**
     * 新增用户等级
     *
     * @param rightsAndLevelCommonDTO 统一权益 DTO
     * @param userId                  用户编号
     * @param bizType                 业务类型
     * @param bizId                   业务 编号
     * @return AdminUserLevelDO
     */
    @Override
    public AdminUserLevelDO createLevelRecord(AdminUserRightsAndLevelCommonDTO rightsAndLevelCommonDTO, Long userId, Integer bizType, String bizId, int orderNums) {

        log.info("【开始添加用户等级，当前用户{},业务类型为{} ,业务编号为 {}数据为[{}]】", userId, bizType, bizId, rightsAndLevelCommonDTO);

        if (Objects.isNull(rightsAndLevelCommonDTO) || Objects.isNull(rightsAndLevelCommonDTO.getLevelBasicDTO())) {
            throw exception(LEVEL_NOT_EXISTS);
        }

        UserLevelBasicDTO levelBasicDTO = rightsAndLevelCommonDTO.getLevelBasicDTO();
        // 是否添加会员等级记录
        if (!levelBasicDTO.getOperateDTO().getIsAdd()) {
            log.info("【当前配置无需添加用户等级，跳出添加步骤");
            return null;
        }


        // 1.0 根据会员配置等级 获取会员配置信息
        AdminUserLevelConfigDO levelConfigDO = levelConfigService.getLevelConfig(levelBasicDTO.getLevelId());
        if (levelConfigDO == null) {
            throw exception(LEVEL_NOT_EXISTS);
        }
        LocalDateTime startTime;
        // 判断是否需要叠加时间
        if (levelBasicDTO.getOperateDTO().getIsSuperposition()) {
            // 设置开始时间
            startTime = buildValidTime(userId, Optional.ofNullable(levelBasicDTO.getLevelId()));
        } else {
            startTime = LocalDateTime.now();
        }

        // 设置结束时间
        LocalDateTime endTime = getPlusTimeByRange(levelBasicDTO.getTimesRange().getRange(), levelBasicDTO.getTimesRange().getNums(), startTime);
        levelConfigDO.getLevelConfig().setUsableTeams(orderNums);

        AdminUserLevelDO adminUserLevelDO = AdminUserLevelConvert.INSTANCE.convert01(userId, bizId, bizType, levelBasicDTO.getLevelId(), levelConfigDO.getName(), StrUtil.format(AdminUserRightsBizTypeEnum.getByType(bizType).getDescription(), levelConfigDO.getName()), startTime, endTime);

        adminUserLevelDO.setCreator(String.valueOf(userId));
        adminUserLevelDO.setUpdater(String.valueOf(userId));


        // 3.0 添加会员等级记录
        adminUserLevelMapper.insert(adminUserLevelDO);

        // 设置等级中绑定的角色
        getSelf().buildUserRole(adminUserLevelDO.getUserId(), levelConfigDO.getRoleId(), null);
        log.info("【用户等级添加成功，当前用户{},业务类型为{} ,业务编号为 {}数据为[{}]】", userId, bizType, bizId, rightsAndLevelCommonDTO);
        return adminUserLevelDO;


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
        AdminUserLevelCreateReqVO createReqVO = getAdminUserLevelCreateReqVO(userId, levelConfigDO);
        createLevelRecord(createReqVO);

    }

    private @NonNull AdminUserLevelCreateReqVO getAdminUserLevelCreateReqVO(Long userId, AdminUserLevelConfigDO levelConfigDO) {
        AdminUserLevelCreateReqVO createReqVO = new AdminUserLevelCreateReqVO();
        createReqVO.setUserId(userId);
        createReqVO.setLevelId(levelConfigDO.getId());

        createReqVO.setBizId(String.valueOf(userId));
        createReqVO.setBizType(AdminUserRightsBizTypeEnum.REGISTER.getType());

        // 默认免费版 时间为 99 年
        createReqVO.setTimeNums(99);
        createReqVO.setTimeRange(TimeRangeTypeEnum.YEAR.getType());

        createReqVO.setDescription(String.format(AdminUserRightsBizTypeEnum.REGISTER.getDescription(), userId));
        return createReqVO;
    }

    /**
     * 获取会员下有效的等级列表
     *
     * @param userId 用户 ID
     */
    @Override
    public List<AdminUserLevelDetailRespVO> getLevelList(Long userId) {
        List<AdminUserLevelDO> adminUserLevelDOS = adminUserLevelMapper.getValidAdminUserLevels(userId, null, LocalDateTime.now());
        List<AdminUserLevelDetailRespVO> bean = BeanUtil.copyToList(adminUserLevelDOS, AdminUserLevelDetailRespVO.class);
        return bean.stream().sorted(Comparator.comparing(AdminUserLevelDetailRespVO::getSort).reversed()).collect(Collectors.toList());

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
        List<AdminUserLevelDO> nextWeekExpiringLevel = validLevelList.stream().filter(level -> level.getValidEndTime().isBefore(ThreeDaysLater) && level.getValidEndTime().isAfter(today)).sorted(Comparator.comparing(AdminUserLevelDO::getValidEndTime).reversed()).collect(Collectors.toList());

        // 获取大于 3 天的用户等级
        List<AdminUserLevelDO> noExpiringLevelDOS = validLevelList.stream().filter(level -> !level.getValidEndTime().isAfter(today) || !level.getValidEndTime().isBefore(ThreeDaysLater)).sorted(Comparator.comparing(AdminUserLevelDO::getValidEndTime).reversed()).collect(Collectors.toList());

        nextWeekExpiringLevel.removeIf(level -> noExpiringLevelDOS.stream().anyMatch(noExpiringLevel -> noExpiringLevel.getLevelId().equals(level.getLevelId())));

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
            createReqVO.setBizType(AdminUserRightsBizTypeEnum.REGISTER.getType());
            // 默认免费版 时间为 99 年
            createReqVO.setTimeNums(99);
            createReqVO.setTimeRange(TimeRangeTypeEnum.YEAR.getType());

            createReqVO.setDescription(String.format(AdminUserRightsBizTypeEnum.REGISTER.getDescription(), adminUserDO.getId()));
            getSelf().createLevelRecord(createReqVO);
        }


    }

    /**
     * @return 过期数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int expireLevel() {
        // 1. 查询过期的用户等级数据
        List<AdminUserLevelDO> levelDOS = adminUserLevelMapper.getUserLevelsNearExpiry(CommonStatusEnum.ENABLE.getStatus(), LocalDateTime.now());
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
            List<Long> configIds = levelConfigDOS.stream().map(AdminUserLevelConfigDO::getId).distinct().collect(Collectors.toList());

            if (CollUtil.isEmpty(adminUserLevelMapper.getValidAdminUserLevels(levelDO.getUserId(), configIds, LocalDateTime.now()))) {
                // 移除过期等级中绑定的角色
                getSelf().buildUserRole(levelDO.getUserId(), null, levelConfig.getRoleId());
            }
        }

        // 更新 AdminUserLevelDO 状态为已关闭
        int updateCount = adminUserLevelMapper.updateByIdAndStatus(levelDO.getId(), levelDO.getStatus(), new AdminUserLevelDO().setStatus(CommonStatusEnum.DISABLE.getStatus()));
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

        LevelRightsLimitEnums value = Optional.ofNullable(LevelRightsLimitEnums.getByRedisKey(levelRightsCode)).orElseThrow(() -> exception(USER_RIGHTS_LIMIT_USE_TYPE_NO_FOUND));

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

        LevelRightsLimitEnums value = Optional.ofNullable(LevelRightsLimitEnums.getByRedisKey(levelRightsCode)).orElseThrow(() -> exception(USER_RIGHTS_LIMIT_USE_TYPE_NO_FOUND));

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
        // 根据用户进行分组
        Map<Long, List<AdminUserLevelDO>> LevelGroups = adminUserLevelDOS.stream().collect(Collectors.groupingBy(AdminUserLevelDO::getUserId));

        ArrayList<Long> errUsers = new ArrayList<>();
        LevelGroups.forEach((user, userLevels) -> {
            // 获取用户角色组
            Set<Long> userRoleIdListByUserIdFromCache = permissionService.getUserRoleIdListByUserIdFromCache(user);
            // 获取用户
            List<Long> levelConfigIds = userLevels.stream().map(AdminUserLevelDO::getLevelId).collect(Collectors.toList());

            List<AdminUserLevelConfigDO> levelConfigDOList = levelConfigService.getLevelList(levelConfigIds);

            Set<Long> userRoles = levelConfigDOList.stream().map(AdminUserLevelConfigDO::getRoleId).collect(Collectors.toSet());

            if (!userRoleIdListByUserIdFromCache.equals(userRoles)) {
                errUsers.add(user);
            }
        });
        if (!errUsers.isEmpty()) {

            HashMap<String, Object> templateParams = MapUtil.newHashMap();
            templateParams.put("warn_name", "用户等级异常");
            templateParams.put("data", errUsers.toString());

            smsSendApi.sendSingleSmsToAdmin(new SmsSendSingleToUserReqDTO().setUserId(2L).setMobile("17835411844").setTemplateParams(templateParams).setTemplateCode("LEVEL_DATA_ROLE_ERROR"));
        }
    }

    /**
     * @param adminUserLevelDO  用户等级 DO
     * @param adminUserRightsDO 用户权益 DO
     */
    @Override
    public Boolean checkLevelAndRights(AdminUserLevelDO adminUserLevelDO, AdminUserRightsDO adminUserRightsDO) {
        if (Objects.isNull(adminUserLevelDO) || Objects.isNull(adminUserRightsDO)) return true;

        long initTimeBetween = 10L;
        // 检验
        long startTimeBetween = LocalDateTimeUtil.between(adminUserLevelDO.getValidStartTime(), adminUserRightsDO.getValidStartTime(), ChronoUnit.SECONDS);

        long endTimeBetween = LocalDateTimeUtil.between(adminUserLevelDO.getValidEndTime(), adminUserRightsDO.getValidEndTime(), ChronoUnit.SECONDS);

        return Math.abs(startTimeBetween) < initTimeBetween && Math.abs(endTimeBetween) < initTimeBetween;
    }

    /**
     * 获取团队的用户等级
     *
     * @param userId 用户编号
     * @return List<AdminUserLevelDetailRespVO>
     */
    @Override
    public List<AdminUserLevelDetailRespVO> getGroupLevelList(Long userId) {
        Long deptRightsUserId = getDeptRightsUserId(userId);

        if (userId.equals(deptRightsUserId)) {
            return null;
        }

        return getSelf().getLevelList(deptRightsUserId);
    }


    /**
     * 设置有效开始时间
     *
     * @param userId  用户编号
     * @param levelId 用户等级编号
     * @return 有效开始时间
     */
    private LocalDateTime buildValidTime(Long userId, Optional<Long> levelId) {
        LocalDateTime startTime = LocalDateTime.now();

        if (levelId.isPresent()) {
            // 尝试在数据库中获取符合条件的用户权限信息，并进行排序选择最晚的失效时间
            List<AdminUserLevelDO> adminUserLevelDOS = adminUserLevelMapper.getValidAdminUserLevels(userId, Collections.singletonList(levelId.get()), startTime);

            // 列表为空时，直接返回当前时间
            if (CollUtil.isEmpty(adminUserLevelDOS)) {
                return startTime;
            }

            // 取时间最大的数据
            return adminUserLevelDOS.stream().max(Comparator.comparing(AdminUserLevelDO::getValidEndTime, Comparator.nullsLast(Comparator.naturalOrder()))) // 直接获取第一个元素，这里假设列表非空，前面已做空检查
                    .map(AdminUserLevelDO::getValidEndTime).orElse(startTime);

        }

        return startTime;
    }


    /**
     * 设置用户角色
     *
     * @param userId       用户编号
     * @param incrRoleId   新增角色编号
     * @param decrRoleCode 减少角色编号
     */
    @Transactional(rollbackFor = Exception.class)
    public void buildUserRole(Long userId, Long incrRoleId, Long decrRoleCode) {
        // 获取当前用户角色
        Set<Long> userRoles = permissionService.getUserRoleIdListByUserId(userId);

        if (userRoles == null) {
            // 提前返回，处理空值的情况
            return;
        }

        if (decrRoleCode != null) {
            userRoles.removeIf(decrRoleCode::equals);
        }
        if (incrRoleId != null) {
            userRoles.add(incrRoleId);
        }

        // 重新设置用户角色
        permissionService.assignUserRole(userId, userRoles);
    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private AdminUserLevelServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

        /*
      获取应该创作权益的用户   返回部门超级管理员id
      1，获取当前用户的部门
      2，判断是否是部门管理员
      1）是部门管理员，返回
      2）不是部门管理员，优先获取部门管理员。判断管理员有无剩余点数
      3，返回有剩余点的用户ID（管理员或当前用户）
     */

    /**
     * 这里关闭数据权限，主要是后面的 SQL查询会带上 kstry 线程中的其他正常用户的上下文，导致跟 powerjob 执行应用时候导致用户上下文冲突
     * 所以这里直接 关闭数据权限，这样下面的 关于权益的扣点 已经不需要用户上下文了，单ruiyi 本地比如SQL update会继续获取，所以后续的方法最好直接指定字段创作DB。
     */
    protected Long getDeptRightsUserId(Long currentUserId) {
        return userDeptService.selectSuperAdminId(currentUserId).getUserId();
    }

}
