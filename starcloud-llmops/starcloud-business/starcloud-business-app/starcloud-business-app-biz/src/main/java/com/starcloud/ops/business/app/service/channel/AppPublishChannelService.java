package com.starcloud.ops.business.app.service.channel;

import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;

import java.util.List;

/**
 * 应用发布渠道服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
public interface AppPublishChannelService {

    /**
     * 根据应用uid获取发布渠道列表
     *
     * @param appUid 应用uid
     * @return {@link List<AppPublishChannelRespVO>}
     */
    List<AppPublishChannelRespVO> listByAppUid(String appUid);

    /**
     * 根据 Uid 获取发布渠道详情
     *
     * @param uid uid
     * @return {@link AppPublishChannelRespVO}
     */
    AppPublishChannelRespVO get(String uid);

    /**
     * 创建发布渠道
     *
     * @param request {@link AppPublishChannelReqVO}
     * @return {@link AppPublishChannelRespVO}
     */
    AppPublishChannelRespVO create(AppPublishChannelReqVO request);

    /**
     * 修改发布渠道
     *
     * @param request {@link AppPublishChannelReqVO}
     * @return {@link AppPublishChannelRespVO}
     */
    AppPublishChannelRespVO modify(AppPublishChannelReqVO request);

    /**
     * 修改发布渠道状态, 存在修改状态，不存在创建一个新发布渠道
     *
     * @param request {@link AppPublishChannelReqVO}
     * @return {@link AppPublishChannelRespVO}
     */
    AppPublishChannelRespVO changeStatus(AppPublishChannelReqVO request);

    /**
     * 根据 appUid 批量修改渠道的 publishUid
     *
     * @param appUid     应用 Uid
     * @param publishUid 发布 Uid
     */
    void updatePublishUidByAppUid(String appUid, String publishUid);

    /**
     * 根据应用uid删除
     *
     * @param appUid 应用uid
     */
    void deleteByAppUid(String appUid);
}
