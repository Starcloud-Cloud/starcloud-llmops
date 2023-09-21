package com.starcloud.ops.business.log.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.log.dal.dataobject.LogEmbeddingDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogEmbeddingMapper extends BaseMapperX<LogEmbeddingDO> {
}
