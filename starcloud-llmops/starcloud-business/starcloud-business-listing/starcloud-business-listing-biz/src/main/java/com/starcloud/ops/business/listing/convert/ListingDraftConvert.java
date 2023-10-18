package com.starcloud.ops.business.listing.convert;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DraftCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DraftSaveReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftRespVO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDraftDO;
import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ListingDraftConvert {
    ListingDraftConvert INSTANCE = Mappers.getMapper(ListingDraftConvert.class);

    @Mapping(source = "draftConfig",target = "config")
    ListingDraftDO convert(DraftCreateReqVO draftCreateReqVO);


    @Mapping(source = "keywordResume",target = "keywordResume",qualifiedByName = "parseKeyword")
    @Mapping(source = "config",target = "draftConfig",qualifiedByName = "parseConfig")
    @Mapping(source = "fiveDesc",target = "fiveDesc",qualifiedByName = "parseDesc")
    DraftRespVO convert(ListingDraftDO draftDO);

    List<DraftRespVO> convert(List<ListingDraftDO> draftDOS);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "keywordResume", target = "keywordResume", ignore = true)
    void update(DraftSaveReqVO reqVO,@MappingTarget ListingDraftDO draftDO);

    default String jsonStr(DraftConfigDTO object) {
        return JSONUtil.toJsonStr(object);
    }

    default String jsonStr(List<String> object) {
        return JSONUtil.toJsonStr(object);
    }

    @Named("parseKeyword")
    default List<KeywordResumeDTO> parseKeyword(String str) {
        return JSONUtil.parseArray(str).toList(KeywordResumeDTO.class);
    }

    @Named("parseConfig")
    default DraftConfigDTO parseConfig(String str) {
        return JSONUtil.toBean(str,DraftConfigDTO.class);
    }

    @Named("parseDesc")
    default List<String> parseDesc(String str) {
        return JSONUtil.parseArray(str).toList(String.class);
    }

}
