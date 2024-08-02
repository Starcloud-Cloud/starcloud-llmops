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


    @Mappings({@Mapping(target = "gkDatas", expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertGkDatasDTO(bean.getGkDatas()))"),
            @Mapping(target = "departments", expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertDepartmentsDTO(bean.getDepartments()))"),
            @Mapping(target = "trends", expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertSearchesTrendDTO(bean.getTrends()))"),
            @Mapping(target = "monopolyAsinDtos", expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertMonopolyAsinDtosDTO(bean.getMonopolyAsinDtos()))")})
    KeywordMetadataRespVO convert(KeywordMetadataDO bean);

    List<KeywordMetadataRespVO> convertList(List<KeywordMetadataDO> beans);

    PageResult<KeywordMetadataRespVO> convertPage(PageResult<KeywordMetadataDO> page);


    @Mappings({@Mapping(target = "gkDatas", expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertGkDatasDTO(bean.getGkDatas()))"),
            @Mapping(target = "departments", expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertDepartmentsDTO(bean.getDepartments()))"),
            @Mapping(target = "trends", expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertSearchesTrendDTO(bean.getTrends()))"),
            @Mapping(target = "monopolyAsinDtos", expression = "java(com.starcloud.ops.business.listing.convert.KeywordMetadataConvert.convertMonopolyAsinDtosDTO(bean.getMonopolyAsinDtos()))")})
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


    default List<ExtendAsinReposeExcelVO> convertExcelVOList(List<ItemsDTO> beans) {
        return beans.stream().map(this::convertExcelVO).collect(Collectors.toList());
    }

    default ExtendAsinReposeExcelVO convertExcelVO(ItemsDTO bean) {
        ExtendAsinReposeExcelVO excelVO = new ExtendAsinReposeExcelVO();

        if (bean == null){
            return excelVO;
        }
        excelVO.setKeywords(bean.getKeywords());
        excelVO.setKeywordCn(bean.getKeywordCn());
        excelVO.setTrafficPercentage(bean.getTrafficPercentage());
        excelVO.setCalculatedWeeklySearches(bean.getCalculatedWeeklySearches());
        excelVO.setRelationVariationsItem(bean.getRelationVariationsItems().stream().map(RelationVariationsItemsDTO::getAsin).collect(Collectors.joining("/")));
        excelVO.setSearchesRank(bean.getSearchesRank());
        excelVO.setSearches(bean.getSearches());
        excelVO.setSearchesDays(Math.floorDiv(bean.getSearches() == null ? 1 : bean.getSearches() <= 0 ? 1 : bean.getSearches(), 30));
        excelVO.setPurchases(bean.getPurchases());
        excelVO.setPurchaseRate(bean.getPurchaseRate());
        excelVO.setCprExact(bean.getCprExact());
        excelVO.setTitleDensityExact(bean.getTitleDensityExact());
        excelVO.setProducts(bean.getProducts());
        excelVO.setSupplyDemandRatio(bean.getSupplyDemandRatio());
        excelVO.setLatest1daysAds(bean.getLatest1daysAds());
        excelVO.setLatest7daysAds(bean.getLatest7daysAds());
        excelVO.setLatest30daysAds(bean.getLatest30daysAds());
        excelVO.setClickTop3s(bean.getClickTop3s().stream().map(ClickTop3sDTO::getAsin).collect(Collectors.joining("/")));
        excelVO.setTop3ClickingRate(bean.getTop3ClickingRate());
        excelVO.setTop3ConversionRate(bean.getTop3ConversionRate());
        excelVO.setBid(bean.getBid());
        excelVO.setBidRate(bean.getBidMin() + "-" + bean.getBidMax());
        excelVO.setBadges(bean.getBadges().stream().map(Object::toString).collect(Collectors.joining("/")));


        return excelVO;
    }


}
