package com.starcloud.ops.business.app.service.plugins;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginListReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.feign.dto.coze.BotListInfo;
import com.starcloud.ops.business.app.feign.dto.coze.CozeBotInfo;
import com.starcloud.ops.business.app.feign.dto.coze.SpaceListInfo;

import java.util.List;
import java.util.Map;

public interface PluginsDefinitionService {

    /**
     * 新建插件
     */
    PluginRespVO create(PluginDefinitionVO pluginVO);

    /**
     * 系统插件
     */
    List<PluginRespVO> publishedList();


    List<PluginRespVO> ownerList();

    /**
     * 插件列表
     */
    List<PluginRespVO> list(PluginListReqVO reqVO);

    /**
     * 查询应用下的插件列表
     *
     * @param appUid 应用uid
     * @return 插件列表
     */
    List<PluginRespVO> list(String appUid);

    /**
     * 发布
     */
    void publish(String uid);

    /**
     * 修改配置
     */
    PluginRespVO modifyPlugin(PluginConfigModifyReqVO reqVO);

    /**
     * 删除
     */
    void delete(String uid);

    /**
     * 测试链接
     */
    CozeBotInfo botInfo(String botId, String accessTokenId);

    /**
     * bot token
     */
    String bearer(String accessTokenId);

    /**
     * 空间中所有bot
     */
    BotListInfo spaceBot(String spaceId, String accessTokenId, Integer pageSize, Integer pageIndex);

    /**
     * 插件详情
     */
    PluginRespVO detail(String uid);

    /**
     * 枚举类型
     */
    Map<String, Object> metadata();

    /**
     * 空间列表
     */
    SpaceListInfo spaceList(String accessTokenId, Integer pageSize, Integer pageIndex);

    void updateTime(Long time, String pluginUid);

}
