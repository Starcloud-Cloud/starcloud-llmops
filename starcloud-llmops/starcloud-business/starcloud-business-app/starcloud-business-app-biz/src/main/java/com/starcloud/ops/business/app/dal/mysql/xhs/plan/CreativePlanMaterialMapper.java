package com.starcloud.ops.business.app.dal.mysql.xhs.plan;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanMaterialDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CreativePlanMaterialMapper extends BaseMapper<CreativePlanMaterialDO> {

    default CreativePlanMaterialDO getMaterial(String uid) {
        LambdaQueryWrapper<CreativePlanMaterialDO> wrapper = Wrappers.lambdaQuery(CreativePlanMaterialDO.class);
        wrapper.select(CreativePlanMaterialDO::getUid, CreativePlanMaterialDO::getMaterialList,
                CreativePlanMaterialDO::getId);
        wrapper.eq(CreativePlanMaterialDO::getUid, uid);
        return this.selectOne(wrapper);
    }

    default CreativePlanMaterialDO getMaterialByAppUid(String appUid, Long userId, String source) {
        LambdaQueryWrapper<CreativePlanMaterialDO> wrapper = Wrappers.lambdaQuery(CreativePlanMaterialDO.class);
        wrapper.select(CreativePlanMaterialDO::getUid, CreativePlanMaterialDO::getMaterialList);
        wrapper.eq(CreativePlanMaterialDO::getAppUid, appUid)
                .eq(CreativePlanDO::getCreator, userId)
                .eq(CreativePlanDO::getSource, source);
        return this.selectOne(wrapper);
    }
}
