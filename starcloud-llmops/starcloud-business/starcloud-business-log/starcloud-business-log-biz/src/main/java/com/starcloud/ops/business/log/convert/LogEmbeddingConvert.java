package com.starcloud.ops.business.log.convert;


import com.starcloud.ops.business.log.api.embedding.EmbeddingReqDTO;
import com.starcloud.ops.business.log.dal.dataobject.LogEmbeddingDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LogEmbeddingConvert {

    LogEmbeddingConvert INSTANCE = Mappers.getMapper(LogEmbeddingConvert.class);


    @Mapping(source = "userId", target = "creator")
    LogEmbeddingDO convert(EmbeddingReqDTO embeddingReqDTO);
}
