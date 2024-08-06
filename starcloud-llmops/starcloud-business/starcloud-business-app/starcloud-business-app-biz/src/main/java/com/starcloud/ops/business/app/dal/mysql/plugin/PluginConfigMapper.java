package com.starcloud.ops.business.app.dal.mysql.plugin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginConfigDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PluginConfigMapper extends BaseMapper<PluginConfigDO> {

    default PluginConfigDO selectByUid(String uid) {
        LambdaQueryWrapper<PluginConfigDO> wrapper = Wrappers.lambdaQuery(PluginConfigDO.class)
                .eq(PluginConfigDO::getUid, uid);
        return selectOne(wrapper);
    }

    default PluginConfigDO selectByLibraryUid(String LibraryUid) {
        LambdaQueryWrapper<PluginConfigDO> wrapper = Wrappers.lambdaQuery(PluginConfigDO.class)
                .eq(PluginConfigDO::getLibraryUid, LibraryUid);
        return selectOne(wrapper);
    }
}
