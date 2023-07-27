package com.starcloud.ops.business.app.service.publish;

import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishPageReqVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.framework.common.api.dto.PageResp;

import java.util.List;

/**
 * 应用发布服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
public interface AppPublishService {

    /**
     * 分页查询应用发布记录
     *
     * @param query 请求参数
     * @return 应用发布响应
     */
    PageResp<AppPublishRespVO> page(AppPublishPageReqVO query);

    /**
     * 根据应用 UID 查询应用发布记录, 根据版本号倒序排序
     *
     * @param appUid 应用 UID
     * @return 应用发布响应
     */
    List<AppPublishRespVO> listByAppUid(String appUid);

    /**
     * 根据发布 UID 查询应用发布记录
     *
     * @param uid 发布 UID
     * @return 应用发布响应
     */
    AppPublishRespVO getByUid(String uid);

    /**
     * 创建一条应用发布记录
     *
     * @param request 请求参数
     * @return 应用发布响应
     */
    AppPublishRespVO create(AppPublishReqVO request);

    /**
     * 审核应用发布记录
     *
     * @param uid   发布 UID
     * @param audit 审核状态
     */
    void audit(String uid, Integer audit);

    /**
     * 提供给用户的接口，用于取消发布到模版市场和重新发布到模版市场
     *
     * @param uid   发布 UID
     * @param audit 审核状态
     */
    void operate(String uid, Integer audit);
}
