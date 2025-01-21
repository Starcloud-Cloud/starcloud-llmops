package com.starcloud.ops.business.app.service.dict;

import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.limit.dto.AppLimitConfigDTO;

import java.util.List;
import java.util.Map;

/**
 * 应用引用字典服务的接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-04
 */
public interface AppDictionaryService {

    /**
     * 查询应用分类列表
     *
     * @param isRoot 是否只根节点数据
     * @return 应用分类列表
     */
    List<AppCategoryVO> categoryList(Boolean isRoot);

    /**
     * 查询应用分类树
     *
     * @return 应用分类树
     */
    List<AppCategoryVO> categoryTree();

    /**
     * 热门搜索应用市场应用名称列表
     *
     * @return 热门搜索应用市场应用名称列表
     */
    List<String> hotSearchMarketAppNameList();

    /**
     * 示例提示集合
     *
     * @return 示例提示集合
     */
    List<ImageMetaDTO> examplePrompt();

    /**
     * 获取应用限流兜底配置
     *
     * @return 应用限流兜底配置
     */
    List<AppLimitConfigDTO> appSystemLimitConfig();

    /**
     * 限流总开关
     *
     * @return 是否开启限流
     */
    Boolean appLimitSwitch();

    /**
     * 用户白名单，白名单之内的用户ID，不进行限流
     *
     * @return 用户白名单
     */
    List<String> appLimitUserWhiteList();

    /**
     * 查询应用分类树
     *
     * @return 应用分类树
     */
    List<AppCategoryVO> creativeSchemeCategoryTree();

    /**
     * 搜索应用模板名称，用于搜索应用市场应用名称
     *
     * @return 搜索应用模板名称
     */
    List<String> appTemplateAppNameList();

    /**
     * 获取应用默认配置
     *
     * @return 获取应用默认配置
     */
    Map<String, String> defaultAppConfiguration();

    /**
     * 获取单词本模板列表
     *
     * @return 获取单词本模板列表
     */
    List<String> getWordbookTemplateIdList();
}
