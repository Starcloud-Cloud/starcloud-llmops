package com.starcloud.ops.business.app.service.xhs.plan;

import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
public interface CreativePlanService {

    /**
     * 上传图片
     *
     * @param image 上传图片
     * @return 图片信息
     */
    UploadImageInfoDTO uploadImage(MultipartFile image);

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
    void updatePlanStatus(String planUid);

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

}
