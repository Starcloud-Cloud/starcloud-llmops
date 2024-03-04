package com.starcloud.ops.business.listing.convert;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.response.ExtendAsinReposeExcelVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

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



    default List<ExtendAsinReposeExcelVO> convertList(List<ItemsDTO> beans){
        return beans.stream().map(bean -> {
            ExtendAsinReposeExcelVO vo = new ExtendAsinReposeExcelVO();
            vo.setAdProducts(bean.getAdProducts());
            vo.setMarketId(bean.getMarketId());
            vo.setMonopolyAsinDtos(bean.getMonopolyAsinDtos());
            vo.setAvgRating(bean.getAvgRating());
            vo.setPurchases(bean.getPurchases());
            vo.setCvsShareRate(bean.getCvsShareRate());
            return vo;
        }).collect(Collectors.toList());
    }

    default ExtendAsinReposeExcelVO convertExcelVO(ItemsDTO bean){
        ExtendAsinReposeExcelVO excelVO = new ExtendAsinReposeExcelVO();
        excelVO.setKeywords(bean.getKeyword());
        excelVO.setKeywordCn(bean.getMarketId());
        excelVO.setTrafficPercentage(bean.getMonopolyAsinDtos());
        excelVO.setCalculatedWeeklySearches(bean.getPurchases());
        excelVO.setRelationVariationsItem(bean.getAvgRating());
        excelVO.setSearchesRank(bean.getSearchesRank());
        excelVO.setSearches(bean.getSearches());
        excelVO.setSearchesDays(bean.getSearches()/30);
        excelVO.setPurchases(bean.getPurchases());
        excelVO.setPurchaseRate(bean.getPurchaseRate());
        excelVO.setCprExact(bean.getPurchaseRate());


        return excelVO;
    }




}
