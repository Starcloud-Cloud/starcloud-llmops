package com.starcloud.ops.business.app.dal.mysql.plugin;

import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginDefinitionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PluginDefinitionMapper extends BaseMapper<PluginDefinitionDO> {

    @DataPermission(enable = false)
    List<PluginDefinitionDO> publishedList();


    default List<PluginDefinitionDO> selectByUid(List<String> uids) {
        LambdaQueryWrapper<PluginDefinitionDO> wrapper = Wrappers.lambdaQuery(PluginDefinitionDO.class)
                .in(PluginDefinitionDO::getUid,uids);
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

    List<PluginDefinitionDO> selectOwnerPlugin();

    default void deleteOwnerPlugin(String uid, String creator) {
        LambdaQueryWrapper<PluginDefinitionDO> wrapper = Wrappers.lambdaQuery(PluginDefinitionDO.class)
                .eq(PluginDefinitionDO::getCreator, creator)
                .eq(PluginDefinitionDO::getUid, uid);
        delete(wrapper);
    }
}
