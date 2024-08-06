package com.starcloud.ops.business.app.convert.plugin;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginDefinitionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PluginDefinitionConvert {

    PluginDefinitionConvert INSTANCE = Mappers.getMapper(PluginDefinitionConvert.class);

    List<PluginRespVO> convert(List<PluginDefinitionDO> pluginDOS);

    PluginDefinitionDO convert(PluginDefinitionVO pluginVO);

    PluginRespVO convert(PluginDefinitionDO pluginConfigDO);
}
