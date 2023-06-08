package com.starcloud.ops.business.dataset.convert.segment;


import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DocumentSegmentConvert {
    DocumentSegmentConvert INSTANCE = Mappers.getMapper(DocumentSegmentConvert.class);

    RecordDTO segmentDo2Record(DocumentSegmentDO dto);

}
