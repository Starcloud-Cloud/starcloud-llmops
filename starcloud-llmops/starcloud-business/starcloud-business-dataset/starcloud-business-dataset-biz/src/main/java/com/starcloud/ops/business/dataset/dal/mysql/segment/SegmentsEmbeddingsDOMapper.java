package com.starcloud.ops.business.dataset.dal.mysql.segment;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.SegmentsEmbeddingsDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SegmentsEmbeddingsDOMapper extends BaseMapperX<SegmentsEmbeddingsDO> {

    default SegmentsEmbeddingsDO selectOneByHash(String segmentHash) {
        LambdaQueryWrapper<SegmentsEmbeddingsDO> queryWrapper = Wrappers.lambdaQuery(SegmentsEmbeddingsDO.class)
                .eq(SegmentsEmbeddingsDO::getSegmentHash, segmentHash)
                .orderByDesc(SegmentsEmbeddingsDO::getId)
                .last("limit 1");
        return this.selectOne(queryWrapper);
    }
}