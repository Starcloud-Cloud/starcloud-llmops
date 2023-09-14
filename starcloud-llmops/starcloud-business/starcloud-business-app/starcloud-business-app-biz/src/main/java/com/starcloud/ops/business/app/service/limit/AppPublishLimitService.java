package com.starcloud.ops.business.app.service.limit;

import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitModifyReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitQuery;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitReqVO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;

/**
 * 应用发布限流服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
public interface AppPublishLimitService {

    /**
     * 根据 uid 获取应用发布限流信息
     *
     * @param uid 应用 uid
     * @return 应用发布限流信息
     */
    AppPublishLimitRespVO get(String uid);

    /**
     * 根据查询条件查询限流信息, 如果不存在则返回默认值
     *
     * @param query 查询条件
     * @return 应用发布限流信息
     */
    AppPublishLimitRespVO get(AppPublishLimitQuery query);

    /**
     * 创建应用发布限流信息
     *
     * @param request 应用发布限流信息
     */
    void create(AppPublishLimitReqVO request);

    /**
     * 更新应用发布限流信息
     *
     * @param request 应用发布限流信息
     */
    void modify(AppPublishLimitModifyReqVO request);

    /**
     * 根据 appUid 更新发布 uid
     *
     * @param appUid     应用 UID
     * @param publishUid 发布 UID
     */
    void updatePublishUidByAppUid(String appUid, String publishUid);

    /**
     * 删除应用发布限流信息
     *
     * @param uid 应用 uid
     */
    void delete(String uid);

    /**
     * 根据 appUid 删除应用发布限流信息
     *
     * @param appUid 应用 uid
     */
    void deleteByAppUid(String appUid);

    /**
     * 根据 publishUid 删除应用发布限流信息
     *
     * @param publishUid 发布 uid
     */
    void deleteByPublishUid(String publishUid);

}
