package com.starcloud.ops.business.app.service.dict;

import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;

import java.util.List;

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
     * @return 应用分类列表
     */
    List<AppCategoryVO> categories();


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
    AppPublishLimitRespVO appSystemLimit();
    
}
