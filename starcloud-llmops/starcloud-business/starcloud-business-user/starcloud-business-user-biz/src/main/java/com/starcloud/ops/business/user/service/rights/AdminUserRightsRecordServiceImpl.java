package com.starcloud.ops.business.user.service.rights;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.api.rights.dto.StatisticsUserRightReqDTO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.record.AdminUserRightsRecordPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsRecordDO;
import com.starcloud.ops.business.user.dal.mysql.rights.AdminUserRightsRecordMapper;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;


/**
 * 积分记录 Service 实现类
 *
 * @author QingX
 */
@Slf4j
@Service
@Validated
public class AdminUserRightsRecordServiceImpl implements AdminUserRightsRecordService {

    @Resource
    private AdminUserRightsRecordMapper adminUserRightsRecordMapper;

    @Resource
    private AdminUserService adminUserService;

    @Override
    public PageResult<AdminUserRightsRecordDO> getPointRecordPage(AdminUserRightsRecordPageReqVO pageReqVO) {
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
        return adminUserRightsRecordMapper.selectPage(pageReqVO, userIds);
    }

    @Override
    public PageResult<AdminUserRightsRecordDO> getPointRecordPage(Long userId, PageParam pageVO) {
        return adminUserRightsRecordMapper.selectPage(userId, pageVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRightsRecord(Long userId, Long teamOwnerId, Long teamId, Integer amount, AdminUserRightsTypeEnum rightsType, Integer bizType, String bizId, String bizCode) {
        if (amount == 0) {
            return;
        }
        AdminUserRightsBizTypeEnum rightsBizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);
        // 1. 校验权益
        if (!rightsBizTypeEnum.isAdd() && amount > 0) {
            amount = -amount;
        }

        // 3. 增加积分记录
        AdminUserRightsRecordDO record = new AdminUserRightsRecordDO()
                .setUserId(userId).setBizCode(bizCode).setBizId(bizId).setBizType(bizType)
                .setTitle(rightsBizTypeEnum.getName()).setOwnerId(teamOwnerId).setTeamId(teamId)
                .setDescription(StrUtil.format(rightsBizTypeEnum.getDescription(), amount, rightsType.getName()))
                .setRightsType(rightsType.getType()).setRightsAmount(amount);
        // if (getLoginUserId() == null) {
        record.setCreator(String.valueOf(userId));
        record.setUpdater(String.valueOf(userId));
        // }
        adminUserRightsRecordMapper.insert(record);
    }

    /**
     * 用户权益统计
     *
     * @param teamId  团队 ID
     * @param userIds 用户 ID，
     */
    @Override
    public List<StatisticsUserRightReqDTO> calculateRightUsedByUser(Long teamId, List<Long> userIds) {
        return adminUserRightsRecordMapper.calculateRightUsedByUser(teamId, userIds);

    }

    @Override
    public List<StatisticsUserRightReqDTO> statisticsUserRightsByBizId(List<String> bizIdList) {
        return adminUserRightsRecordMapper.statisticsUserRightsByBizId(bizIdList);
    }

}
