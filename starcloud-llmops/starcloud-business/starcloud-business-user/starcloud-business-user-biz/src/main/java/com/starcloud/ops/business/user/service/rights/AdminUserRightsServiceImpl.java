package com.starcloud.ops.business.user.service.rights;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.user.api.rights.dto.*;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.*;
import com.starcloud.ops.business.user.convert.rights.AdminUserRightsConvert;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.dal.mysql.rights.AdminUserRightsMapper;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsStatusEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.business.user.service.dept.UserDeptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum.getPlusTimeByRange;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;


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

    @Resource
    private AdminUserRightsRecordService adminUserRightsRecordService;

    @Resource
    private UserDeptService userDeptService;

    private static final int BIZ_TYPE_OFFSET = 50;

    @Override
    public PageResult<AdminUserRightsDO> getRightsPage(AdminUserRightsPageReqVO pageReqVO) {
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
    public PageResult<AdminUserRightsDO> getRightsPage(Long userId, AppAdminUserRightsPageReqVO pageVO) {
        return adminUserRightsMapper.selectPage(userId, pageVO);
    }

    /**
     * 【会员】获得权益记录分页
     *
     * @param userId 用户编号
     * @param pageVO 分页查询
     * @return 签到记录分页
     */
    @Override
    public PageResult<AppAdminUserRightsRespVO> getRightsPage2(Long userId, AppAdminUserRightsPageReqVO pageVO) {
        PageResult<AdminUserRightsDO> adminUserRightsDOPageResult = adminUserRightsMapper.selectPage(userId, pageVO);
        List<AdminUserRightsDO> rightsDOS = adminUserRightsDOPageResult.getList();

        // 即使列表为空也继续处理
        List<AppAdminUserRightsRespVO> convertedRightsList = new ArrayList<>();

        // 定义权益类型与其对应的getter方法的映射
        Map<AdminUserRightsTypeEnum, Function<AdminUserRightsDO, Integer>> rightsGetterMap = new EnumMap<>(AdminUserRightsTypeEnum.class);
        rightsGetterMap.put(AdminUserRightsTypeEnum.MAGIC_BEAN, rights -> Optional.ofNullable(rights.getMagicBeanInit()).orElse(0));
        rightsGetterMap.put(AdminUserRightsTypeEnum.MAGIC_IMAGE, rights -> Optional.ofNullable(rights.getMagicImageInit()).orElse(0));
        rightsGetterMap.put(AdminUserRightsTypeEnum.MATRIX_BEAN, rights -> Optional.ofNullable(rights.getMatrixBeanInit()).orElse(0));
        rightsGetterMap.put(AdminUserRightsTypeEnum.TEMPLATE, rights -> Optional.ofNullable(rights.getOriginalFixedRights())
                .map(AdminUserRightsDO.OriginalFixedRights::getTemplateNums)
                .orElse(0));

        // 转换每条记录
        for (AdminUserRightsDO rightsDO : rightsDOS) {
            // 构建权益列表
            List<AppAdminUserRightsRespVO.RightsRespVO> rightsRespVOS = Arrays.stream(AdminUserRightsTypeEnum.values())
                    .map(rightsType -> {
                        Function<AdminUserRightsDO, Integer> getter = rightsGetterMap.get(rightsType);
                        if (getter == null) {
                            return null;
                        }

                        // 获取权益数量,即使为0也创建对象
                        Integer num = getter.apply(rightsDO);
                        return new AppAdminUserRightsRespVO.RightsRespVO(
                                rightsType.getName(),
                                rightsType.name(),
                                num
                        );
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 设置权益列表到响应对象
            AppAdminUserRightsRespVO respVO = AdminUserRightsConvert.INSTANCE.convert2(rightsDO);
            respVO.setRightsRespVOS(rightsRespVOS);

            convertedRightsList.add(respVO);
        }

        return new PageResult<>(convertedRightsList, adminUserRightsDOPageResult.getTotal());
    }

    /**
     * 通过业务 ID 和业务类型获取权益数据
     *
     * @param bizType 业务类型
     * @param bizId   业务编号
     * @param userId  用户编号
     * @return 权益数据
     */
    @Override
    public AdminUserRightsDO getRecordByBiz(Integer bizType, Long bizId, Long userId) {
        return adminUserRightsMapper.selectOne(Wrappers.lambdaQuery(AdminUserRightsDO.class)
                .eq(AdminUserRightsDO::getBizType, bizType)
                .eq(AdminUserRightsDO::getBizId, bizId)
                .eq(AdminUserRightsDO::getUserId, userId));
    }

    /**
     * 获取权益数据汇总
     *
     * @param userId 用户编号
     * @return 权益数据汇总
     */
    @Override
    public List<AdminUserRightsCollectRespVO> getGroupRightsCollect(Long userId) {

        Long deptRightsUserId = getDeptRightsUserId(userId);
        if (userId.equals(deptRightsUserId)) {
            return null;
        }
        return getRightsCollect(deptRightsUserId);
    }

    /**
     * 获取权益数据汇总
     *
     * @param userId 用户编号
     */
    @Override
    public List<AdminUserRightsCollectRespVO> getRightsCollect(Long userId) {
        List<AdminUserRightsDO> validRightsList = getValidAndCountableRightsList(userId, null);

        // 移除空列表判断,使用空列表继续处理
        if (validRightsList == null) {
            validRightsList = Collections.emptyList();
        }

        // 使用 final 修饰,确保在 lambda 中可以安全使用
        final List<AdminUserRightsDO> finalValidRightsList = validRightsList;

        // 定义权益类型与其对应的getter方法的映射
        Map<AdminUserRightsTypeEnum, Function<AdminUserRightsDO, Integer>> rightsGetterMap = new EnumMap<>(AdminUserRightsTypeEnum.class);
        rightsGetterMap.put(AdminUserRightsTypeEnum.MAGIC_BEAN, rights -> Optional.ofNullable(rights.getMagicBean()).orElse(0));
        rightsGetterMap.put(AdminUserRightsTypeEnum.MAGIC_IMAGE, rights -> Optional.ofNullable(rights.getMagicImage()).orElse(0));
        rightsGetterMap.put(AdminUserRightsTypeEnum.MATRIX_BEAN, rights -> Optional.ofNullable(rights.getMatrixBean()).orElse(0));
        rightsGetterMap.put(AdminUserRightsTypeEnum.TEMPLATE, rights -> Optional.ofNullable(rights.getDynamicRights())
                .map(AdminUserRightsDO.DynamicRights::getTemplateNums)
                .orElse(0));

        // 定义权益类型与其对应的初始值getter方法的映射
        Map<AdminUserRightsTypeEnum, Function<AdminUserRightsDO, Integer>> rightsInitGetterMap = new EnumMap<>(AdminUserRightsTypeEnum.class);
        rightsInitGetterMap.put(AdminUserRightsTypeEnum.MAGIC_BEAN, rights -> Optional.ofNullable(rights.getMagicBeanInit()).orElse(0));
        rightsInitGetterMap.put(AdminUserRightsTypeEnum.MAGIC_IMAGE, rights -> Optional.ofNullable(rights.getMagicImageInit()).orElse(0));
        rightsInitGetterMap.put(AdminUserRightsTypeEnum.MATRIX_BEAN, rights -> Optional.ofNullable(rights.getMatrixBeanInit()).orElse(0));
        rightsInitGetterMap.put(AdminUserRightsTypeEnum.TEMPLATE, rights -> Optional.ofNullable(rights.getOriginalFixedRights())
                .map(AdminUserRightsDO.OriginalFixedRights::getTemplateNums)
                .orElse(0));

        // 计算每种权益类型的汇总数据,始终返回所有权益类型
        return Arrays.stream(AdminUserRightsTypeEnum.values())
                .map(rightsType -> {
                    final Function<AdminUserRightsDO, Integer> currentGetter =
                            Optional.ofNullable(rightsGetterMap.get(rightsType)).orElse(rights -> 0);
                    final Function<AdminUserRightsDO, Integer> initGetter =
                            Optional.ofNullable(rightsInitGetterMap.get(rightsType)).orElse(rights -> 0);

                    // 对于模板类型,从 OriginalFixedRights 获取总数,从 DynamicRights 获取剩余数
                    if (rightsType == AdminUserRightsTypeEnum.TEMPLATE) {
                        int totalSum = finalValidRightsList.stream()
                                .mapToInt(rights -> Optional.ofNullable(rights.getOriginalFixedRights())
                                        .map(AdminUserRightsDO.OriginalFixedRights::getTemplateNums)
                                        .orElse(0))
                                .sum();

                        int remainingSum = finalValidRightsList.stream()
                                .mapToInt(rights -> Optional.ofNullable(rights.getDynamicRights())
                                        .map(AdminUserRightsDO.DynamicRights::getTemplateNums)
                                        .orElse(0))
                                .sum();

                        return new AdminUserRightsCollectRespVO(
                                rightsType.getName(),
                                rightsType.name(),
                                totalSum,
                                totalSum - remainingSum,  // 已使用数量 = 总数 - 剩余数量
                                remainingSum,  // 剩余数量
                                0
                        );
                    }

                    // 其他权益类型的计算逻辑
                    int currentSum = finalValidRightsList.stream()
                            .mapToInt(currentGetter::apply)
                            .sum();

                    int initSum = finalValidRightsList.stream()
                            .mapToInt(initGetter::apply)
                            .sum();

                    return new AdminUserRightsCollectRespVO(
                            rightsType.getName(),
                            rightsType.name(),
                            initSum,
                            initSum - currentSum,
                            currentSum,
                            0
                    );
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRights(Long userId, Integer magicBean, Integer magicImage, Integer matrixBean, Integer timeNums, Integer timeRange, Integer bizType, String bizId, Long levelId, Integer templates) {

        if (magicBean == 0 || magicImage == 0) {
            return;
        }
        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);
        if (bizTypeEnum.isSystem()) {
            timeNums = 1;
            timeRange = TimeRangeTypeEnum.MONTH.getType();
        }

        // 获取权益开始时间
        LocalDateTime startTime = buildValidTime(userId, Optional.ofNullable(levelId));
        // 设置权益结束时间
        LocalDateTime endTime = getPlusTimeByRange(timeRange, timeNums, startTime);

        // 构建对象
        AdminUserRightsDO record = AdminUserRightsConvert.INSTANCE.convert(userId, bizId, bizType, magicBean, magicImage, matrixBean, startTime, endTime, levelId, templates);

        if (getLoginUserId() == null) {
            record.setCreator(String.valueOf(userId));
            record.setUpdater(String.valueOf(userId));
        }
        // 插入记录
        adminUserRightsMapper.insert(record);
        // 插入明细
        createCommonRightsRecord(record, magicBean, magicImage, matrixBean, templates);
    }

    /**
     * 创建用户权益记录
     *
     * @param addRightsDTO 新增权益 DTO
     */
    @Override
    public void createRights(AddRightsDTO addRightsDTO) {

        // 获取业务类型
        AdminUserRightsBizTypeEnum bizType = AdminUserRightsBizTypeEnum.getByType(addRightsDTO.getBizType());

        if (bizType.isSystem()) {
            addRightsDTO.setTimeNums(1);
            addRightsDTO.setTimeRange(TimeRangeTypeEnum.MONTH.getType());
        }

        // 获取权益开始时间
        LocalDateTime startTime = buildValidTime(addRightsDTO.getUserId(), Optional.ofNullable(addRightsDTO.getLevelId()));
        // 设置权益结束时间
        LocalDateTime endTime = getPlusTimeByRange(addRightsDTO.getTimeRange(), addRightsDTO.getTimeNums(), startTime);

        AdminUserRightsDO record = AdminUserRightsConvert.INSTANCE.convert01(addRightsDTO, addRightsDTO.getBizType(), startTime, endTime);

        if (getLoginUserId() == null) {
            record.setCreator(String.valueOf(addRightsDTO.getUserId()));
            record.setUpdater(String.valueOf(addRightsDTO.getUserId()));
        }

        // 插入记录
        adminUserRightsMapper.insert(record);

        // 插入明细
        createCommonRightsRecord(record, addRightsDTO.getMagicBean(), addRightsDTO.getMagicImage(), addRightsDTO.getMatrixBean(), addRightsDTO.getTemplate());

    }

    /**
     * 创建用户权益记录
     *
     * @param rightsAndLevelCommonDTO 权益配置
     * @param bizType                 业务类型
     * @param bizId                   业务编号
     */
    @Override
    public AdminUserRightsDO createRights(AdminUserRightsAndLevelCommonDTO rightsAndLevelCommonDTO, Long userId, Integer bizType, String bizId) {

        log.info("【添加用户权益，当前用户{},业务类型为{} ,业务编号为 {}数据为[{}]】", userId, bizType, bizId, rightsAndLevelCommonDTO);
        // 权益判断
        if (Objects.isNull(rightsAndLevelCommonDTO.getRightsBasicDTO())) {
            log.warn("权益添加失败，权益配置不存在无法添加，当前用户 ID{},业务 ID 为{},业务类型为{}, 权益数据为{}", userId, bizId, bizType, rightsAndLevelCommonDTO);
            return null;
        }


        // 权益数量判断
        UserRightsBasicDTO rightsBasicDTO = rightsAndLevelCommonDTO.getRightsBasicDTO();
        if (rightsBasicDTO.getMagicBean() == 0 && rightsBasicDTO.getMagicImage() == 0 && rightsBasicDTO.getMatrixBean() == 0 && rightsBasicDTO.getTemplate() == 0) {
            log.warn("权益添加失败，权益数量为0无法添加，当前用户 ID{},业务 ID 为{},业务类型为{}, 权益数据为{}", userId, bizId, bizType, rightsAndLevelCommonDTO);
            return null;
        }

        // 是否添加会员等级记录
        if (!rightsBasicDTO.getOperateDTO().getIsAdd()) {
            log.info("【当前配置无需添加用户权益，跳出添加步骤");
            return null;
        }

        // 根据业务类型设置权益有效期
        TimesRangeDTO timesRange = rightsBasicDTO.getTimesRange();

        if (AdminUserRightsBizTypeEnum.getByType(bizType).isSystem()) {
            timesRange.setNums(1);
            timesRange.setRange(TimeRangeTypeEnum.MONTH.getType());
        }

        LocalDateTime startTime;
        // 判断是否需要叠加时间
        if (rightsBasicDTO.getOperateDTO().getIsSuperposition()) {
            // 设置开始时间
            startTime = buildValidTime(userId, Optional.ofNullable(rightsAndLevelCommonDTO.getLevelBasicDTO().getLevelId()));
        } else {
            startTime = LocalDateTime.now();
        }

        // 设置权益结束时间
        LocalDateTime endTime = getPlusTimeByRange(rightsAndLevelCommonDTO.getLevelBasicDTO().getTimesRange().getRange(), rightsAndLevelCommonDTO.getLevelBasicDTO().getTimesRange().getNums(), startTime);

        // 构建对象
        AdminUserRightsDO record = AdminUserRightsConvert.INSTANCE.convert(userId, bizId, bizType, rightsBasicDTO.getMagicBean(), rightsBasicDTO.getMagicImage(), rightsBasicDTO.getMatrixBean(), startTime, endTime,
                Objects.isNull(rightsAndLevelCommonDTO.getLevelBasicDTO().getLevelId()) ? null : rightsAndLevelCommonDTO.getLevelBasicDTO().getLevelId(), rightsBasicDTO.getTemplate());

        if (getLoginUserId() == null) {
            record.setCreator(String.valueOf(userId));
            record.setUpdater(String.valueOf(userId));
        }
        // 插入记录
        adminUserRightsMapper.insert(record);

        // 插入明细
        createCommonRightsRecord(record, rightsBasicDTO.getMagicBean(), rightsBasicDTO.getMagicImage(), rightsBasicDTO.getMatrixBean(), rightsBasicDTO.getTemplate());

        log.info("【添加用户权益成功，当前用户{},业务类型为{} ,业务编号为 {}数据为[{}]】", userId, bizType, bizId, rightsAndLevelCommonDTO);

        return record;
    }

    /**
     * 校验权益是否可供扣除
     *
     * @param userId      用户 ID
     * @param rightsType  权益类型
     * @param rightAmount 权益数量
     */
    @Override
    public Boolean calculateUserRightsEnough(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount) {
        // 获取可用权益列表
        List<AdminUserRightsDO> validRightsList = getValidAndCountableRightsList(userId, rightsType);
        if (validRightsList.isEmpty()) {
            return false;
        }

        int validSum = 0;
        switch (rightsType) {
            case MAGIC_IMAGE:
                validSum = validRightsList.stream().mapToInt(AdminUserRightsDO::getMagicImage).sum();
                break;
            case MAGIC_BEAN:
                validSum = validRightsList.stream().mapToInt(AdminUserRightsDO::getMagicBean).sum();
                break;
            case MATRIX_BEAN:
                validSum = validRightsList.stream().mapToInt(AdminUserRightsDO::getMatrixBean).sum();
                break;
        }
        if (Objects.isNull(rightAmount) || rightAmount == 0) {
            return validSum > 0;
        }
        return validSum >= rightAmount;

    }

    /**
     * @param userId      用户 ID
     * @param rightsType  权益类型
     * @param rightAmount 扣除的权益数
     * @param bizType     业务类型
     * @param bizId       业务编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reduceRights(Long userId, Long teamOwnerId, Long teamId, AdminUserRightsTypeEnum rightsType, Integer rightAmount, AdminUserRightsBizTypeEnum bizType, String bizId) {
        log.info("【开始执行权益扣除操作，当前用户编号为{}，团队所属人编号为{}，团队编号为{}，权益类型为{}，扣除数量为{}，业务类型为{}，业务编号为{}】",
                userId, teamOwnerId, teamId, rightsType.getName(), rightAmount, bizType, bizId);
        Long reduceUserId = Objects.isNull(teamOwnerId) ? userId : teamOwnerId;

        // 获取可用权益列表
        List<AdminUserRightsDO> validRightsList = getValidAndCountableRightsList(reduceUserId, rightsType);
        if (validRightsList.isEmpty()) {
            if (AdminUserRightsTypeEnum.MAGIC_BEAN.getType().equals(rightsType.getType())) {
                throw exception(USER_RIGHTS_BEAN_NOT_ENOUGH);
            }
            if (AdminUserRightsTypeEnum.MAGIC_IMAGE.getType().equals(rightsType.getType())) {
                throw exception(USER_RIGHTS_IMAGE_NOT_ENOUGH);
            }
            if (AdminUserRightsTypeEnum.MATRIX_BEAN.getType().equals(rightsType.getType())) {
                throw exception(USER_RIGHTS_MATRIX_BEAN_NOT_ENOUGH);
            }
            throw exception(USER_RIGHTS_NOT_ENOUGH);

        }


        List<AdminUserRightsDO> deductRightsList = deduct(validRightsList, rightsType, rightAmount);

        // 批量更新数据
        adminUserRightsMapper.updateBatch(deductRightsList, deductRightsList.size());

        // 使用流API提取Id并使用逗号分割
        String deductIds = deductRightsList.stream()
                .map(adminUserRightsDO -> adminUserRightsDO.getId().toString()) // 提取Id
                .collect(Collectors.joining(",")); // 使用逗号连接

        adminUserRightsRecordService.createRightsRecord(userId, teamOwnerId, teamId, rightAmount, rightsType, bizType.getType() + 50, bizId, deductIds);
        log.info("【权益扣除执行结束，扣除成功，权益扣除操作，当前用户编号为{}，权益扣除人用户编号为{}，团队所属人编号为{}，团队编号为{}，权益类型为{}，扣除数量为{}，业务类型为{}，业务编号为{}】",
                userId, reduceUserId, teamOwnerId, teamId, rightsType.getName(), rightAmount, bizType, bizId);
    }

    /**
     * 权益扣减
     *
     * @param reduceRightsDTO 权益扣减 DTO
     */
    @Override
    public void reduceRights(ReduceRightsDTO reduceRightsDTO) {
        Long reduceUserId = Objects.isNull(reduceRightsDTO.getTeamOwnerId()) ? reduceRightsDTO.getUserId() : reduceRightsDTO.getTeamOwnerId();
        AdminUserRightsTypeEnum rightsType = AdminUserRightsTypeEnum.getByType(reduceRightsDTO.getRightType());
        AdminUserRightsBizTypeEnum bizType = AdminUserRightsBizTypeEnum.getByType(reduceRightsDTO.getBizType());

        // 获取可用权益列表
        List<AdminUserRightsDO> validRightsList = getValidAndCountableRightsList(reduceUserId, rightsType);
        if (validRightsList.isEmpty()) {
            if (AdminUserRightsTypeEnum.MAGIC_BEAN.getType().equals(rightsType.getType())) {
                throw exception(USER_RIGHTS_BEAN_NOT_ENOUGH);
            }
            if (AdminUserRightsTypeEnum.MAGIC_IMAGE.getType().equals(rightsType.getType())) {
                throw exception(USER_RIGHTS_IMAGE_NOT_ENOUGH);
            }
            if (AdminUserRightsTypeEnum.MATRIX_BEAN.getType().equals(rightsType.getType())) {
                throw exception(USER_RIGHTS_MATRIX_BEAN_NOT_ENOUGH);
            }
            throw exception(USER_RIGHTS_NOT_ENOUGH);

        }


        List<AdminUserRightsDO> deductRightsList = deduct(validRightsList, rightsType, reduceRightsDTO.getReduceNums());

        // 批量更新数据
        adminUserRightsMapper.updateBatch(deductRightsList, deductRightsList.size());

        // 使用流API提取Id并使用逗号分割
        String deductIds = deductRightsList.stream()
                .map(adminUserRightsDO -> adminUserRightsDO.getId().toString()) // 提取Id
                .collect(Collectors.joining(",")); // 使用逗号连接

        adminUserRightsRecordService.createRightsRecord(reduceRightsDTO.getUserId(), reduceRightsDTO.getTeamOwnerId(), reduceRightsDTO.getTeamId(), reduceRightsDTO.getReduceNums(), rightsType, bizType.getType() + 50, reduceRightsDTO.getBizId(), deductIds);

    }

    /**
     * 权益过期提醒
     *
     * @param userId 用户 ID
     */
    @Override
    public NotifyExpiringRightsRespVO notifyExpiringRights(Long userId) {

        NotifyExpiringRightsRespVO notifyExpiringRightsRespVO = new NotifyExpiringRightsRespVO();
        notifyExpiringRightsRespVO.setIsNotify(false);

        // 获取有效的魔法豆
        List<AdminUserRightsDO> validRightsList = getValidAndCountableRightsList(userId, AdminUserRightsTypeEnum.MAGIC_BEAN);
        if (CollUtil.isEmpty(validRightsList)) {
            return notifyExpiringRightsRespVO;
        }

        // 获取当前租户
        Long tenantId = TenantContextHolder.getTenantId();
        if (Objects.nonNull(tenantId) && tenantId.equals(3L)) {

            // 计算7 天内过期的魔法豆数量
            int sumMatrixBean = validRightsList.stream().mapToInt(AdminUserRightsDO::getMatrixBean).sum();

            notifyExpiringRightsRespVO.setName(AdminUserRightsTypeEnum.MATRIX_BEAN.getName());
            notifyExpiringRightsRespVO.setRightsType(AdminUserRightsTypeEnum.MATRIX_BEAN.name());
            notifyExpiringRightsRespVO.setIsNotify(sumMatrixBean <= 5);
            notifyExpiringRightsRespVO.setExpiredNum(sumMatrixBean);
            return notifyExpiringRightsRespVO;
        }
        // 计算7 天内过期的魔法豆数量
        int sumMagicBean = validRightsList.stream().mapToInt(AdminUserRightsDO::getMagicBean).sum();

        notifyExpiringRightsRespVO.setName(AdminUserRightsTypeEnum.MAGIC_BEAN.getName());
        notifyExpiringRightsRespVO.setRightsType(AdminUserRightsTypeEnum.MAGIC_BEAN.name());
        notifyExpiringRightsRespVO.setIsNotify(sumMagicBean <= 5);
        notifyExpiringRightsRespVO.setExpiredNum(sumMagicBean);

        return notifyExpiringRightsRespVO;
    }


    /**
     *
     */
    @Override
    public Integer expireRights() {

        // 1. 查询过期的权益订单
        List<AdminUserRightsDO> rightsDOS = adminUserRightsMapper.selectListByStatusAndValidTimeLt(
                AdminUserRightsStatusEnum.NORMAL.getType(), LocalDateTime.now());
        if (CollUtil.isEmpty(rightsDOS)) {
            return 0;
        }

        // 2. 遍历执行，逐个取消
        int count = 0;
        for (AdminUserRightsDO rightsDO : rightsDOS) {
            try {
                getSelf().expireRightsBySystem(rightsDO);
                count++;
            } catch (Throwable e) {
                log.error("[expireRightsBySystem][rightsDO({}) 用户权益过期异常]", rightsDO.getId(), e);
            }
        }
        return count;
    }

    /**
     * @param rightsDO 权益 DO
     */
    @Override
    public void expireRightsBySystem(AdminUserRightsDO rightsDO) {
        // 更新 AdminUserRightsDO 状态为过期
        int updateCount = adminUserRightsMapper.updateByIdAndStatus(rightsDO.getId(), rightsDO.getStatus(),
                new AdminUserRightsDO().setStatus(AdminUserRightsStatusEnum.EXPIRE.getType()));
        if (updateCount == 0) {
            throw exception(USER_RIGHTS_EXPIRE_FAIL_STATUS_NOT_ENABLE);
        }
    }

    /**
     * 获取指定类型的剩余数量-有效期内的
     *
     * @param type 权益类型
     */
    @Override
    public Integer getUsedNumsByType(Integer type) {
        return 0;
    }

    /**
     * 获取指定类型的总数量-有效期内的
     *
     * @param rightsType 权益类型
     */
    @Override
    public Integer getEffectiveNumsByType(Long userId, AdminUserRightsTypeEnum rightsType) {
        switch (rightsType) {
            case TEMPLATE:
                return selectCountByTypeAndStatus(userId, AdminUserRightsTypeEnum.TEMPLATE);
            default:
                throw exception(USER_RIGHTS_TYPE_NOT_SUPPORT);
        }

    }

    private Integer selectCountByTypeAndStatus(Long userId, AdminUserRightsTypeEnum rightsType) {
        LocalDateTime now = LocalDateTime.now();
        // 查询条件：当前用户下启用且未过期且权益值大于0的数据
        LambdaQueryWrapper<AdminUserRightsDO> wrapper = Wrappers.lambdaQuery(AdminUserRightsDO.class)
                .eq(AdminUserRightsDO::getStatus, AdminUserRightsStatusEnum.NORMAL.getType())
                // 校验有效期；为避免定时器没跑，实际已经过期
                // 添加这个条件来确保当前时间在有效期内
                .le(AdminUserRightsDO::getValidStartTime, now)
                // 添加这个条件来确保当前时间在有效期内
                .ge(AdminUserRightsDO::getValidEndTime, now)
                .eq(AdminUserRightsDO::getUserId, userId)
                .orderByDesc(AdminUserRightsDO::getValidEndTime);

        if (Objects.nonNull(rightsType)) {
            switch (rightsType) {
                case TEMPLATE:
                    wrapper.isNotNull(AdminUserRightsDO::getDynamicRights);
                    wrapper.orderByAsc(AdminUserRightsDO::getMagicImage);
                    break;
            }
        }

        List<AdminUserRightsDO> adminUserRightsDOS = adminUserRightsMapper.selectList(wrapper);

        return adminUserRightsDOS.stream()
                .mapToInt(adminUserRightsDO -> adminUserRightsDO.getOriginalFixedRights().getTemplateNums())
                .sum();
    }

    /**
     * 获取权益数大于 0 且有效的权益列表
     *
     * @param userId     用户 ID
     * @param rightsType 权益类型 如果为空 查询所有有效数据
     * @return List<AdminUserRightsDO>
     */
    private List<AdminUserRightsDO> getValidAndCountableRightsList(Long userId, AdminUserRightsTypeEnum rightsType) {
        LocalDateTime now = LocalDateTime.now();
        // 查询条件：当前用户下启用且未过期且权益值大于0的数据
        LambdaQueryWrapper<AdminUserRightsDO> wrapper = Wrappers.lambdaQuery(AdminUserRightsDO.class)
                .eq(AdminUserRightsDO::getStatus, AdminUserRightsStatusEnum.NORMAL.getType())
                // 校验有效期；为避免定时器没跑，实际已经过期
                // 添加这个条件来确保当前时间在有效期内
                .le(AdminUserRightsDO::getValidStartTime, now)
                // 添加这个条件来确保当前时间在有效期内
                .ge(AdminUserRightsDO::getValidEndTime, now)
                .eq(AdminUserRightsDO::getUserId, userId)
                .orderByDesc(AdminUserRightsDO::getValidEndTime);
        if (Objects.nonNull(rightsType)) {
            switch (rightsType) {
                case MAGIC_IMAGE:
                    wrapper.gt(AdminUserRightsDO::getMagicImage, 0L);
                    wrapper.orderByAsc(AdminUserRightsDO::getMagicImage);
                    break;
                case MAGIC_BEAN:
                    wrapper.gt(AdminUserRightsDO::getMagicBean, 0L);
                    wrapper.orderByAsc(AdminUserRightsDO::getMagicBean);
                    break;
                case MATRIX_BEAN:
                    wrapper.gt(AdminUserRightsDO::getMatrixBean, 0L);
                    wrapper.orderByAsc(AdminUserRightsDO::getMatrixBean);
                    break;
            }
        }

        return adminUserRightsMapper.selectList(wrapper);

    }

    /**
     * 权益扣除
     *
     * @param validRightsList 有效的权益列表
     * @param amount          扣减数
     */
    private List<AdminUserRightsDO> deduct(List<AdminUserRightsDO> validRightsList, AdminUserRightsTypeEnum rightsType, Integer amount) {

        long remainingAmount = amount;

        Function<AdminUserRightsDO, Integer> getter = null;
        BiConsumer<AdminUserRightsDO, Integer> setter = null;
        switch (rightsType) {
            case MAGIC_IMAGE:
                getter = AdminUserRightsDO::getMagicImage;
                setter = AdminUserRightsDO::setMagicImage;
                break;
            case MAGIC_BEAN:
                getter = AdminUserRightsDO::getMagicBean;
                setter = AdminUserRightsDO::setMagicBean;
                break;
            case MATRIX_BEAN:
                getter = AdminUserRightsDO::getMatrixBean;
                setter = AdminUserRightsDO::setMatrixBean;
                break;
        }

        // 创建用于记录已使用的权益的列表
        List<AdminUserRightsDO> deductRightsList = new ArrayList<>();

        for (AdminUserRightsDO rightsDO : validRightsList) {
            // 获取当前权益已使用数量
            long used = getter.apply(rightsDO);
            // 计算可用数量
            long available = Math.max(0, used - remainingAmount);
            // 计算已扣除数量
            long deducted = used - available;
            // 更新权益的可用数量
            setter.accept(rightsDO, (int) available);
            // 更新剩余需扣除数量
            remainingAmount -= deducted;

            // 如果剩余需扣除数量已为0或负数，退出循环
            if (remainingAmount <= 0) {
                deductRightsList.add(rightsDO); // 将已使用的权益添加到列表中
                break;
            } else {
                deductRightsList.add(rightsDO); // 将已使用的权益添加到列表中
            }
        }

        if (remainingAmount > 0) {
            log.warn("[expendBenefits][权益扣减成功，用户剩余权益不足扣除：用户ID({})｜权益类型({})|剩余数量({})", getLoginUserId(), getter.toString(), remainingAmount);
        }
        return deductRightsList;

    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private AdminUserRightsServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }


    // 共通的创建权益记录方法
    private void createCommonRightsRecord(AdminUserRightsDO record, Integer magicBean, Integer magicImage, Integer matrixBean, Integer template) {
        try {
            // 插入魔法豆明细记录
            if (magicBean > 0) {
                adminUserRightsRecordService.createRightsRecord(record.getUserId(), null, null, magicBean,
                        AdminUserRightsTypeEnum.MAGIC_BEAN, record.getBizType() + BIZ_TYPE_OFFSET,
                        String.valueOf(record.getId()), String.valueOf(record.getId()));
            }
            // 插入魔法图片明细记录
            if (magicImage > 0) {
                adminUserRightsRecordService.createRightsRecord(record.getUserId(), null, null, magicImage,
                        AdminUserRightsTypeEnum.MAGIC_IMAGE, record.getBizType() + BIZ_TYPE_OFFSET,
                        String.valueOf(record.getId()), String.valueOf(record.getId()));
            }
            // 插入矩阵豆明细记录
            if (matrixBean > 0) {
                adminUserRightsRecordService.createRightsRecord(record.getUserId(), null, null, matrixBean,
                        AdminUserRightsTypeEnum.MATRIX_BEAN, record.getBizType() + BIZ_TYPE_OFFSET,
                        String.valueOf(record.getId()), String.valueOf(record.getId()));
            }
            if (Objects.isNull(template)) {
                template = 0;

            }
            // 插入矩阵豆明细记录
            if (template > 0) {
                adminUserRightsRecordService.createRightsRecord(record.getUserId(), null, null, template,
                        AdminUserRightsTypeEnum.TEMPLATE, record.getBizType() + BIZ_TYPE_OFFSET,
                        String.valueOf(record.getId()), String.valueOf(record.getId()));
            }
        } catch (Exception e) {
            // 适当的异常处理逻辑
            log.error("Failed to create rights record for user:{} ", record.getUserId(), e);
        }
    }

    /**
     * @param userId  用户编号
     * @param levelId 用户等级编号
     * @return 有效开始时间
     */
    private LocalDateTime buildValidTime(Long userId, Optional<Long> levelId) {
        LocalDateTime startTime = LocalDateTime.now();

        if (levelId.isPresent()) {
            // 尝试在数据库中获取符合条件的用户权限信息，并进行排序选择最晚的失效时间
            List<AdminUserRightsDO> validAdminUserRights = adminUserRightsMapper.getValidAdminUserRights(userId, levelId.get(), startTime);

            // 列表为空时，直接返回当前时间
            if (CollUtil.isEmpty(validAdminUserRights)) {
                return startTime;
            }

            // 取时间最大的数据
            return validAdminUserRights.stream().max(Comparator.comparing(AdminUserRightsDO::getValidEndTime, Comparator.nullsLast(Comparator.naturalOrder()))) // 直接获取第一个元素，这里假设列表非空，前面已做空检查
                    .map(AdminUserRightsDO::getValidEndTime)
                    .orElse(startTime);

        }

        return startTime;
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
