package com.starcloud.ops.business.app.convert.plugin;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PluginConfigConvert {

    PluginConfigConvert INSTANCE = Mappers.getMapper(PluginConfigConvert.class);

    PluginConfigDO convert(PluginConfigVO pluginConfigVO);

    PluginConfigRespVO convert(PluginConfigDO pluginConfigDO);

    PluginConfigDO convert(PluginConfigReqVO pluginVO);


    List<PluginConfigRespVO> convert(List<PluginConfigDO> pluginConfigDOList);

}
