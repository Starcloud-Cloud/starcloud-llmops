package com.starcloud.ops.business.dataset.convert.segment;


import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper
public interface DocumentSegmentConvert {
    DocumentSegmentConvert INSTANCE = Mappers.getMapper(DocumentSegmentConvert.class);

    RecordDTO segmentDo2Record(DocumentSegmentDO dto);

    RecordDTO convert(DocumentSegmentDTO dto);

    default LocalDateTime long2Time(Long milliseconds) {
        Instant instant = Instant.ofEpochMilli(milliseconds);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        return zonedDateTime.toLocalDateTime();
    }
}
