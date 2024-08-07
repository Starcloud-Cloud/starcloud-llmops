package com.starcloud.ops.business.app.service.plugins;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginTestReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.VerifyResult;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginTestRespVO;
import com.starcloud.ops.business.app.feign.dto.coze.CozeBotInfo;
import com.starcloud.ops.business.app.feign.dto.coze.SpaceBot;
import com.starcloud.ops.business.app.feign.dto.coze.SpaceInfo;

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
     * 空间中所有bot
     */
    SpaceInfo spaceBot(String spaceId, String accessTokenId, Integer pageSize, Integer pageIndex);

    /**
     * 插件详情
     */
    PluginRespVO detail(String uid);

    /**
     * 枚举类型
     */
    Map<String, Object> metadata();

    /**
     * 验证机器人
     */
    String verify(PluginTestReqVO reqVO);

    /**
     * 验证结果
     *
     * @param code
     * @return
     */
    VerifyResult verifyResult(String code, String accessTokenId);
}
