package com.starcloud.ops.business.app.dal.mysql.config;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.config.ChatExpandConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface ChatExpandConfigMapper extends BaseMapperX<ChatExpandConfigDO> {


    default List<ChatExpandConfigDO> selectByAppConfigUid(String appConfigId) {
        LambdaQueryWrapper<ChatExpandConfigDO> wrapper = Wrappers.lambdaQuery(ChatExpandConfigDO.class)
                .eq(ChatExpandConfigDO::getAppConfigId, appConfigId)
                .orderByAsc(ChatExpandConfigDO::getType);
        return this.selectList(wrapper);
    }

    default ChatExpandConfigDO selectByUid(String uid) {
        LambdaQueryWrapper<ChatExpandConfigDO> wrapper = Wrappers.lambdaQuery(ChatExpandConfigDO.class)
                .eq(ChatExpandConfigDO::getUid, uid);
        return this.selectOne(wrapper);
    }

    default List<ChatExpandConfigDO> selectByType(String appConfigId, Integer type) {
        LambdaQueryWrapper<ChatExpandConfigDO> wrapper = Wrappers.lambdaQuery(ChatExpandConfigDO.class)
                .eq(ChatExpandConfigDO::getAppConfigId, appConfigId)
                .eq(ChatExpandConfigDO::getType, true)
                .eq(ChatExpandConfigDO::getType, type);
        return this.selectList(wrapper);
    }

    default int modify(ChatExpandConfigDO configDO) {
        LambdaUpdateWrapper<ChatExpandConfigDO> updateWrapper = Wrappers.lambdaUpdate(ChatExpandConfigDO.class)
                .eq(ChatExpandConfigDO::getUid, configDO.getUid())
                .eq(ChatExpandConfigDO::getType,configDO.getType())
                .set(configDO.getDisabled() != null, ChatExpandConfigDO::getDisabled, configDO.getDisabled())
                .set(configDO.getConfig() != null, ChatExpandConfigDO::getConfig, configDO.getConfig());
        return this.update(null, updateWrapper);
    }
}
