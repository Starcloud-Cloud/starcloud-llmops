package com.starcloud.ops.business.user.service.rights;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsCommonDTO;
import com.starcloud.ops.business.user.api.rights.dto.TimesRangeDTO;
import com.starcloud.ops.business.user.api.rights.dto.UserRightsBasicDTO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsCollectRespVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsPageReqVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.NotifyExpiringRightsRespVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.dal.mysql.rights.AdminUserRightsMapper;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsStatusEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
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
    public PageResult<AdminUserRightsDO> getRightsPage(Long userId, PageParam pageVO) {
        return adminUserRightsMapper.selectPage(userId, pageVO);
    }

    /**
     * 获取权益数据汇总
     *
     * @param userId 用户编号
     * @return
     */
    @Override
    public List<AdminUserRightsCollectRespVO> getRightsCollect(Long userId) {

        List<AdminUserRightsDO> validRightsList = getValidAndCountableRightsList(userId, null);
        int sumMagicBean = 0;
        int sumMagicImage = 0;
        int sumMagicBeanInit = 0;
        int sumMagicImageInit = 0;
        if (!validRightsList.isEmpty()) {
            sumMagicBean = validRightsList.stream().mapToInt(AdminUserRightsDO::getMagicBean).sum();
            sumMagicImage = validRightsList.stream().mapToInt(AdminUserRightsDO::getMagicImage).sum();

            sumMagicBeanInit = validRightsList.stream().mapToInt(AdminUserRightsDO::getMagicBeanInit).sum();
            sumMagicImageInit = validRightsList.stream().mapToInt(AdminUserRightsDO::getMagicImageInit).sum();
        }

        List<AdminUserRightsCollectRespVO> rightsCollectRespVOS = new ArrayList<>();

        rightsCollectRespVOS.add(new AdminUserRightsCollectRespVO(AdminUserRightsTypeEnum.MAGIC_BEAN.getName(), AdminUserRightsTypeEnum.MAGIC_BEAN.name(), sumMagicBeanInit,
                sumMagicBeanInit - sumMagicBean, sumMagicBean, 0));
        rightsCollectRespVOS.add(new AdminUserRightsCollectRespVO(AdminUserRightsTypeEnum.MAGIC_IMAGE.getName(), AdminUserRightsTypeEnum.MAGIC_IMAGE.name(), sumMagicImageInit,
                sumMagicImageInit - sumMagicImage, sumMagicImage, 0));

        return rightsCollectRespVOS;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRights(Long userId, Integer magicBean, Integer magicImage, Integer timeNums, Integer timeRange, AdminUserRightsBizTypeEnum bizType, String bizId, Long LevelId) {

        if (magicBean == 0 || magicImage == 0) {
            return;
        }

        if (bizType.isSystem()) {
            timeNums = 1;
            timeRange = TimeRangeTypeEnum.MONTH.getType();
        }

        LocalDateTime startTime = LocalDateTimeUtil.now();
        LocalDateTime endTime;
        // 判断当前是否存在相同等级配置
        if (Objects.nonNull(LevelId)) {
            AdminUserRightsDO latestExpirationByLevel = adminUserRightsMapper.findLatestExpirationByLevel(userId, LevelId);
            if (latestExpirationByLevel != null) {
                startTime = latestExpirationByLevel.getValidEndTime();
            }
        }
        endTime = getSpecificTime(startTime, timeNums, timeRange);

        // 1. 增加权益记录
        AdminUserRightsDO record = new AdminUserRightsDO()
                .setUserId(userId)
                .setBizId(bizId)
                .setBizType(bizType.getType())
                .setTitle(bizType.getName())
                .setDescription(StrUtil.format(bizType.getDescription(), magicBean, magicImage))
                .setMagicBean(magicBean)
                .setMagicImage(magicImage)
                .setMagicBeanInit(magicBean)
                .setMagicImageInit(magicImage)
                .setUserLevelId(LevelId)
                .setValidStartTime(startTime)
                .setValidEndTime(endTime)
                .setStatus(AdminUserRightsStatusEnum.NORMAL.getType());

        if (getLoginUserId() == null) {
            record.setCreator(String.valueOf(userId));
            record.setUpdater(String.valueOf(userId));
        }

        adminUserRightsMapper.insert(record);

        if (magicBean > 0) {
            adminUserRightsRecordService.createRightsRecord(userId, null, null, magicBean, AdminUserRightsTypeEnum.MAGIC_BEAN, bizType.getType() + 50, String.valueOf(record.getId()), String.valueOf(record.getId()));
        }
        if (magicImage > 0) {
            adminUserRightsRecordService.createRightsRecord(userId, null, null, magicImage, AdminUserRightsTypeEnum.MAGIC_IMAGE, bizType.getType() + 50, String.valueOf(record.getId()), String.valueOf(record.getId()));
        }


    }

    /**
     * 创建用户权益记录
     *
     * @param rightsDTO 权益配置
     * @param bizType   业务类型
     * @param bizId     业务编号
     */
    @Override
    public void createRights(AdminUserRightsCommonDTO rightsDTO, Long userId, AdminUserRightsBizTypeEnum bizType, String bizId) {
        // 权益判断
        if (Objects.isNull(rightsDTO.getRightsBasicDTO())) {
            log.warn("权益添加失败，权益配置不存在无法添加，当前用户 ID{},业务 ID 为{},业务类型为{}, 权益数据为{}", userId, bizId, bizType, rightsDTO);
            return;
        }

        // 权益数量判断
        UserRightsBasicDTO rightsBasicDTO = rightsDTO.getRightsBasicDTO();
        if (rightsBasicDTO.getMagicBean() == 0 && rightsBasicDTO.getMagicImage() == 0 && rightsBasicDTO.getMatrixBean() == 0) {
            log.warn("权益添加失败，权益数量为0无法添加，当前用户 ID{},业务 ID 为{},业务类型为{}, 权益数据为{}", userId, bizId, bizType, rightsDTO);
            return;
        }

        // 根据业务类型设置权益有效期
        TimesRangeDTO timesRange = rightsBasicDTO.getTimesRange();
        if (bizType.isSystem()) {
            timesRange.setTimeNums(1);
            timesRange.setTimeRange(TimeRangeTypeEnum.MONTH.getType());
        }

        LocalDateTime startTime = LocalDateTimeUtil.now();
        LocalDateTime endTime;
        // 判断当前是否存在相同等级配置
        if (Objects.nonNull(rightsDTO.getLevelBasicDTO()) && Objects.nonNull(rightsDTO.getLevelBasicDTO().getLevelId())) {
            AdminUserRightsDO latestExpirationByLevel = adminUserRightsMapper.findLatestExpirationByLevel(userId, rightsDTO.getLevelBasicDTO().getLevelId());
            if (latestExpirationByLevel != null) {
                startTime = latestExpirationByLevel.getValidEndTime();
            }
        }
        endTime = getSpecificTime(startTime, rightsDTO.getLevelBasicDTO().getTimesRange().getTimeNums(), rightsDTO.getLevelBasicDTO().getTimesRange().getTimeRange());

        // FIXME 添加数据字段
        // 1. 增加权益记录
        AdminUserRightsDO record = new AdminUserRightsDO()
                .setUserId(userId)
                .setBizId(bizId)
                .setBizType(bizType.getType())
                .setTitle(bizType.getName())
                .setDescription(StrUtil.format(bizType.getDescription(), rightsBasicDTO.getMagicBean(), rightsBasicDTO.getMagicImage()))
                .setMagicBean(rightsBasicDTO.getMagicBean())
                .setMagicImage(rightsBasicDTO.getMagicImage())
                .setMagicBeanInit(rightsBasicDTO.getMagicBean())
                .setMagicImageInit(rightsBasicDTO.getMagicImage())
                .setUserLevelId(Objects.isNull(rightsDTO.getLevelBasicDTO().getLevelId()) ? null : rightsDTO.getLevelBasicDTO().getLevelId())
                .setValidStartTime(startTime)
                .setValidEndTime(endTime)
                .setStatus(AdminUserRightsStatusEnum.NORMAL.getType());

        if (getLoginUserId() == null) {
            record.setCreator(String.valueOf(userId));
            record.setUpdater(String.valueOf(userId));
        }

        adminUserRightsMapper.insert(record);

        if (rightsBasicDTO.getMagicBean() > 0) {
            adminUserRightsRecordService.createRightsRecord(userId, null, null, rightsBasicDTO.getMagicBean(), AdminUserRightsTypeEnum.MAGIC_BEAN, bizType.getType() + 50, String.valueOf(record.getId()), String.valueOf(record.getId()));
        }

        if (rightsBasicDTO.getMagicImage() > 0) {
            adminUserRightsRecordService.createRightsRecord(userId, null, null, rightsBasicDTO.getMagicImage(), AdminUserRightsTypeEnum.MAGIC_IMAGE, bizType.getType() + 50, String.valueOf(record.getId()), String.valueOf(record.getId()));
        }
        // FIXME 添加矩阵点


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

        LocalDateTime today = LocalDateTime.now();
        // LocalDateTime nextWeek = today.plusDays(7);
        // 获取有效的魔法豆
        List<AdminUserRightsDO> validRightsList = getValidAndCountableRightsList(userId, AdminUserRightsTypeEnum.MAGIC_BEAN);
        if (CollUtil.isEmpty(validRightsList)) {
            return notifyExpiringRightsRespVO;
        }

        // 计算7 天内过期的魔法豆数量
        int sumMagicBean = validRightsList.stream().mapToInt(AdminUserRightsDO::getMagicBean).sum();

        notifyExpiringRightsRespVO.setName(AdminUserRightsTypeEnum.MAGIC_BEAN.getName());
        notifyExpiringRightsRespVO.setRightsType(AdminUserRightsTypeEnum.MAGIC_BEAN.name());
        notifyExpiringRightsRespVO.setIsNotify(sumMagicBean <= 10);
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
     * @param rightsDO
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
     * 获取权益数大于 0 且有效的权益列表
     *
     * @param userId     用户 ID
     * @param rightsType 权益类型 如果为空 查询所有有效数据
     * @return
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

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private AdminUserRightsServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
