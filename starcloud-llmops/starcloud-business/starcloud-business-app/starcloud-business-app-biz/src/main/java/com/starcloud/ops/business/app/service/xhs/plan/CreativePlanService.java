package com.starcloud.ops.business.app.service.xhs.plan;

import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;

import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
public interface CreativePlanService {

    /**
     * 创作计划元数据
     *
     * @return 元数据
     */
    Map<String, List<Option>> metadata();

    /**
     * 获取创作计划详情
     *
     * @param uid 创作计划UID
     * @return 创作计划详情
     */
    CreativePlanRespVO get(String uid);

    /**
     * 获取创作计划分页列表
     *
     * @param query 请求参数
     * @return 创作计划分页列表
     */
    PageResp<CreativePlanRespVO> page(CreativePlanPageQuery query);

    /**
     * 创建创作计划
     *
     * @param request 创作计划请求
     * @return 创作计划UID
     */
    String create(CreativePlanReqVO request);

    /**
     * 复制创作计划
     *
     * @param request 创作计划请求
     * @return 创作计划UID
     */
    String copy(UidRequest request);

    /**
     * 修改创作计划
     *
     * @param request 创作计划请求
     * @return 创作计划UID
     */
    String modify(CreativePlanModifyReqVO request);

    /**
     * 修改创作计划状态
     *
     * @param uid    创作计划UID
     * @param status 修改状态
     */
    void updateStatus(String uid, String status);

    /**
     * 更新计划状态
     */
    void updatePlanStatus(String planUid, Long batch);

    /**
     * 删除创作计划
     *
     * @param uid 创作计划UID
     */
    void delete(String uid);

    /**
     * 执行创作计划
     *
     * @param uid 创作计划UID
     */
    void execute(String uid);

    /**
     * 批量执行任务创建
     *
     * @param creativePlan 创作计划
     */
    void bathCreativeContent(CreativePlanRespVO creativePlan);

}
