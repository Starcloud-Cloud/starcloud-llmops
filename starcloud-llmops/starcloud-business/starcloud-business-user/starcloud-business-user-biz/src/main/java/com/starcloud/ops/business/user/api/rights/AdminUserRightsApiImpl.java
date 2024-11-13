package com.starcloud.ops.business.user.api.rights;

import cn.hutool.core.lang.Assert;
import com.starcloud.ops.business.user.api.rights.dto.AddRightsDTO;
import com.starcloud.ops.business.user.api.rights.dto.ReduceRightsDTO;
import com.starcloud.ops.business.user.api.rights.dto.StatisticsUserRightReqDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsRecordService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.RIGHTS_BIZ_NOT_SUPPORT;

/**
 * 用户积分的 API 实现类
 *
 * @author owen
 */
@Service
@Validated
public class AdminUserRightsApiImpl implements AdminUserRightsApi {

    @Resource
    private AdminUserRightsService adminUserRightsService;

    @Resource
    private AdminUserRightsRecordService adminUserRightsRecordService;

    @Override
    public void addRights(Long userId, Integer magicBean, Integer magicImage, Integer matrixBean, Integer rightsTimeNums, Integer rightsTimeRange,
                          Integer bizType, String bizId, Long levelId) {
        Assert.isTrue(magicBean > 0 || magicImage > 0);
        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);
        if (bizTypeEnum == null) {
            throw exception(RIGHTS_BIZ_NOT_SUPPORT);
        }
        adminUserRightsService.createRights(userId, magicBean, magicImage, matrixBean, rightsTimeNums, rightsTimeRange, bizType, bizId, levelId);
    }

    /**
     * @param addRightsDTO 新增权益DTO
     */
    @Override
    public void addRights(AddRightsDTO addRightsDTO) {
        adminUserRightsService.createRights(addRightsDTO);
    }

    @Override
    public void reduceRights(Long userId, Long teamOwnerId, Long teamId, AdminUserRightsTypeEnum rightsType, Integer rightAmount,
                             Integer bizType, String bizId) {
        Assert.isTrue(rightAmount > 0);
        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);

        if (bizTypeEnum == null) {
            throw exception(RIGHTS_BIZ_NOT_SUPPORT);
        }
        adminUserRightsService.reduceRights(userId, teamOwnerId, teamId, rightsType, rightAmount, bizTypeEnum, bizId);
    }

    /**
     * @param reduceRightsDTO 权益扣减DTO
     */
    @Override
    public void reduceRights(ReduceRightsDTO reduceRightsDTO) {
        adminUserRightsService.reduceRights(reduceRightsDTO);
    }

    /**
     * 判断权益是否充足
     *
     * @param userId      用户 ID
     * @param rightsType  权益类型
     * @param rightAmount 检测权益数 可以为空 为空 仅仅判断当前权益数大于 0
     * @return 是否充足 布尔值
     */
    @Override
    public Boolean calculateUserRightsEnough(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount) {
        return adminUserRightsService.calculateUserRightsEnough(userId, rightsType, rightAmount);
    }

    /**
     * 统计权益。 通过业务ID
     *
     * @param bizIdList 业务ID
     * @return 权益统计
     */
    @Override
    public List<StatisticsUserRightReqDTO> statisticsUserRightsByBizId(List<String> bizIdList) {
        return adminUserRightsRecordService.statisticsUserRightsByBizId(bizIdList);
    }

}
