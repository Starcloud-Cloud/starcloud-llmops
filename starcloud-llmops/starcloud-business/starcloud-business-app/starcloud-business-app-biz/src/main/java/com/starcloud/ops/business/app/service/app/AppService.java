package com.starcloud.ops.business.app.service.app;

import com.starcloud.ops.business.app.api.app.dto.AppCategoryDTO;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.request.AppPublishRequest;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.api.app.request.AppUpdateRequest;
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
    List<AppCategoryDTO> categories();

    /**
     * 查询推荐的应用列表
     *
     * @return 模版列表
     */
    List<AppDTO> listRecommendedApps();

    /**
     * 分页查询应用列表
     *
     * @param query 查询条件
     * @return 应用列表
     */
    PageResp<AppDTO> page(AppPageQuery query);

    /**
     * 根据应用 UID 获取应用详情
     *
     * @param uid 应用 UID
     * @return 应用详情
     */
    AppDTO getByUid(String uid);

    /**
     * 创建模版
     *
     * @param request 应用请求信息
     */
    void create(AppRequest request);

    /**
     * 复制应用
     *
     * @param request 应用请求信息
     */
    void copy(AppRequest request);

    /**
     * 应用模版
     *
     * @param request 应用更新请求信息
     */
    void modify(AppUpdateRequest request);

    /**
     * 根据应用 UID 删除应用
     *
     * @param uid 应用 UID
     */
    void deleteByUid(String uid);

    /**
     * 发布应用到应用市场
     *
     * @param request 应用发布到应用市场请求对象
     */
    void publicAppToMarket(AppPublishRequest request);

    /**
     * 校验应用是否已经下载过
     *
     * @param marketKey 应用市场 UID。
     * @return 是否已经下载
     */
    Boolean verifyHasDownloaded(String marketKey);

    /**
     * 应用名称重复校验
     *
     * @param name 应用名称
     * @return 是否重复
     */
    Boolean duplicateNameVerification(String name);

}
