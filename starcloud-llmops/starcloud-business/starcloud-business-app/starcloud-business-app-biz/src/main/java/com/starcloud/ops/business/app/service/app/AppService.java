package com.starcloud.ops.business.app.service.app;

import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.api.app.request.AppUpdateRequest;
import com.starcloud.ops.framework.common.api.dto.PageResp;

import java.util.List;

/**
 * 模版管理服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
public interface AppService {

    /**
     * 查询推荐的模版列表
     *
     * @return 模版列表
     */
    List<AppDTO> listRecommendedTemplates();

    /**
     * 分页查询模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    PageResp<AppDTO> page(AppPageQuery query);

    /**
     * 分页查询下载的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    PageResp<AppDTO> pageDownloadTemplates(AppPageQuery query);

    /**
     * 分页查询我的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    PageResp<AppDTO> pageMyTemplate(AppPageQuery query);

    /**
     * 根据模版 ID 获取模版详情
     *
     * @param id 模版 ID
     * @return 模版详情
     */
    AppDTO getById(Long id);

    /**
     * 根据模版 UID 获取模版详情
     *
     * @param uid 模版 UID
     * @return 模版详情
     */
    AppDTO getByUid(String uid);

    /**
     * 创建模版
     *
     * @param request 模版信息
     * @return 是否创建成功
     */
    Boolean create(AppRequest request);

    /**
     * 复制模版
     *
     * @param request 模版信息
     * @return 是否复制成功
     */
    Boolean copy(AppRequest request);

    /**
     * 更新模版
     *
     * @param request 模版信息
     * @return 是否更新成功
     */
    Boolean modify(AppUpdateRequest request);

    /**
     * 删除模版
     *
     * @param id 模版ID
     * @return 是否删除成功
     */
    Boolean delete(Long id);

    /**
     * 根据模版 UID 删除模版
     *
     * @param uid 模版 UID
     * @return 是否删除成功
     */
    Boolean deleteByUid(String uid);

    /**
     * 校验模版是否已经下载过
     *
     * @param marketKey 模版市场特有的 key，唯一。
     * @return 是否已经下载
     */
    Boolean verifyHasDownloaded(String marketKey);

    /**
     * 模版名称重复校验
     * @param name 模版名称
     * @return 是否重复
     */
    Boolean duplicateNameVerification(String name);

}
