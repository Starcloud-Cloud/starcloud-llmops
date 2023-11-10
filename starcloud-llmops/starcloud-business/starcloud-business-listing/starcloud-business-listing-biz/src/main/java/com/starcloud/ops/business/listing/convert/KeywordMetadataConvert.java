package com.starcloud.ops.business.listing.convert;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface KeywordMetadataConvert {

    KeywordMetadataConvert INSTANCE = Mappers.getMapper(KeywordMetadataConvert.class);


    @Mappings({@Mapping(target = "gkDatas",  expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertGkDatasDTO(bean.getGkDatas()))"),
            @Mapping(target = "departments",  expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertDepartmentsDTO(bean.getDepartments()))"),
            @Mapping(target = "trends",  expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertSearchesTrendDTO(bean.getTrends()))"),
            @Mapping(target = "monopolyAsinDtos",  expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertMonopolyAsinDtosDTO(bean.getMonopolyAsinDtos()))")})
    KeywordMetadataRespVO convert(KeywordMetadataDO bean);

    List<KeywordMetadataRespVO> convertList(List<KeywordMetadataDO> beans);

    PageResult<KeywordMetadataRespVO> convertPage(PageResult<KeywordMetadataDO> page);



    @Mappings({@Mapping(target = "gkDatas",  expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertGkDatasDTO(bean.getGkDatas()))"),
            @Mapping(target = "departments",  expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertDepartmentsDTO(bean.getDepartments()))"),
            @Mapping(target = "trends",  expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertSearchesTrendDTO(bean.getTrends()))"),
            @Mapping(target = "monopolyAsinDtos",  expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertMonopolyAsinDtosDTO(bean.getMonopolyAsinDtos()))")})
    KeywordMetadataDO convert(ItemsDTO bean);


    static List<GkDatasDTO> convertGkDatasDTO(String data) {
        return JSONUtil.toList(data, GkDatasDTO.class);
    }
    static String convertGkDatasDTO(List<GkDatasDTO> data) {
        return JSONUtil.toJsonStr(data);
    }

    static List<DepartmentsDTO> convertDepartmentsDTO(String data) {
        return JSONUtil.toList(data, DepartmentsDTO.class);
    }
    static String convertDepartmentsDTO(List<DepartmentsDTO> data) {
        return JSONUtil.toJsonStr(data);
    }
    static List<SearchesTrendsDTO> convertSearchesTrendDTO(String data) {
        return JSONUtil.toList(data, SearchesTrendsDTO.class);
    }

    static String convertSearchesTrendDTO(List<SearchesTrendsDTO> data) {
        return JSONUtil.toJsonStr(data);
    }
    static List<MonopolyAsinDtosDTO> convertMonopolyAsinDtosDTO(String data) {
        return JSONUtil.toList(data, MonopolyAsinDtosDTO.class);
    }


    static String convertMonopolyAsinDtosDTO(List<MonopolyAsinDtosDTO> data) {
        return JSONUtil.toJsonStr(data);
    }





}
