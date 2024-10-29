package com.starcloud.ops.business.app.service.plugins.handler;

import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginResultReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginExecuteRespVO;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
public abstract class PluginExecuteHandler {

    @Resource
    private SocialUserService socialUserService;

    static final String prefix_exectue = "coze_exectue_";

    static final String prefix_start = "coze_start_";

    /**
     * 支持的插件平台
     *
     * @return
     */
    abstract String supportPlatform();

    /**
     * 异步执行插件
     *
     * @param reqVO
     * @return
     */
    public abstract String executePlugin(PluginExecuteReqVO reqVO);

    /**
     * 查询执行结果
     */
    public abstract PluginExecuteRespVO getPluginResult(PluginResultReqVO pluginResultReqVO);


    void cleanMap(Map<String, Object> objectMap) {
        objectMap.remove("TAKO_BOT_HISTORY");
    }

}
