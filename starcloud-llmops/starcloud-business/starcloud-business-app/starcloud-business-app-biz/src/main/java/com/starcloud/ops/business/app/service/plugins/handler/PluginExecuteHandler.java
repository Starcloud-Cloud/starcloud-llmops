package com.starcloud.ops.business.app.service.plugins.handler;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginExecuteRespVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public abstract class PluginExecuteHandler {

    static final String PREFIX_EXECTUE = "coze_exectue_";

    static final String PREFIX_EXECTUE_ERROR = "prefix_exectue_error";

    static final String PREFIX_START = "coze_start_";

    static final String VERIFY_PARAMS = "verify_params_";

    /**
     * 支持的插件平台
     *
     * @return
     */
    abstract String supportPlatform();

    /**
     * 验证
     */
    public abstract String verify(PluginTestReqVO reqVO);

    /**
     * 验证结果
     *
     */
    public abstract VerifyResult verifyResult(PluginTestResultReqVO resultReqVO);

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
