package com.starcloud.ops.business.app.service.channel;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.StatusRequest;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelModifyReqVO;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;

import java.util.List;
import java.util.Map;

/**
 * 应用发布渠道服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@SuppressWarnings("all")
public interface AppPublishChannelService {

    /**
     * 根据应用 UID 获取发布渠道列表
     *
     * @param appUid 应用uid
     * @return 发布渠道列表
     */
    List<AppPublishChannelRespVO> listByAppUid(String appUid);

    /**
     * 根据应用发布记录 UID 获取发布渠道列表
     *
     * @param appPublishUid 应用发布记录 UID
     * @return 发布渠道列表
     */
    List<AppPublishChannelRespVO> listByAppPublishUid(String appPublishUid);

    /**
     * 根据应用发布记录 UID 获取发布渠道 Map
     *
     * @param appPublishUid 应用发布记录 UID
     * @return 发布渠道 Map
     */
    Map<Integer, List<AppPublishChannelRespVO>> mapByAppPublishUidGroupByType(String appPublishUid);

    /**
     * 根据 发布渠道 UID 获取发布渠道详情
     *
     * @param uid uid
     * @return 发布渠道详情
     */
    AppPublishChannelRespVO get(String uid);

    /**
     * 根据发布媒介 UID 查找发布渠道详情
     *
     * @param mediumUid 发布媒介 UID
     * @return 发布渠道详情
     */
    AppPublishChannelRespVO getByMediumUid(String mediumUid);

    /**
     * 根据发布媒介 UID 查找应用信息
     *
     * @param mediumUid 发布媒介 UID
     * @return 应用信息
     */
    AppRespVO getAppByMediumUid(String mediumUid);

    /**
     * 创建发布渠道
     *
     * @param request 请求参数
     * @return 创建的发布渠道详情
     */
    AppPublishChannelRespVO create(AppPublishChannelReqVO request);

    /**
     * 修改发布渠道
     *
     * @param request 请求参数
     * @return 修改后的发布渠道详情
     */
    AppPublishChannelRespVO modify(AppPublishChannelModifyReqVO request);

    /**
     * 重置分享链接唯一标识
     *
     * @param uid 应用发布渠道 UID
     * @return 重置后的分享链接唯一标识
     */
    String resetShareSlug(String uid);

    /**
     * 修改发布渠道状态
     *
     * @param request 请求参数
     * @return 修改状态后的发布渠道详情
     */
    void operate(StatusRequest request);

    /**
     * 根据应用 UID 批量修改发布渠道的发布 UID
     *
     * @param appUid     应用 UID
     * @param publishUid 发布 UID
     */
    void updatePublishUidByAppUid(String appUid, String publishUid);

    /**
     * 根据发布 UID 删除
     *
     * @param uid 发布 UID
     */
    void delete(String uid);

    /**
     * 根据应用发布记录 UID 批量删除 发布渠道记录
     *
     * @param publishUid
     */
    void deleteByAppPublishUid(String publishUid);

    /**
     * 根据应用 UID 批量删除 发布渠道记录
     *
     * @param appUid 应用 UID
     */
    void deleteByAppUid(String appUid);


}
