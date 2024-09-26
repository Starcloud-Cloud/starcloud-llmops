package com.starcloud.ops.business.app.service.plugins;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.enums.plugin.PluginBindTypeEnum;

import java.util.List;

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
     * 删除插件配置
     */
    void delete(String uid, boolean forced);

    void deleteByPluginUid(String pluginUid);

    /**
     * @param libraryUid 素材库uid
     */
    PluginConfigRespVO getByLibrary(String libraryUid, String pluginUid);

    /**
     * 素材配置列表
     *
     * @param libraryUid
     * @return
     */
    List<PluginConfigRespVO> configList(String libraryUid);

    /**
     * 全量复制插件配置 定时任务
     * 覆盖掉所有配置
     *
     * @param sourceUid 素材库uid
     * @param targetUid 素材库uid
     */
    void copyPluginConfig(String sourceUid, String targetUid, PluginBindTypeEnum typeEnum);

    /**
     * 更新用户插件配置 只新增和删除
     */
    void updatePluginConfig(String sourceUid, String targetUid);
}
