package com.starcloud.ops.business.listing.convert;

import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataBasicRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.dto.DraftRecommendKeyDTO;
import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ListingKeywordConvert {

    ListingKeywordConvert INSTANCE = Mappers.getMapper(ListingKeywordConvert.class);

    List<KeywordMetaDataDTO> convert(List<KeywordMetadataBasicRespVO> metadataRespVO);

    List<DraftRecommendKeyDTO> convert2(List<KeywordMetaDataDTO> keywordMetaData);
}
