package com.starcloud.ops.business.user.api.level;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.starcloud.ops.business.user.api.level.dto.AdminUserLevelRespDTO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelDetailRespVO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.TIME_RANGE_BIZ_NOT_SUPPORT;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.LEVEL_BIZ_NOT_SUPPORT;

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

    /**
     * 获得会员等级列表
     *
     * @param userId 会员ID
     * @return 会员等级
     */
    @Override
    public List<AdminUserLevelRespDTO> getAdminUserLevelList(Long userId) {
        List<AdminUserLevelDetailRespVO> levelList = adminUserLevelService.getLevelList(userId);
        return BeanUtils.toBean(levelList,AdminUserLevelRespDTO.class);
    }

    /**
     * 新增会员等级
     *
     * @param userId  会员ID
     * @param levelId 会员等级编号
     */
    @Override
    public void addAdminUserLevel(Long userId, Long levelId, Integer TimeNums, Integer timeRange,
                                  Integer bizType, String bizId) {

        if (Objects.isNull(levelId) || levelId == 0) {
            log.warn("会员等级 ID 不存在，直接返回，当前业务 ID 为{}", bizId);
            return;
        }
        // 2.0 计算会员有效期
        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);
        if (bizTypeEnum == null) {
            throw exception(LEVEL_BIZ_NOT_SUPPORT);
        }

        TimeRangeTypeEnum timeRangeEnum = TimeRangeTypeEnum.getByType(timeRange);
        if (timeRangeEnum == null) {
            throw exception(TIME_RANGE_BIZ_NOT_SUPPORT);
        }

        log.warn("设置会员等级");
        AdminUserLevelCreateReqVO createReqVO = new AdminUserLevelCreateReqVO();
        createReqVO.setUserId(userId);
        createReqVO.setLevelId(levelId);

        createReqVO.setBizId(bizId);
        createReqVO.setBizType(bizType);

        createReqVO.setTimeNums(TimeNums);
        createReqVO.setTimeRange(timeRange);

        createReqVO.setDescription(String.format(bizTypeEnum.getDescription(), levelId));


        adminUserLevelService.createLevelRecord(createReqVO);


    }

    /**
     * 过期会员等级
     *
     * @param userId  会员ID
     * @param levelId 会员等级编号
     */
    @Override
    public void expireAdminUserLevel(Long userId, Long levelId) {

    }
}
