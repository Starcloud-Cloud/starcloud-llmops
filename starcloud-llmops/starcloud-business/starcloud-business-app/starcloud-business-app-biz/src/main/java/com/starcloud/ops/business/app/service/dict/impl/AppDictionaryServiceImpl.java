package com.starcloud.ops.business.app.service.dict.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.limit.dto.AppLimitConfigDTO;
import com.starcloud.ops.business.app.convert.category.CategoryConvert;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.limit.AppLimitConfigEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.APP_CATEGORY_DICT_TYPE);
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
        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.IMAGE_EXAMPLE_PROMPT);
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
    public List<AppLimitConfigDTO> appSystemLimitConfig() {
        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.APP_LIMIT_DEFAULT_CONFIG);
        List<AppLimitConfigDTO> systemLimitConfig = defaultSystemLimitConfig();
        if (CollectionUtil.isEmpty(dictDataList)) {
            return systemLimitConfig;
        }

        List<AppLimitConfigDTO> list = new ArrayList<>();
        for (AppLimitConfigDTO config : systemLimitConfig) {
            Optional<DictDataDO> any = dictDataList.stream().filter(item -> config.getCode().equalsIgnoreCase(item.getValue())).findAny();
            if (any.isPresent()) {
                DictDataDO dataDO = any.get();
                AppLimitConfigDTO limitConfig = JSONUtil.toBean(dataDO.getRemark(), AppLimitConfigDTO.class);
                if (limitConfig.getEnable()) {
                    limitConfig.setCode(dataDO.getValue().toUpperCase());
                    list.add(limitConfig);
                    continue;
                }
            }
            list.add(config);
        }

        return list;
    }

    /**
     * 限流总开关
     *
     * @return 是否开启限流
     */
    @Override
    public Boolean appLimitSwitch() {

        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.APP_LIMIT_SWITCH);
        if (CollectionUtil.isEmpty(dictDataList)) {
            return Boolean.TRUE;
        }
        DictDataDO data = dictDataList.get(0);
        if (Objects.isNull(data)) {
            return Boolean.TRUE;
        }

        if (StringUtils.isBlank(data.getValue())) {
            return Boolean.TRUE;
        }

        if ("true".equalsIgnoreCase(data.getValue())) {
            return Boolean.TRUE;
        }

        if ("false".equalsIgnoreCase(data.getValue())) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 用户白名单，白名单之内的用户ID，不进行限流
     *
     * @return 用户白名单
     */
    @Override
    public List<String> appLimitUserWhiteList() {
        List<DictDataDO> dictionaryList = getDictionaryList(AppConstants.APP_LIMIT_USER_WHITE_LIST);
        return CollectionUtil.emptyIfNull(dictionaryList).stream()
                .filter(item -> Objects.nonNull(item) && StringUtils.isNotBlank(item.getValue()))
                .map(DictDataDO::getValue).collect(Collectors.toList());
    }

    /**
     * 根据字典类型，获取正在启用的字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    private List<DictDataDO> getDictionaryList(String dictType) {
        DictDataExportReqVO request = new DictDataExportReqVO();
        request.setDictType(dictType);
        request.setStatus(StateEnum.ENABLE.getCode());
        List<DictDataDO> dictDataList = dictDataService.getDictDataList(request);
        if (CollectionUtil.isEmpty(dictDataList)) {
            return Collections.emptyList();
        }
        return dictDataList;
    }

    /**
     * 获取应用系统限流兜底配置
     *
     * @return 应用限流兜底配置
     */
    private static List<AppLimitConfigDTO> defaultSystemLimitConfig() {
        return Arrays.stream(AppLimitConfigEnum.values()).map(AppLimitConfigEnum::getDefaultSystemConfig).collect(Collectors.toList());
    }

}
