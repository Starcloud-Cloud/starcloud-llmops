package com.starcloud.ops.business.user.service.signin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.api.rights.dto.AddRightsDTO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AdminUserSignInRecordPageReqVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AppAdminUserSignInRecordSummaryRespVO;
import com.starcloud.ops.business.user.convert.signin.AdminUserSignInRecordConvert;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInConfigDO;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInRecordDO;
import com.starcloud.ops.business.user.dal.mysql.signin.AdminUserSignInRecordMapper;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.SIGN_IN_RECORD_TODAY_EXISTS;

/**
 * 签到记录 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AdminUserSignInRecordServiceImpl implements AdminUserSignInRecordService {

    @Resource
    private AdminUserSignInRecordMapper signInRecordMapper;
    @Resource
    private AdminUserSignInConfigService signInConfigService;
    @Resource
    private AdminUserRightsService adminUserRightsService;

    @Resource
    private AdminUserService adminUserService;

    @Override
    public AppAdminUserSignInRecordSummaryRespVO getSignInRecordSummary(Long userId) {
        // 1. 初始化默认返回信息
        AppAdminUserSignInRecordSummaryRespVO summary = new AppAdminUserSignInRecordSummaryRespVO();
        summary.setTotalDay(0);
        summary.setContinuousDay(0);
        summary.setTodaySignIn(false);

        // 2. 获取用户签到的记录数
        Long signCount = signInRecordMapper.selectCountByUserId(userId);
        if (ObjUtil.equal(signCount, 0L)) {
            return summary;
        }
        summary.setTotalDay(signCount.intValue()); // 设置总签到天数

        // 3. 校验当天是否有签到
        AdminUserSignInRecordDO lastRecord = signInRecordMapper.selectLastRecordByUserId(userId);
        if (lastRecord == null) {
            return summary;
        }
        summary.setTodaySignIn(DateUtils.isToday(lastRecord.getCreateTime()));

        // 4. 校验今天是否签到，没有签到则直接返回
        if (!summary.getTodaySignIn()) {
            return summary;
        }
        // 4.1. 判断连续签到天数
        // TODO @puhui999：连续签到，可以基于 lastRecord 的 day 和当前时间判断呀？按 day 统计连续签到天数可能不准确
        //      1. day 只是记录第几天签到的有可能不连续，比如第一次签到是周一，第二次签到是周三这样 lastRecord 的 day 为 2 但是并不是连续的两天
        //      2. day 超出签到规则的最大天数会重置到从第一天开始签到（我理解为开始下一轮，类似一周签到七天七天结束下周又从周一开始签到）
        // 1. 回复：周三签到，day 要归 1 呀。连续签到哈；
        List<AdminUserSignInRecordDO> signInRecords = signInRecordMapper.selectListByUserId(userId);
        signInRecords.sort(Comparator.comparing(AdminUserSignInRecordDO::getCreateTime).reversed()); // 根据签到时间倒序
        summary.setContinuousDay(calculateConsecutiveDays(signInRecords));
        return summary;
    }

    /**
     * 计算连续签到天数
     *
     * @param signInRecords 签到记录列表
     * @return int 连续签到天数
     */
    public int calculateConsecutiveDays(List<AdminUserSignInRecordDO> signInRecords) {
        int consecutiveDays = 1;  // 初始连续天数为1
        LocalDate previousDate = null;

        for (AdminUserSignInRecordDO record : signInRecords) {
            LocalDate currentDate = record.getCreateTime().toLocalDate();

            if (previousDate != null) {
                // 检查相邻两个日期是否连续
                if (currentDate.minusDays(1).isEqual(previousDate)) {
                    consecutiveDays++;
                } else {
                    // 如果日期不连续，停止遍历
                    break;
                }
            }

            previousDate = currentDate;
        }

        return consecutiveDays;
    }

    @Override
    public PageResult<AdminUserSignInRecordDO> getSignInRecordPage(AdminUserSignInRecordPageReqVO pageReqVO) {
        // 根据用户昵称查询出用户ids
        Set<Long> userIds = null;
        if (StringUtils.isNotBlank(pageReqVO.getNickname())) {
            List<AdminUserDO> users = adminUserService.getUserListByNickname(pageReqVO.getNickname());
            // 如果查询用户结果为空直接返回无需继续查询
            if (CollUtil.isEmpty(users)) {
                return PageResult.empty();
            }
            userIds = convertSet(users, AdminUserDO::getId);
        }
        // 分页查询
        return signInRecordMapper.selectPage(pageReqVO, userIds);
    }

    @Override
    public PageResult<AdminUserSignInRecordDO> getSignRecordPage(Long userId, PageParam pageParam) {
        return signInRecordMapper.selectPage(userId, pageParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserSignInRecordDO createSignRecord(Long userId) {
        // 1. 获取当前用户最近的签到
        AdminUserSignInRecordDO lastRecord = signInRecordMapper.selectLastRecordByUserId(userId);
        // 1.1. 判断是否重复签到
        validateSigned(lastRecord);

        // 2.1. 获取所有的签到规则
        List<AdminUserSignInConfigDO> signInConfigs = signInConfigService.getSignInConfigList(CommonStatusEnum.ENABLE.getStatus());
        // 2.2. 组合数据
        AdminUserSignInRecordDO record = AdminUserSignInRecordConvert.INSTANCE.convert(userId, lastRecord, signInConfigs);

        // 3. 插入签到记录
        signInRecordMapper.insert(record);

        // 4. 增加权益
        if (!ObjectUtils.equalsAny(record.getMagicBean(), null, 0)) {
            AddRightsDTO signInRightsDTO = new AddRightsDTO()
                    .setUserId(userId)
                    .setMagicBean(record.getMagicBean())
                    .setMagicImage(record.getMagicImage())
                    .setMatrixBean(record.getMatrixBean())
                    .setTimeNums(1)
                    .setTimeRange(TimeRangeTypeEnum.MONTH.getType())
                    .setBizId(String.valueOf(record.getId()))
                    .setBizType(AdminUserRightsBizTypeEnum.SIGN.getType())
                    .setLevelId(null);
            adminUserRightsService.createRights(signInRightsDTO);
        }

        return record;
    }

    private void validateSigned(AdminUserSignInRecordDO signInRecordDO) {
        if (signInRecordDO == null) {
            return;
        }
        if (DateUtils.isToday(signInRecordDO.getCreateTime())) {
            throw exception(SIGN_IN_RECORD_TODAY_EXISTS);
        }
    }

}
