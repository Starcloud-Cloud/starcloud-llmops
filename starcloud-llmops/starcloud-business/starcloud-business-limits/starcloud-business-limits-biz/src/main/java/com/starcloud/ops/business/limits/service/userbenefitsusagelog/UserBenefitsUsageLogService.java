package com.starcloud.ops.business.limits.service.userbenefitsusagelog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogPageReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsusagelog.UserBenefitsUsageLogDO;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户权益使用日志 Service 接口
 *
 * @author AlanCusack
 */
public interface UserBenefitsUsageLogService {

    /**
     * 创建用户权益使用日志
     *
     * @param createReqVO 创建信息
     *
     * @return 编号
     */
    Long createUserBenefitsUsageLog(@Valid UserBenefitsUsageLogCreateReqVO createReqVO);

    /**
     * 获得用户权益使用日志分页
     *
     * @param pageReqVO 分页查询
     *
     * @return 用户权益使用日志分页
     */
    PageResult<UserBenefitsUsageLogDO> getUserBenefitsUsageLogPage(UserBenefitsUsageLogPageReqVO pageReqVO);

    /**
     * 根据时间和 Action 获取指定的权益记录
     *
     * @param queryTime 查询时间 如果查询时间为空 则查询所有的action 记录，不建议为空
     *
     * @return 用户权益使用日志
     */
    List<UserBenefitsUsageLogDO> getUserBenefitsUsageLog(LocalDateTime queryTime, String action);

}
