package com.starcloud.ops.business.app.service.xhs.batch;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchListReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
public interface CreativePlanBatchService {

    /**
     * 获取批次详情
     *
     * @param uid 批次UID
     * @return 批次详情
     */
    CreativePlanBatchRespVO get(String uid);

    /**
     * 获取批次列表
     *
     * @param query 查询条件
     * @return 批次列表
     */
    List<CreativePlanBatchRespVO> list(CreativePlanBatchListReqVO query);

    /**
     * 分页查询批次
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<CreativePlanBatchRespVO> page(CreativePlanBatchPageReqVO query);

    /**
     * 新建执行批次
     *
     * @param request 请求
     * @return 批次UID
     */
    String create(CreativePlanBatchReqVO request);

    /**
     * 开始执行批次，将批次状态更改为执行中
     *
     * @param batchUid 批次UID
     */
    void startBatch(String batchUid);

    /**
     * 取消批次
     *
     * @param batchUid 批次UID
     */
    void cancelBatch(String batchUid);

    /**
     * 更新批次状态
     *
     * @param batchUid 批次UID
     */
    void updateStatus(String batchUid);

    /**
     * 删除批次
     *
     * @param planUid 计划UID
     */
    void deleteByPlanUid(String planUid);

}
