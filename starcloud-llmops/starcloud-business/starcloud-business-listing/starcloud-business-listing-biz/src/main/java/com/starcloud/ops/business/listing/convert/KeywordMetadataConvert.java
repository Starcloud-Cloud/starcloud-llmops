package com.starcloud.ops.business.listing.convert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
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

    static DepartmentsDTO convertDepartmentsDTO(String data) {
        return JSONUtil.toBean(data, DepartmentsDTO.class);
    }
    static String convertDepartmentsDTO(DepartmentsDTO data) {
        return JSONUtil.toJsonStr(data);
    }
    static List<SearchesTrendDTO> convertSearchesTrendDTO(String data) {
        return JSONUtil.toList(data, SearchesTrendDTO.class);
    }

    static String convertSearchesTrendDTO(List<SearchesTrendDTO> data) {
        return JSONUtil.toJsonStr(data);
    }
    static List<MonopolyAsinDtosDTO> convertMonopolyAsinDtosDTO(String data) {
        return JSONUtil.toList(data, MonopolyAsinDtosDTO.class);
    }


    static String convertMonopolyAsinDtosDTO(List<MonopolyAsinDtosDTO> data) {
        return JSONUtil.toJsonStr(data);
    }





}
