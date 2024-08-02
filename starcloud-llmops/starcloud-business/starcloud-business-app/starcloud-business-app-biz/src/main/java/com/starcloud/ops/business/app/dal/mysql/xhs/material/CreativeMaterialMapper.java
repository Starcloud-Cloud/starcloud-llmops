package com.starcloud.ops.business.app.dal.mysql.xhs.material;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.FilterMaterialReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.material.CreativeMaterialDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CreativeMaterialMapper extends BaseMapperX<CreativeMaterialDO> {

    default CreativeMaterialDO getByUid(String uid) {
        LambdaQueryWrapper<CreativeMaterialDO> wrapper = Wrappers.lambdaQuery(CreativeMaterialDO.class)
                .eq(CreativeMaterialDO::getUid, uid);
        return selectOne(wrapper);
    }

    default List<CreativeMaterialDO> filterMaterial(FilterMaterialReqVO reqVO) {
        LambdaQueryWrapper<CreativeMaterialDO> wrapper = Wrappers.lambdaQuery(CreativeMaterialDO.class)
                .eq(CreativeMaterialDO::getType, reqVO.getType())
                .likeRight(StringUtils.isNotBlank(reqVO.getContent()), CreativeMaterialDO::getContent, reqVO.getContent())
                .apply("FIND_IN_SET({0}, tags)", reqVO.getTag())
                .last("limit " + reqVO.getLimitCount());
        return selectList(wrapper);
    }
}
