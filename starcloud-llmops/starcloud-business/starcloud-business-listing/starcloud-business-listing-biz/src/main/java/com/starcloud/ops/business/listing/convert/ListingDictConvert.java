package com.starcloud.ops.business.listing.convert;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictModifyReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictRespVO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDictDO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDictDTO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ListingDictConvert {

    ListingDictConvert INSTANCE = Mappers.getMapper(ListingDictConvert.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateParams(DictModifyReqVO params, @MappingTarget ListingDictDO dictDO);


    ListingDictDO convert(DictCreateReqVO reqVO);

    DictRespVO convert(ListingDictDO dictDO);

    List<DictRespVO> convert(List<ListingDictDTO> list);

    @Mapping(source = "username",target = "createUser")
    DictRespVO convert(ListingDictDTO dictDO);

    @Named("parseKey")
    default String parseKeyword(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return null;
        }
        List<KeywordResumeDTO> collect = keys.stream().map(KeywordResumeDTO::new).collect(Collectors.toList());
        return JSONUtil.toJsonStr(collect);
    }

    default String listToStr(List<KeywordResumeDTO> strs) {
        return JSONUtil.toJsonStr(strs);
    }

    default List<KeywordResumeDTO> strToList(String keywords) {
        return JSONUtil.parseArray(keywords).toList(KeywordResumeDTO.class);
    }

}
