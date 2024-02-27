package com.starcloud.ops.business.app.service.xhs.batch;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;

import java.util.List;

public interface CreativePlanBatchService {

    /**
     * 新建执行批次
     */
    void createBatch(Long batch, CreativePlanRespVO creativePlan);

    /**
     * 执行结束更新状态
     */
    void updateCompleteStatus(String planUid, Long batch);

    /**
     * 分页查询批次
     */
    PageResult<CreativePlanBatchRespVO> page(CreativePlanBatchPageReqVO pageReqVO);

    /**
     * 最近一次执行批次的记录内容
     * @param planUidList
     * @return
     */
    List<CreativePlanBatchRespVO> latestBatch(List<String> planUidList);

}
