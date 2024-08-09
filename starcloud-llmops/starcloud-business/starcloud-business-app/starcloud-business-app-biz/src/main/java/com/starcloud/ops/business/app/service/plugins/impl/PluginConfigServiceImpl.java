package com.starcloud.ops.business.app.service.plugins.impl;

import cn.hutool.core.util.IdUtil;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.convert.plugin.PluginConfigConvert;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginConfigDO;
import com.starcloud.ops.business.app.dal.mysql.plugin.PluginConfigMapper;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.LIBRARY_HAS_CONFIG;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.PLUGIN_CONFIG_NOT_EXIST;

@Slf4j
@Service
public class PluginConfigServiceImpl implements PluginConfigService {

    @Resource
    private PluginConfigMapper pluginConfigMapper;

    @Override
    public PluginConfigRespVO create(PluginConfigVO pluginConfigVO) {
        PluginConfigDO oldConfig = pluginConfigMapper.selectByLibraryUid(pluginConfigVO.getLibraryUid(), pluginConfigVO.getPluginUid());
        if (Objects.nonNull(oldConfig)) {
            throw exception(LIBRARY_HAS_CONFIG, pluginConfigVO.getLibraryUid());
        }
        PluginConfigDO pluginConfigDO = PluginConfigConvert.INSTANCE.convert(pluginConfigVO);
        pluginConfigDO.setUid(IdUtil.fastSimpleUUID());
        pluginConfigMapper.insert(pluginConfigDO);
        return PluginConfigConvert.INSTANCE.convert(pluginConfigDO);
    }

    @Override
    public void modify(PluginConfigReqVO pluginVO) {
        PluginConfigDO pluginConfigDO = getByUid(pluginVO.getUid());
        PluginConfigDO updateConfig = PluginConfigConvert.INSTANCE.convert(pluginVO);
        updateConfig.setId(pluginConfigDO.getId());
        pluginConfigMapper.updateById(updateConfig);
    }

    @Override
    public PluginConfigRespVO getByLibrary(String libraryUid, String pluginUid) {
        PluginConfigDO pluginConfigDO = pluginConfigMapper.selectByLibraryUid(libraryUid, pluginUid);
        return PluginConfigConvert.INSTANCE.convert(pluginConfigDO);
    }


    @Override
    public List<PluginConfigRespVO> configList(String libraryUid) {
        List<PluginConfigDO> pluginConfigDOList = pluginConfigMapper.selectByLibraryUid(libraryUid);
        return PluginConfigConvert.INSTANCE.convert(pluginConfigDOList);
    }

    public PluginConfigDO getByUid(String uid) {
        PluginConfigDO pluginConfigDO = pluginConfigMapper.selectByUid(uid);
        if (Objects.isNull(pluginConfigDO)) {
            throw exception(PLUGIN_CONFIG_NOT_EXIST, "uid :" + uid);
        }
        return pluginConfigDO;
    }
}
