package com.starcloud.ops.business.app.service.chat;

import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigReqVO;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigRespVO;

import java.util.List;
import java.util.Map;

public interface ChatExpandConfigService {

    /**
     * 查询app所有配置 包含enable=false
     *
     * @param appUid
     * @return
     */
    Map<Integer, List<ChatExpandConfigRespVO>> getConfig(String appConfigId);


    /**
     * 新增配置
     *
     * @param reqVO
     * @return
     */
    String create(ChatExpandConfigReqVO reqVO);


    /**
     * 修改配置
     *
     * @param reqVO
     * @return
     */
    void modify(ChatExpandConfigReqVO reqVO);

    /**
     * 删除配置
     *
     * @param uid
     */
    void delete(String uid);

    /**
     * 发布 copy配置
     * @param sourceConfigId
     * @param targetConfigId
     */
    void copyConfig(String sourceConfigId, String targetConfigId);
}
