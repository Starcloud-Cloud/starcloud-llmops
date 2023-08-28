package com.starcloud.ops.business.app.service.dict.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.convert.category.CategoryConvert;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
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


        return null;
    }
}
