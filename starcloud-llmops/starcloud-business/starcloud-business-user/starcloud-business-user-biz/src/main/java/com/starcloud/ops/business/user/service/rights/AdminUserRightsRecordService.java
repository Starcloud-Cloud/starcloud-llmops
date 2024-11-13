package com.starcloud.ops.business.user.service.rights;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.api.rights.dto.StatisticsUserRightReqDTO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.record.AdminUserRightsRecordPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsRecordDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;

import java.util.List;

/**
 * 用户积分记录 Service 接口
 *
 * @author QingX
 */
public interface AdminUserRightsRecordService {

    /**
     * 【管理员】获得积分记录分页
     *
     * @param pageReqVO 分页查询
     * @return 签到记录分页
     */
    PageResult<AdminUserRightsRecordDO> getPointRecordPage(AdminUserRightsRecordPageReqVO pageReqVO);

    /**
     * 【会员】获得积分记录分页
     *
     * @param userId 用户编号
     * @param pageVO 分页查询
     * @return 签到记录分页
     */
    PageResult<AdminUserRightsRecordDO> getPointRecordPage(Long userId, PageParam pageVO);

    /**
     * 创建用户积分记录
     *
     * @param userId     用户ID
     * @param amount     变动权益
     * @param rightsType 权益类型
     * @param bizType    业务类型
     * @param bizId      业务编号
     */
    void createRightsRecord(Long userId, Long teamOwnerId, Long teamId, Integer amount, AdminUserRightsTypeEnum rightsType, Integer bizType, String bizId, String bizCode);


    /**
     * 用户权益统计
     *
     * @param teamId  团队 ID
     * @param userIds 用户 ID，
     */
    List<StatisticsUserRightReqDTO> calculateRightUsedByUser(Long teamId, List<Long> userIds);

    /**
     * 统计权益,根据业务ID
     *
     * @param bizIdList 业务ID
     * @return 统计结果
     */
    List<StatisticsUserRightReqDTO> statisticsUserRightsByBizId(List<String> bizIdList);
}
