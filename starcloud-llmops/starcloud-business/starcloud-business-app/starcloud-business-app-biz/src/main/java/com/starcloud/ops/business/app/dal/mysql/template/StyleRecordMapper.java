package com.starcloud.ops.business.app.dal.mysql.template;

import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.starcloud.ops.business.app.dal.databoject.template.StyleRecordDO;
import com.starcloud.ops.business.app.dal.databoject.template.StyleRecordDTO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StyleRecordMapper extends BaseMapperX<StyleRecordDO> {

    default List<StyleRecordDO> selectList(String creator) {
        LambdaQueryWrapper<StyleRecordDO> wrapper = Wrappers.lambdaQuery(StyleRecordDO.class)
                .eq(StyleRecordDO::getCreator, creator)
                .orderByDesc(StyleRecordDO::getId);
        return selectList(wrapper);
    }

    @DataPermission(enable = false)
    default List<StyleRecordDTO> templateList(String creator) {
        MPJLambdaWrapper<StyleRecordDO> wrapper = new MPJLambdaWrapper<StyleRecordDO>("t")
                .selectAs(StyleRecordDO::getUid, StyleRecordDTO::getUid)
                .selectAs(StyleRecordDO::getStyleUid, StyleRecordDTO::getStyleUid)
                .selectAs(StyleRecordDO::getCreateTime, StyleRecordDTO::getCreateTime)
                .selectAs("configuration->>'$.imageStyleList'", StyleRecordDTO::getImageStyleList)
                .leftJoin(CreativePlanDO.class, "p", CreativePlanDO::getUid, StyleRecordDO::getPlanUid)
                .eq(StyleRecordDO::getCreator, creator);
        return selectJoinList(StyleRecordDTO.class, wrapper);
    }
}
