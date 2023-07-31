package com.starcloud.ops.business.app.service.app;

import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;

import java.util.List;

/**
 * 应用管理服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
public interface AppService {

    /**
     * 查询应用分类列表
     *
     * @return 应用分类列表
     */
    List<AppCategoryVO> categories();

    /**
     * 查询应用语言列表
     *
     * @return 应用语言列表
     */
    List<Option> languages();

    /**
     * 查询推荐的应用列表
     *
     * @return 模版列表
     */
    List<AppRespVO> listRecommendedApps(String model);

    /**
     * 查询推荐的应用详情
     *
     * @param uid 推荐应用唯一标识
     * @return 应用详情
     */
    AppRespVO getRecommendApp(String uid);

    /**
     * 获取步骤列表
     *
     * @return 步骤列表
     */
    List<WorkflowStepWrapperRespVO> stepList();

    /**
     * 分页查询应用列表
     *
     * @param query 查询条件
     * @return 应用列表
     */
    PageResp<AppRespVO> page(AppPageQuery query);

    /**
     * 根据应用 UID 获取应用详情
     *
     * @param uid 应用 UID
     * @return 应用详情
     */
    AppRespVO get(String uid);

    /**
     * 创建模版
     *
     * @param request 应用请求信息
     */
    AppRespVO create(AppReqVO request);

    /**
     * 复制应用
     *
     * @param request 应用请求信息
     */
    AppRespVO copy(AppReqVO request);

    /**
     * 应用模版
     *
     * @param request 应用更新请求信息
     */
    AppRespVO modify(AppUpdateReqVO request);

    /**
     * 根据应用 UID 删除应用
     *
     * @param uid 应用 UID
     */
    void delete(String uid);

    /**
     * 获取最新的wxmp聊天应用Uid
     */
    AppRespVO getRecently(Long userId);

}
