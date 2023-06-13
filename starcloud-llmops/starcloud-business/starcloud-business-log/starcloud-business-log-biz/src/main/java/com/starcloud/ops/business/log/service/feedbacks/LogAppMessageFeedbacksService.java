package com.starcloud.ops.business.log.service.feedbacks;

import java.util.*;
import javax.validation.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.feedbacks.vo.*;
import com.starcloud.ops.business.log.dal.dataobject.*;

/**
 * 应用执行日志结果反馈 Service 接口
 *
 * @author 芋道源码
 */
public interface LogAppMessageFeedbacksService {

    /**
     * 创建应用执行日志结果反馈
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAppMessageFeedbacks(@Valid LogAppMessageFeedbacksCreateReqVO createReqVO);

    /**
     * 更新应用执行日志结果反馈
     *
     * @param updateReqVO 更新信息
     */
    void updateAppMessageFeedbacks(@Valid LogAppMessageFeedbacksUpdateReqVO updateReqVO);

    /**
     * 删除应用执行日志结果反馈
     *
     * @param id 编号
     */
    void deleteAppMessageFeedbacks(Long id);

    /**
     * 获得应用执行日志结果反馈
     *
     * @param id 编号
     * @return 应用执行日志结果反馈
     */
    LogAppMessageFeedbacksDO getAppMessageFeedbacks(Long id);

    /**
     * 获得应用执行日志结果反馈列表
     *
     * @param ids 编号
     * @return 应用执行日志结果反馈列表
     */
    List<LogAppMessageFeedbacksDO> getAppMessageFeedbacksList(Collection<Long> ids);

    /**
     * 获得应用执行日志结果反馈分页
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志结果反馈分页
     */
    PageResult<LogAppMessageFeedbacksDO> getAppMessageFeedbacksPage(LogAppMessageFeedbacksPageReqVO pageReqVO);

    /**
     * 获得应用执行日志结果反馈列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用执行日志结果反馈列表
     */
    List<LogAppMessageFeedbacksDO> getAppMessageFeedbacksList(LogAppMessageFeedbacksExportReqVO exportReqVO);

}