package com.starcloud.ops.business.app.service.plugins;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;

public interface PluginConfigService {
    /**
     * 新增配置
     */
    PluginConfigRespVO create(PluginConfigVO pluginVO);

    /**
     * 修改配置
     */
    void modify(PluginConfigReqVO pluginVO);

    /**
     * @param libraryUid 素材库uid
     * @return
     */
    PluginConfigRespVO getByLibrary(String libraryUid, String pluginUid);
}
