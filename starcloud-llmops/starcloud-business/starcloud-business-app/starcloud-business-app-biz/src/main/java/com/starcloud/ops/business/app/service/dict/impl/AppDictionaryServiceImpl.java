package com.starcloud.ops.business.app.service.dict.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
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
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<AppCategoryVO> categoryList(Boolean isRoot) {
        String categoryType = AppConstants.APP_CATEGORY_DICT_TYPE;
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        if (AppConstants.MOFAAI_TENANT_ID.equals(tenantId)) {
            categoryType = AppConstants.APP_CATEGORY_DICT_TYPE;
        } else if (AppConstants.JUZHEN_TENANT_ID.equals(tenantId)) {
            categoryType = AppConstants.APP_CATEGORY_DICT_TYPE_JU_ZHEN;
        }

        // 查询应用分类字典数据
        List<DictDataDO> dictDataList = getDictionaryList(categoryType);
        if (CollectionUtil.isEmpty(dictDataList)) {
            return Collections.emptyList();
        }
        Stream<AppCategoryVO> stream = dictDataList.stream().map(CategoryConvert.INSTANCE::convert);
        if (isRoot) {
            stream = stream.filter(category -> AppConstants.ROOT.equalsIgnoreCase(category.getParentCode()));
        }
        if (AppConstants.JUZHEN_TENANT_ID.equals(tenantId)) {
            // 不是管理员，不显示其他分类
            if (UserUtils.isNotAdmin()) {
                stream = stream.filter(category -> !"OTHER".equalsIgnoreCase(category.getCode()));
            }
        }
        return stream.sorted(Comparator.comparingInt(AppCategoryVO::getSort)).collect(Collectors.toList());
    }

    /**
     * 查询应用分类列表
     *
     * @return 应用分类列表
     */
    @Override
    public List<AppCategoryVO> categoryTree() {
        List<AppCategoryVO> categoryList = categoryList(Boolean.FALSE);
        // 递归实现分类树
        return categoryListToTree(categoryList, AppConstants.ROOT);
    }

    /**
     * 热门搜索应用市场应用名称列表
     *
     * @return 热门搜索应用市场应用名称列表
     */
    @Override
    public List<String> hotSearchMarketAppNameList() {
        String hotSearchMarket = AppConstants.APP_HOT_SEARCH_MARKET;
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        if (AppConstants.MOFAAI_TENANT_ID.equals(tenantId)) {
            hotSearchMarket = AppConstants.APP_HOT_SEARCH_MARKET;
        } else if (AppConstants.JUZHEN_TENANT_ID.equals(tenantId)) {
            hotSearchMarket = AppConstants.APP_HOT_SEARCH_MARKET_JU_ZHEN;
        }
        List<DictDataDO> dictDataList = getDictionaryList(hotSearchMarket);
        return CollectionUtil.emptyIfNull(dictDataList).stream().sorted(Comparator.comparingInt(DictDataDO::getSort)).map(DictDataDO::getLabel).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
    }

    /**
     * 示例提示集合
     *
     * @return 示例提示集合
     */
    @Override
    public List<ImageMetaDTO> examplePrompt() {
        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.IMAGE_EXAMPLE_PROMPT);
        return CollectionUtil.emptyIfNull(dictDataList).stream().filter(Objects::nonNull).map(dictData -> {
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
        return CollectionUtil.emptyIfNull(dictionaryList).stream().filter(item -> Objects.nonNull(item) && StringUtils.isNotBlank(item.getValue())).map(DictDataDO::getValue).collect(Collectors.toList());
    }

    /**
     * 查询应用分类树
     *
     * @return 应用分类树
     */
    @Override
    public List<AppCategoryVO> creativeSchemeCategoryTree() {
        // 查询应用分类字典数据
        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.CREATIVE_SCHEME_CATEGORY_DICT_TYPE);
        if (CollectionUtil.isEmpty(dictDataList)) {
            return Collections.emptyList();
        }
        List<AppCategoryVO> collect = dictDataList.stream().map(CategoryConvert.INSTANCE::convert).sorted(Comparator.comparingInt(AppCategoryVO::getSort)).collect(Collectors.toList());
        // 递归实现分类树
        return categoryListToTree(collect, AppConstants.ROOT);
    }

    /**
     * 搜索应用模板名称，用于搜索应用市场应用名称
     *
     * @return 搜索应用模板名称
     */
    @Override
    public List<String> appTemplateAppNameList() {

        String hotSearchMarket = AppConstants.APP_TEMPLATE_SEARCH_MARKET;
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        if (AppConstants.MOFAAI_TENANT_ID.equals(tenantId)) {
            hotSearchMarket = AppConstants.APP_TEMPLATE_SEARCH_MARKET;
        } else if (AppConstants.JUZHEN_TENANT_ID.equals(tenantId)) {
            hotSearchMarket = AppConstants.APP_TEMPLATE_SEARCH_MARKET_JU_ZHEN;
        }
        List<DictDataDO> dictDataList = getDictionaryList(hotSearchMarket);
        return CollectionUtil.emptyIfNull(dictDataList)
                .stream()
                .sorted(Comparator.comparingInt(DictDataDO::getSort))
                .map(DictDataDO::getLabel).filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取应用默认配置
     *
     * @return 应用默认配置
     */
    @Override
    public Map<String, String> defaultAppConfiguration() {
        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.DEFAULT_APP_CONFIGURATION);
        return CollectionUtil.emptyIfNull(dictDataList).stream()
                .collect(Collectors.toMap(DictDataDO::getValue, DictDataDO::getRemark));
    }

    /**
     * 获取单词本模板列表
     *
     * @return 获取单词本模板列表
     */
    @Override
    public List<String> getWordbookTemplateIdList() {
        List<DictDataDO> dictDataList = getDictionaryList(AppConstants.WORD_BOOK_ID_TEMPLATE);
        return CollectionUtil.emptyIfNull(dictDataList)
                .stream()
                .map(DictDataDO::getValue)
                .collect(Collectors.toList());
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

    /**
     * 递归实现分类树
     *
     * @param categoryList 应用分类列表
     * @param parentCode   根节点Code
     * @return 应用分类列表
     */
    private static List<AppCategoryVO> categoryListToTree(List<AppCategoryVO> categoryList, String parentCode) {
        // 利用 stream 进行递归，尽可能的效率高
        return categoryList.stream().filter(category -> parentCode.equalsIgnoreCase(category.getParentCode())).filter(category -> !"ALL".equalsIgnoreCase(category.getCode())).peek(category -> category.setChildren(categoryListToTree(categoryList, category.getCode()))).collect(Collectors.toList());
    }

}
