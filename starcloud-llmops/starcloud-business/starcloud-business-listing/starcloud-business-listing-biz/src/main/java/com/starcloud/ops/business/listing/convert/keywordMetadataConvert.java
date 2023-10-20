package com.starcloud.ops.business.listing.convert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictModifyReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDictDO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface keywordMetadataConvert {

    keywordMetadataConvert INSTANCE = Mappers.getMapper(keywordMetadataConvert.class);


    @Mappings({@Mapping(target = "gkDatas",  expression = "java(com.starcloud.ops.business.listing.convert.keywordMetadataConvert.convertGkDatasDTO(bean.getGkDatas()))"),
            @Mapping(target = "departments",  expression = "java(com.starcloud.ops.business.dataset.convert.keywordMetadataConvert.convertRelationVariationsItemsDTO(bean.getDepartments()))"),
            @Mapping(target = "trends",  expression = "java(com.starcloud.ops.business.dataset.convert.keywordMetadataConvert.convertSearchesTrendDTO(bean.getTrends()))"),
            @Mapping(target = "monopolyAsinDtos",  expression = "java(com.starcloud.ops.business.dataset.convert.keywordMetadataConvert.convertMonopolyAsinDtosDTO(bean.getMonopolyAsinDtos()))")})
    KeywordMetadataRespVO convert(KeywordMetadataDO bean);

    List<KeywordMetadataRespVO> convertList(KeywordMetadataDO bean);

    PageResult<KeywordMetadataRespVO> convertPage(PageResult<KeywordMetadataDO> page);



    static GkDatasDTO convertGkDatasDTO(String data) {
        return JSONUtil.toBean(data, GkDatasDTO.class);
    }

    static RelationVariationsItemsDTO convertRelationVariationsItemsDTO(String data) {
        return JSONUtil.toBean(data, RelationVariationsItemsDTO.class);
    }
    static SearchesTrendDTO convertSearchesTrendDTO(String data) {
        return JSONUtil.toBean(data, SearchesTrendDTO.class);
    }
    static MonopolyAsinDtosDTO convertMonopolyAsinDtosDTO(String data) {
        return JSONUtil.toBean(data, MonopolyAsinDtosDTO.class);
    }


}
