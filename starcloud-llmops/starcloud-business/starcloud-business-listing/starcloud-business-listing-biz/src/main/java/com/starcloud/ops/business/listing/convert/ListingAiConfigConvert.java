package com.starcloud.ops.business.listing.convert;

import com.starcloud.ops.business.listing.dto.AiConfigDTO;
import com.starcloud.ops.business.listing.vo.ListingGenerateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ListingAiConfigConvert {

    ListingAiConfigConvert INSTANCE = Mappers.getMapper(ListingAiConfigConvert.class);

    ListingGenerateRequest convert(AiConfigDTO aiConfigDTO);
}
