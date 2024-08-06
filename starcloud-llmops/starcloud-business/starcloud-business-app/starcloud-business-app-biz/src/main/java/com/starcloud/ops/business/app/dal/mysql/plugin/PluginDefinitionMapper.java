package com.starcloud.ops.business.app.dal.mysql.plugin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginDefinitionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PluginDefinitionMapper extends BaseMapper<PluginDefinitionDO> {

    default List<PluginDefinitionDO> publishedList() {
        LambdaQueryWrapper<PluginDefinitionDO> wrapper = Wrappers.lambdaQuery(PluginDefinitionDO.class)
                .eq(PluginDefinitionDO::getPublished, true);
        return selectList(wrapper);
    }

    default PluginDefinitionDO selectByName(String name) {
        LambdaQueryWrapper<PluginDefinitionDO> wrapper = Wrappers.lambdaQuery(PluginDefinitionDO.class)
                .eq(PluginDefinitionDO::getPluginName, name);
        return selectOne(wrapper);
    }

    default PluginDefinitionDO selectByUid(String uid) {
        LambdaQueryWrapper<PluginDefinitionDO> wrapper = Wrappers.lambdaQuery(PluginDefinitionDO.class)
                .eq(PluginDefinitionDO::getUid, uid);
        return selectOne(wrapper);
    }

    default List<PluginDefinitionDO> selectOwnerPlugin(String creator) {
        LambdaQueryWrapper<PluginDefinitionDO> wrapper = Wrappers.lambdaQuery(PluginDefinitionDO.class)
                .eq(PluginDefinitionDO::getCreator, creator);
        return selectList(wrapper);
    }

    default void deleteOwnerPlugin(String uid, String creator) {
        LambdaQueryWrapper<PluginDefinitionDO> wrapper = Wrappers.lambdaQuery(PluginDefinitionDO.class)
                .eq(PluginDefinitionDO::getCreator, creator)
                .eq(PluginDefinitionDO::getUid, uid);
        delete(wrapper);
    }
}
