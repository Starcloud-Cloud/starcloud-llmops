package com.starcloud.ops.business.app.dal.mysql.plugin;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PluginConfigMapper extends BaseMapperX<PluginConfigDO> {

    default PluginConfigDO selectByUid(String uid) {
        LambdaQueryWrapper<PluginConfigDO> wrapper = Wrappers.lambdaQuery(PluginConfigDO.class)
                .eq(PluginConfigDO::getUid, uid);
        return selectOne(wrapper);
    }

    default PluginConfigDO selectByLibraryUid(String libraryUid, String pluginUid) {
        LambdaQueryWrapper<PluginConfigDO> wrapper = Wrappers.lambdaQuery(PluginConfigDO.class)
                .eq(PluginConfigDO::getPluginUid, pluginUid)
                .eq(PluginConfigDO::getLibraryUid, libraryUid)
                .orderByDesc(PluginConfigDO::getUpdateTime)
                ;
        return selectOne(wrapper);
    }

    default List<PluginConfigDO> selectByLibraryUid(String libraryUid) {
        LambdaQueryWrapper<PluginConfigDO> wrapper = Wrappers.lambdaQuery(PluginConfigDO.class)
                .eq(PluginConfigDO::getLibraryUid, libraryUid)
                .orderByDesc(PluginConfigDO::getUpdateTime)
                ;
        return selectList(wrapper);
    }
}
