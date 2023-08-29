package com.starcloud.ops.business.app.service.dict.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.limit.dto.DictionaryLimitDTO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.convert.category.CategoryConvert;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.limit.LimitConfigEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-04
 */
@Service
public class AppDictionaryServiceImpl implements AppDictionaryService {

    @Resource
    private DictDataService dictDataService;

    /**
     * 查询应用分类列表
     *
     * @return 应用分类列表
     */
    @Override
    public List<AppCategoryVO> categories() {
        // 查询应用分类字典数据
        DictDataExportReqVO request = new DictDataExportReqVO();
        request.setDictType(AppConstants.APP_CATEGORY_DICT_TYPE);
        request.setStatus(StateEnum.ENABLE.getCode());
        List<DictDataDO> dictDataList = dictDataService.getDictDataList(request);

        // 未查询到数据，返回空列表
        if (CollectionUtil.isEmpty(dictDataList)) {
            return Collections.emptyList();
        }
        // 转换为应用分类列表
        return dictDataList.stream().map(CategoryConvert.INSTANCE::convert).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 示例提示集合
     *
     * @return 示例提示集合
     */
    @Override
    public List<ImageMetaDTO> examplePrompt() {
        DictDataExportReqVO request = new DictDataExportReqVO();
        request.setDictType(AppConstants.IMAGE_EXAMPLE_PROMPT);
        request.setStatus(StateEnum.ENABLE.getCode());
        List<DictDataDO> dictDataList = dictDataService.getDictDataList(request);
        return CollectionUtil.emptyIfNull(dictDataList).stream()
                .filter(Objects::nonNull)
                .map(dictData -> {
                    ImageMetaDTO imageMetaDTO = new ImageMetaDTO();
                    imageMetaDTO.setLabel(dictData.getLabel());
                    imageMetaDTO.setValue(dictData.getValue());
                    return imageMetaDTO;
                }).collect(Collectors.toList());
    }

    /**
     * 获取应用限流兜底配置
     *
     * @return 应用限流兜底配置
     */
    @Override
    public AppPublishLimitRespVO appSystemLimit() {
        DictDataExportReqVO request = new DictDataExportReqVO();
        request.setDictType(AppConstants.APP_LIMIT_DEFAULT_CONFIG);
        request.setStatus(StateEnum.ENABLE.getCode());
        List<DictDataDO> dictDataList = dictDataService.getDictDataList(request);
        if (CollectionUtil.isEmpty(dictDataList)) {
            return null;
        }
        AppPublishLimitRespVO response = getDefaultSystemLimit();
        for (DictDataDO dictData : dictDataList) {
            if (StringUtils.isBlank(dictData.getRemark())) {
                continue;
            }
            DictionaryLimitDTO dictionaryLimit = JSONUtil.toBean(dictData.getRemark(), DictionaryLimitDTO.class);
            if ("SYSTEM_QUOTA".equals(dictData.getValue())) {
                response.setQuotaEnable(dictionaryLimit.getEnable());
                response.setQuotaConfig(dictionaryLimit.getConfig());
            }
            if ("SYSTEM_RATE".equals(dictData.getValue())) {
                response.setRateEnable(dictionaryLimit.getEnable());
                response.setRateConfig(dictionaryLimit.getConfig());
            }
            if ("SYSTEM_USER_QUOTA".equals(dictData.getValue())) {
                response.setUserQuotaEnable(dictionaryLimit.getEnable());
                response.setUserQuotaConfig(dictionaryLimit.getConfig());
            }
            if ("SYSTEM_ADVERTISING".equals(dictData.getValue())) {
                response.setAdvertisingEnable(dictionaryLimit.getEnable());
                response.setAdvertisingConfig(dictionaryLimit.getConfig());
            }
        }

        return response;
    }

    /**
     * 获取应用系统限流兜底配置
     *
     * @return 应用限流兜底配置
     */
    private static AppPublishLimitRespVO getDefaultSystemLimit() {
        AppPublishLimitRespVO response = new AppPublishLimitRespVO();
        response.setQuotaEnable(Boolean.TRUE);
        response.setQuotaConfig(LimitConfigEnum.QUOTA.getDefaultSystemConfig());
        response.setRateEnable(Boolean.TRUE);
        response.setRateConfig(LimitConfigEnum.RATE.getDefaultSystemConfig());
        response.setUserQuotaEnable(Boolean.TRUE);
        response.setUserQuotaConfig(LimitConfigEnum.USER_QUOTA.getDefaultSystemConfig());
        response.setAdvertisingEnable(Boolean.TRUE);
        response.setAdvertisingConfig(LimitConfigEnum.ADVERTISING.getDefaultSystemConfig());
        return response;
    }

}
