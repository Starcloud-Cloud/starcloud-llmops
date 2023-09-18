package com.starcloud.ops.business.app.service.dict.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.limit.dto.AppLimitRuleDTO;
import com.starcloud.ops.business.app.convert.category.CategoryConvert;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.RecommendAppConsts;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.limit.AppLimitRuleEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-04
 */
@Slf4j
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
    public List<AppLimitRuleDTO> systemLimitRuleList() {
        // 转换字典限流配置
        List<AppLimitRuleDTO> dictionaryLimitRuleList = CollectionUtil.emptyIfNull(getDictionaryList(AppConstants.APP_LIMIT_DEFAULT_CONFIG))
                .stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    AppLimitRuleDTO limitRule;
                    try {
                        limitRule = JSONUtil.toBean(item.getRemark(), AppLimitRuleDTO.class);
                    } catch (Exception e) {
                        log.warn("DictDataDO remark Json to AppLimitRuleDTO fail, Will ignore this rule：code: {}", item.getValue());
                        limitRule = null;
                    }
                    if (Objects.nonNull(limitRule)) {
                        limitRule.setCode(item.getValue().toUpperCase());
                    }
                    return limitRule;
                })
                .filter(Objects::nonNull)
                .filter(AppLimitRuleDTO::getEnable)
                .collect(Collectors.toList());

        List<AppLimitRuleDTO> systemLimitRuleList = AppLimitRuleEnum.defaultSystemLimitRuleList();

        // 字典未配置，取 AppLimitRuleEnum 的最后兜底配置
        if (CollectionUtil.isEmpty(dictionaryLimitRuleList)) {
            return systemLimitRuleList;
        }

        // 取并集，并且 code 相同时候，取 字典 配置
        Map<String, AppLimitRuleDTO> mergeMap = Stream.concat(dictionaryLimitRuleList.stream(), systemLimitRuleList.stream())
                .collect(Collectors.toMap(
                        AppLimitRuleDTO::getCode,
                        Function.identity(),
                        (existing, replacement) -> dictionaryLimitRuleList.contains(replacement) ? replacement : existing)
                );

        return mergeMap.values().stream().sorted(Comparator.comparingInt(AppLimitRuleDTO::getOrder)).collect(Collectors.toList());
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
     * 不需要进行应用限流的应用
     *
     * @return 不需要进行应用限流的应用
     */
    @Override
    public List<String> appLimitWhiteList() {
        List<String> list = Arrays.asList(RecommendAppConsts.GENERATE_TEXT, RecommendAppConsts.GENERATE_ARTICLE, RecommendAppConsts.BASE_GENERATE_IMAGE, RecommendAppConsts.CHAT_ROBOT);
        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.APP_LIMIT_WHITE_LIST);
        List<String> collect = CollectionUtil.emptyIfNull(dictDataList)
                .stream()
                .filter(item -> Objects.nonNull(item) && StringUtils.isNotBlank(item.getValue()))
                .map(item -> item.getValue().trim().toUpperCase())
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(collect)) {
            return list;
        }
        return Stream.concat(list.stream(), collect.stream()).distinct().collect(Collectors.toList());
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
     * 不走广告限流配置的场景
     *
     * @return 不走广告限流配置的场景
     */
    @Override
    public List<String> appLimitNoAdsScenes() {
        List<String> list = Arrays.asList(AppSceneEnum.WEB_ADMIN.name(), AppSceneEnum.WEB_MARKET.name(), AppSceneEnum.WEB_IMAGE.name(), AppSceneEnum.OPTIMIZE_PROMPT.name());
        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.APP_LIMIT_NO_ADS_SCENE_LIST);
        List<String> collect = CollectionUtil.emptyIfNull(dictDataList)
                .stream()
                .filter(item -> Objects.nonNull(item) && StringUtils.isNotBlank(item.getValue()))
                .map(item -> item.getValue().trim().toUpperCase())
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(collect)) {
            return list;
        }
        return Stream.concat(list.stream(), collect.stream()).distinct().collect(Collectors.toList());
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

}
