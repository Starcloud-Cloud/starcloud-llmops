package com.starcloud.ops.business.listing.convert;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DraftCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DraftReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftDetailExcelVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftRespVO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDraftDO;
import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import com.starcloud.ops.business.listing.dto.DraftItemScoreDTO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Mapper
public interface ListingDraftConvert {
    ListingDraftConvert INSTANCE = Mappers.getMapper(ListingDraftConvert.class);

    @Mapping(source = "draftConfig", target = "config")
    ListingDraftDO convert(DraftCreateReqVO draftCreateReqVO);

    @Mapping(source = "draftConfig", target = "config")
    ListingDraftDO convert(DraftReqVO draftCreateReqVO);


    @Mapping(source = "config", target = "draftConfig", qualifiedByName = "parseConfig")
    @Mapping(source = "fiveDesc", target = "fiveDesc", qualifiedByName = "parseFiveDesc")
    DraftRespVO convert(ListingDraftDO draftDO);

    List<DraftRespVO> convert(List<ListingDraftDO> draftDOS);

    List<DraftDetailExcelVO> convertExcel(List<ListingDraftDO> draftDOS);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
//    @Mapping(source = "endpoint", target = "endpoint", ignore = true)
    void update(DraftReqVO reqVO, @MappingTarget ListingDraftDO draftDO);

    default String jsonStr(DraftConfigDTO object) {
        return JSONUtil.toJsonStr(object);
    }

    default String jsonStr(List<String> object) {
        return JSONUtil.toJsonStr(object);
    }

    default String jsonStr(Map<String, String> object) {
        return JSONObject.toJSONString(object);
    }

    default String jsonStr(DraftItemScoreDTO object) {
        return JSONObject.toJSONString(object);
    }

    default DraftItemScoreDTO parseScore(String str) {
        return JSONUtil.toBean(str, DraftItemScoreDTO.class);
    }

    @Named("parseKeyword")
    default List<KeywordResumeDTO> parseKeyword(String str) {
        return JSONUtil.parseArray(str).toList(KeywordResumeDTO.class);
    }

    @Named("parseConfig")
    default DraftConfigDTO parseConfig(String str) {
        return JSONUtil.toBean(str, DraftConfigDTO.class);
    }

    @Named("parseFiveDesc")
    default Map<String, String> parseFiveDesc(String str) {
        Type type = new TypeReference<Map<String, String>>() {
        }.getType();
        return JSONObject.parseObject(str, type);
    }

}
